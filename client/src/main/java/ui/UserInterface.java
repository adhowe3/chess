package ui;

import chess.*;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import server.ServerFacade;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class UserInterface implements NotificationHandler {

    private Scanner scanner = new Scanner(System.in);
    private ServerFacade server;

    private boolean isLoggedin = false;
    private boolean isInGamePlay = false;

    private ChessGame.TeamColor playerColor;
    private boolean exit = false;
    private boolean isObserver = false;
    private String authToken;
    private Integer gameID;

    private GameData gameData;
    private WebSocketFacade wsFacade;

    private ChessBoardPrinter boardPrinter = new ChessBoardPrinter();

    private List<GameData> gameDataList = new ArrayList<GameData>();
    public UserInterface(String serverUrl) throws ResponseException {
        wsFacade = new WebSocketFacade(serverUrl, this);
        server = new ServerFacade(serverUrl);
    }

    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch(serverMessage.getServerMessageType()){
            case NOTIFICATION :
                System.out.println();
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(SET_TEXT_COLOR_YELLOW + notification.getMessage() + SET_TEXT_COLOR_WHITE);
                printUserTerminal();
                break;
            case LOAD_GAME:
                System.out.println();
                LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                boardPrinter.printChessBoard(loadGame.getGame().getBoard(), playerColor);
                printUserTerminal();
                break;
            case ERROR:
                System.out.println();
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + SET_TEXT_COLOR_WHITE);
                printUserTerminal();
                break;
        }
    }

    public void runClient() throws ResponseException {
        System.out.println(SET_TEXT_COLOR_WHITE + WHITE_KING + "Welcome to 240 chess. Type help to get started." + WHITE_KING);
        readPreLoginCmds();
        boolean firstTime = true;
        while(!exit){
            if(isInGamePlay){
                if(firstTime){
                    printGameplayInit();
                    firstTime = false;
                }
                readGamePlayCmds();
            }
            else if(!isLoggedin){
                readPreLoginCmds();
            }
            else if(isLoggedin){
                readPostLoginCmds();
            }
        }
    }

    private void printUserTerminal(){
        if(isInGamePlay && !isObserver){
            System.out.print("[PLAYING_GAME] >>> ");
        }
        else if(isInGamePlay && isObserver){
            System.out.print("[OBSERVING_GAME] >>> ");
        }
        else if(isLoggedin){
            System.out.print("[LOGGED_IN] >>> ");
        }
        else if(!isLoggedin){
            System.out.print("[LOGGED_OUT] >>> ");
        }
    }

    private String[] readCommand(){
        while(!scanner.hasNext()){
        }
        String input = scanner.nextLine();
        return input.split("\\s+");
    }

    private void printGamePlayUI(){
        System.out.println(SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_WHITE +"- with possible commands");
        System.out.println(SET_TEXT_COLOR_BLUE+"redraw " + SET_TEXT_COLOR_WHITE +" -redraws the board");
        System.out.println(SET_TEXT_COLOR_BLUE+"leave" + SET_TEXT_COLOR_WHITE +" - removes yourself from the game");
        System.out.println(SET_TEXT_COLOR_BLUE+"move [FROM] [TO] " + SET_TEXT_COLOR_WHITE +"- make a move in the game ex. b2 f3");
        System.out.println(SET_TEXT_COLOR_BLUE+"resign " + SET_TEXT_COLOR_WHITE +"- forfeit the game");
        System.out.println(SET_TEXT_COLOR_BLUE+"highlight legal moves [PIECE LOCATION]" + SET_TEXT_COLOR_WHITE +"- display the possible moves for give piece");
        System.out.println();
    }

    public void readGamePlayCmds() {
        printUserTerminal();
        String userInput[] = readCommand();
        switch(userInput[0]) {
            case ("help"):
                try{
                    printGamePlayUI();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case ("redraw"):
                try{
                    ArrayList<GameData> lsGames = server.listGames(authToken).games();
                    for(GameData game : lsGames){
                        if(game.getGameID() == gameID){
                            this.gameData = game;
                            boardPrinter.printChessBoard(gameData.getGame().getBoard(), playerColor);
                        }
                    }
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case ("leave"):
                try{
                    wsFacade.leaveChessGame(authToken, gameID);
                    System.out.println("Leaving game");
                    isInGamePlay = false;
                    isObserver = false;
                }catch(ResponseException e){
                    System.out.println(e.getMessage());
                }
                break;
            case ("move"):
                try{
                    ChessMove move = getMoveFromCommand(userInput);
                    wsFacade.makeMove(authToken, move, gameID);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case("resign"):
                try{
                    wsFacade.resign(authToken, gameID);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case("highlight"):
                if(userInput.length > 1){
                    ChessPosition pos = getPositionFromString(userInput[1]);
                    if(pos != null){
                        try {
                            ArrayList<GameData> lsGames = server.listGames(authToken).games();
                            for (GameData game : lsGames) {
                                if (game.getGameID() == gameID) {
                                    this.gameData = game;
                                    Collection<ChessMove> validMoves = this.gameData.getGame().validMoves(pos);
                                    boardPrinter.printChessBoardHighlight(gameData.getGame().getBoard(), validMoves, playerColor);
                                }
                            }
                        }catch(ResponseException e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
                break;
            default:
                System.out.println("Not a recognized command");
                break;
        }
    }

    private ChessMove getMoveFromCommand(String[] userInput){
        if(userInput.length >= 3){
            String from = userInput[1];
            String to = userInput[2];
            System.out.println("from: " + userInput[1]);
            System.out.println("to: " + userInput[2]);
            return new ChessMove(getPositionFromString(from), getPositionFromString(to));
        }
        else return null;
    }

    private ChessPosition getPositionFromString(String str){
        if(str.length() < 2) return null;
        str = str.toLowerCase(Locale.ROOT);
        int col = 8 - (str.charAt(0) - 'a');
        int row = str.charAt(1) - '0';
        if(row > 8 || col > 8) return null;
        System.out.println("col: " + col);
        System.out.println("row: " + row);
        return new ChessPosition(row, col);
    }

    private void printGameplayInit(){
        String textString;
        if(playerColor == null) textString = "observing ";
        else textString = ("playing as ");
        System.out.println(SET_TEXT_COLOR_WHITE + BLACK_KING + "You are now " + textString
                + ". Type help to get started" + WHITE_KING);
    }

    private void printPreLoginUI() throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE +"- to create an account");
        System.out.println(SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE +" - to play chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_WHITE +"- playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_WHITE +"- with possible commands");
    }

    private void readPreLoginCmds() throws ResponseException {
        System.out.print("[LOGGED_OUT] >>> ");
        String[] userInput = readCommand();
        switch(userInput[0]) {
            case ("register"):
                if (userInput.length > 3) {
                    UserData usr = new UserData(userInput[1], userInput[2], userInput[3]);
                    try {
                        authToken = server.registerUser(usr).getAuthToken();
                        isLoggedin = true;
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("Not enough arguments");
                }
                break;
            case ("login"):
                if(userInput.length > 2){
                    try{
                        authToken = server.loginUser(new LoginRequest(userInput[1], userInput[2])).getAuthToken();
                        isLoggedin = true;
                    }catch(ResponseException e){
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("not enough arguments");
                }
                break;
            case ("quit"):
                exit = true;
                break;
            case ("help"):
                printPreLoginUI();
                break;
            default:
                System.out.println("Not a recognized command");
                break;
        }
    }

    void printPostLoginUI(){
        System.out.println(SET_TEXT_COLOR_BLUE+"create <NAME>" + SET_TEXT_COLOR_WHITE +" - a game");
        System.out.println(SET_TEXT_COLOR_BLUE+"list" + SET_TEXT_COLOR_WHITE +" - games");
        System.out.println(SET_TEXT_COLOR_BLUE+"join <ID> [WHITE|BLACK|<empty>] " + SET_TEXT_COLOR_WHITE +"- a game");
        System.out.println(SET_TEXT_COLOR_BLUE+"observe <ID> " + SET_TEXT_COLOR_WHITE +"- a game");
        System.out.println(SET_TEXT_COLOR_BLUE+"logout " + SET_TEXT_COLOR_WHITE +"- when you are done");
        System.out.println(SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_WHITE +"- playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_WHITE +"- with possible commands");
        System.out.println();
    }

    private void readPostLoginCmds(){
        System.out.print("[LOGGED_IN] >>> ");
        String userInput[] = readCommand();
        switch(userInput[0]) {
            case ("create"):
                if (userInput.length > 1) {
                    try {
                        CreateGameResponse gameID = server.createGame(new CreateGameRequest(authToken, userInput[1]));
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else System.out.println("Not enough arguments");
                break;
            case ("list"):
                try {
                    ListGamesResponse gamesList = server.listGames(authToken);
                    gameDataList = gamesList.games();
                    listGames(gamesList);
                } catch (ResponseException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case ("join"):
                JoinGameRequest joinReq = createJoinGameReq(userInput);
                if(joinReq != null) {
                    try {
                        server.joinGame(joinReq);
                        playerColor = strToTeamColor(joinReq.getPlayerColor());
                        gameID = joinReq.getgameID();
                        this.wsFacade = new WebSocketFacade(server.getServerUrl(), this);
                        this.wsFacade.joinChessGame(joinReq.getAuthorization(), strToTeamColor(joinReq.getPlayerColor()), joinReq.getgameID());
                        isInGamePlay = true;
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("Not a valid request");
                }
                break;
            case ("observe"):
                JoinGameRequest observeReq = createObserveReq(userInput);
                if(observeReq != null) {
                    try {
                        server.joinGame(observeReq);
                        isInGamePlay = true;
                        isObserver = true;
                        this.wsFacade = new WebSocketFacade(server.getServerUrl(), this);
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("Not a valid request");
                }
                break;
            case("logout"):
                try{
                    server.logoutUser(authToken);
                    isLoggedin = false;
                }catch(ResponseException e){
                    System.out.println(e.getMessage());
                }
                break;
            case("quit"):
                exit = true;
                break;
            case("help"):
                printPostLoginUI();
                break;
            default:
                System.out.println("Not a recognized command");
                break;
        }
    }

    private ChessGame.TeamColor strToTeamColor(String color){
        if(color == null) return null;
        if(color.equals("BLACK")){
            return ChessGame.TeamColor.BLACK;
        }
        else return ChessGame.TeamColor.WHITE;
    }

    private Map<Integer, Integer> gameIDMap = new HashMap<>();

    private void listGames(ListGamesResponse gamesList){
        int listNum = 1;
        gameIDMap.clear();
        for(GameData game : gamesList.games()){
            gameIDMap.put(listNum, game.getGameID());
            System.out.println(SET_TEXT_COLOR_WHITE + listNum +".) " +"gameID: " + game.getGameID() + "Game_Name: " + SET_TEXT_COLOR_GREEN + game.getGameName() +
                    SET_TEXT_COLOR_WHITE + " White_Player: " + SET_TEXT_COLOR_GREEN + game.getWhiteUsername()
                    + SET_TEXT_COLOR_WHITE +" Black_Player: " + SET_TEXT_COLOR_GREEN + game.getBlackUsername() + SET_TEXT_COLOR_WHITE);
            listNum++;
        }
    }

    private JoinGameRequest createJoinGameReq(String[] userIn){
        String userColor = null;
        JoinGameRequest req = createObserveReq(userIn);
        if(req == null){
            return null;
        }
        if(userIn.length > 2){
            if(userIn[2].equals("WHITE") || userIn[2].equals("BLACK"))
                userColor = userIn[2];
        }
        req.setPlayerColor(userColor);
        return req;
    }

    private JoinGameRequest createObserveReq(String[] userIn){
        int gameListNum;
        int gameID;
        if(userIn.length > 1){
            try {
                gameListNum = Integer.parseInt(userIn[1]);
            } catch (NumberFormatException e) {
                System.out.println("Not a valid gameID");
                return null;
            }
        }
        else{
            System.out.println("Not enough arguments");
            return null;
        }
        // the numbers in the list correspond to a game ID
        if(gameListNum > gameIDMap.size()) return null;
        gameID = gameIDMap.get(gameListNum);
        return new JoinGameRequest(authToken, null, gameID, gameListNum);
    }

}
