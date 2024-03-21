package ui;

import exception.ResponseException;
import model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import server.ServerFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UserInterface {

    private Scanner scanner = new Scanner(System.in);
    private ServerFacade server;

    private boolean isLoggedin = false;
    private boolean exit = false;
    private String authToken;

    public UserInterface(String serverUrl){
        server = new ServerFacade(serverUrl);
    }

    public void runClient() throws ResponseException {
        System.out.println(SET_TEXT_COLOR_WHITE + WHITE_KING + "Welcome to 240 chess. Type help to get started." + WHITE_KING);
        readPreLoginCmds();
        while(!exit){
            if(!isLoggedin){
                readPreLoginCmds();
            }
            else{
                readPostLoginCmds();
            }
        }
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
                        printBothChessBoards();
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case ("observe"):
                JoinGameRequest observeReq = createObserveReq(userInput);
                if(observeReq != null) {
                    try {
                        server.joinGame(observeReq);
                        printBothChessBoards();
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
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

    private Map<Integer, Integer> gameIDMap = new HashMap<>();

    private void listGames(ListGamesResponse gamesList){
        int listNum = 1;
        gameIDMap.clear();
        for(GameData game : gamesList.games()){
            gameIDMap.put(listNum, game.getGameID());
            System.out.println(SET_TEXT_COLOR_WHITE + listNum +".) " + "Game_Name: " + SET_TEXT_COLOR_GREEN + game.getGameName() +
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
        gameID = gameIDMap.get(gameListNum);
        return new JoinGameRequest(authToken, null, gameID);
    }

    private String[] readCommand(){
        while(!scanner.hasNext()){}
        String input = scanner.nextLine();
        return input.split("\\s+");
    }

    private void printBothChessBoards(){
        printChessBoardToTerminalWhite();
        System.out.println();
        printChessBoardToTerminalBlack();
    }

    private void printChessBoardToTerminalWhite(){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        String[] backwardLetters ={(" h"+spacing), (" g"+spacing), (" f"+spacing), (" e"+spacing), (" d"+spacing), (" c"+spacing), (" b"+spacing), " a\u2005"};
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 1 ");

        String[] backRowPiecesBlack = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};

        for (String backRowPiece : backRowPiecesBlack) {
            System.out.print(backgroundColor + SET_TEXT_COLOR_BLACK + backRowPiece);
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 1 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK+" 2 ");

        // print the pawns
        for(int i = 0; i < 8; i++){
            if((i % 2) >= 1) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + SET_TEXT_COLOR_BLACK +  BLACK_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 2 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK+" 3 ");

        // print the middle board
        backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        for(int row = 0; row < 4; row++){
            for(int i = 0; i < 8; i++){
                System.out.print(backgroundColor + EMPTY);
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + (row + 3) + " " + SET_BG_COLOR_DARK_GREY);
            System.out.print(SET_BG_COLOR_BLACK + " " + (row+4) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }

        //print the white pawns
        for(int i = 0; i < 8; i++){
            if((i % 2) == 0) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + SET_TEXT_COLOR_WHITE + WHITE_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 7 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK +" 8 ");

        // print the white backrow
        String[] backRowPiecesWhite = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        for (String backRowPiece : backRowPiecesWhite) {
            System.out.print(backgroundColor + SET_TEXT_COLOR_WHITE + backRowPiece);
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.println(SET_BG_COLOR_BLACK + " 8 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);

        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
    }

    private void printChessBoardToTerminalBlack(){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        String[] forwardLetters ={(" a"+spacing), (" b"+spacing), (" c"+spacing), (" d"+spacing), (" e"+spacing), (" f"+spacing), (" g"+spacing), " h\u2005"};
        String[] backRowPiecesBlack = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        String[] backRowPiecesWhite = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 8 ");

        for (String backRowPiece : backRowPiecesWhite) {
            System.out.print(backgroundColor + SET_TEXT_COLOR_WHITE + backRowPiece);
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 8 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK+" 7 ");

        // print the white pawns
        for(int i = 0; i < 8; i++){
            if((i % 2) >= 1) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + SET_TEXT_COLOR_WHITE +  WHITE_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 7 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK+" 6 ");

        // print the middle board
        backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        for(int row = 0; row < 4; row++){
            for(int i = 0; i < 8; i++){
                System.out.print(backgroundColor + EMPTY);
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + (6-row) + " " + SET_BG_COLOR_DARK_GREY);
            System.out.print(SET_BG_COLOR_BLACK + " " + (5-row) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }

        //print the black pawns
        for(int i = 0; i < 8; i++){
            if((i % 2) == 0) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + SET_TEXT_COLOR_BLACK + BLACK_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 2 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK +" 1 ");

        // print the white backrow
        for (String backRowPiece : backRowPiecesBlack) {
            System.out.print(backgroundColor + SET_TEXT_COLOR_BLACK + backRowPiece);
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 1 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);

        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
    }

    private String flipBgColor(String currColor){
        if(currColor.equals(SET_BG_COLOR_LIGHT_GREY)){
            return SET_BG_COLOR_RED;
        }
        if(currColor.equals(SET_BG_COLOR_RED)){
            return SET_BG_COLOR_LIGHT_GREY;
        }
        // error case
        return SET_BG_COLOR_BLUE;
    }


}
