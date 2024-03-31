package ui;

import chess.*;
import dataAccess.GameDAO;
import dataAccess.MySQLGameDAO;
import exception.ResponseException;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import server.ServerFacade;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLUE;

public class gamePlayUserInterface {

    private GameData gameData;
    private ChessGame chessGame;
    private String playerColor;
    private String auth;
    private Boolean isPlaying = true;
    private ServerFacade server;

    private Scanner scanner = new Scanner(System.in);

    public gamePlayUserInterface(ServerFacade server, JoinGameRequest joinReq) throws ResponseException {
        this.server = server;
        this.playerColor = joinReq.getPlayerColor();
        this.auth = joinReq.getAuthorization();
        ListGamesResponse listResponse = server.listGames(auth);
        this.gameData = listResponse.games().get(joinReq.getGameIndex() - 1);
        this.chessGame = gameData.getGame();
        this.runUI();
    }
    public void runUI() throws ResponseException {
        initalPrintScreen();
        if(playerColor == null){
//            sendJoinObserver();
            printChessBoardToTerminalWhite();
        }
        else if(playerColor.equals("BLACK")){
            sendJoinPlayer();
            printChessBoardToTerminalBlack();
        }
        else{
            sendJoinPlayer();
            printChessBoardToTerminalWhite();
        }
        while(isPlaying){
            readGamePlayCmds();
        }
    }

    private void initalPrintScreen(){
        String textString;
        if(playerColor == null) textString = "observing ";
        else textString = ("playing as " + playerColor + " in ");
        System.out.println(SET_TEXT_COLOR_WHITE + BLACK_KING + "You are now " + textString
                + "game: " + gameData.getGameName() + ". Type help to get started" + WHITE_KING);
    }

//    private sendJoinPlayer(){
//
//    }

    private String[] readCommand(){
        while(!scanner.hasNext()){}
        String input = scanner.nextLine();
        return input.split("\\s+");
    }

    private void readGamePlayCmds() throws ResponseException {
        System.out.print("[PLAYING_GAME] >>> ");
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
                    redraw();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case ("leave"):
                    isPlaying = false;
                    System.out.println("Leaving game: " + gameData.getGameName());
                break;
            case ("move"):
                try{
                    ChessMove move = getMoveFromCommand(userInput);
                    makeMove(move);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                break;
            case("resign"):

                break;
            case("highlight"):
                break;
            default:
                System.out.println("Not a recognized command");
                break;
        }
    }

    private ChessMove getMoveFromCommand(String[] userInput){
        if(userInput.length > 3){
            String from = userInput[1];
            String to = userInput[2];
            return new ChessMove(getPositionFromString(from), getPositionFromString(to));
        }
        else return null;
    }

    private ChessPosition getPositionFromString(String str){
        if(str.length() < 2) return null;
        int col = str.charAt(0) - 'a';
        int row = str.charAt(1);
        return new ChessPosition(row, col);
    }

    private void makeMove(ChessMove move){
        //FIX ME
    }

    private void redraw() throws ResponseException {
        // update current gameData with database gameData
        ListGamesResponse ls = server.listGames(auth);
        for(GameData gameData : ls.games()){
            if(gameData.getGameID() == this.gameData.getGameID()){
                this.gameData = gameData;
            }
        }
        if(playerColor.equals("BLACK")) printChessBoardToTerminalBlack();
        else printChessBoardToTerminalWhite();
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

    // printing board functions
    private void printSquareToTerminal(String bgColor, ChessPiece chessPiece){
        String textColor = SET_TEXT_COLOR_WHITE;
        String pieceString;
        if(chessPiece == null) pieceString = EMPTY;
        else if(chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            textColor = SET_TEXT_COLOR_BLACK;
            pieceString = getPieceString(chessPiece, BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT, BLACK_PAWN);
        }
        else{
            pieceString = getPieceString(chessPiece, WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_PAWN);
        }
        System.out.print(bgColor + textColor +  pieceString);
    }

    private String getPieceString(ChessPiece chessPiece, String blackKing, String blackQueen, String blackRook, String blackBishop, String blackKnight, String blackPawn) {
        String pieceString;
        if(chessPiece == null) return EMPTY;
        switch(chessPiece.getPieceType()){
            case KING -> pieceString = blackKing;
            case QUEEN -> pieceString = blackQueen;
            case ROOK -> pieceString = blackRook;
            case BISHOP -> pieceString = blackBishop;
            case KNIGHT -> pieceString = blackKnight;
            case PAWN -> pieceString = blackPawn;
            default -> pieceString = EMPTY;
        }
        return pieceString;
    }

    private void printChessBoardToTerminalBlack(){
        ChessBoard board = this.chessGame.getBoard();
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        String[] backwardLetters ={(" h"+spacing), (" g"+spacing), (" f"+spacing), (" e"+spacing), (" d"+spacing), (" c"+spacing), (" b"+spacing), " a\u2005"};
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 1 ");

        for(int row = 1; row < 9; row++){
            for(int col = 1; col < 9; col++){
                printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row < 8) System.out.print(SET_BG_COLOR_BLACK+ " " + (row+1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY  + SET_BG_COLOR_DARK_GREY);
    }

    private void printChessBoardToTerminalWhite(){
        ChessBoard board = this.chessGame.getBoard();
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        String[] forwardLetters ={(" a"+spacing), (" b"+spacing), (" c"+spacing), (" d"+spacing), (" e"+spacing), (" f"+spacing), (" g"+spacing), " h\u2005"};

        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 8 ");

        for(int row = 8; row >= 1; row--){
            for(int col = 8; col >= 1; col--){
                printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row > 1) System.out.print(SET_BG_COLOR_BLACK+ " " + (row-1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
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



    @Override
    public String toString() {
        return "gamePlayUserInterface{" +
                "gameData=" + gameData +
                ", playerColor='" + playerColor + '\'' +
                ", auth='" + auth + '\'' +
                '}';
    }
}
