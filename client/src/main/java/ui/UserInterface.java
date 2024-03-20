package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import server.ServerFacade;

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

    public void printPreLoginUI() throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE +"- to create an account");
        System.out.println(SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE +" - to play chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_WHITE +"- playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_WHITE +"- with possible commands");
    }

    void readPreLoginCmds() throws ResponseException {
        System.out.print("[LOGGED_OUT] >>> ");
        String userInput[] = readCommand();
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

    void readPostLoginCmds(){
        System.out.print("[LOGGED_IN] >>> ");
        String userInput[] = readCommand();
        switch(userInput[0]) {
            case ("create"):
                if (userInput.length > 1) {
                    try {
                        CreateGameResponse gameID = server.createGame(new CreateGameRequest(authToken, userInput[1]));
                        System.out.println(gameID.toString());
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                else System.out.println("Not enough arguments");
                break;
            case ("list"):
                try {
                    ListGamesResponse gamesList = server.listGames(authToken);
                    for(GameData game : gamesList.games()){
                        System.out.println(game.toString());
                    }
                } catch (ResponseException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case ("join"):
                JoinGameRequest joinReq = createJoinGameReq(userInput);
                if(joinReq != null) {
                    try {
                        server.joinGame(joinReq);
                        printChessBoardToTerminalWhite();
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
                        printChessBoardToTerminalWhite();
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
        System.out.println(userInput[0]);
    }

    private JoinGameRequest createJoinGameReq(String[] userIn){
        int gameID;
        String userColor = null;
        if(userIn.length > 1){
            try {
                 gameID = Integer.parseInt(userIn[1]);
            } catch (NumberFormatException e) {
                System.out.println("Not a valid gameID");
                return null;
            }
        }
        else{
            System.out.println("Not enough arguments");
            return null;
        }
        if(userIn.length > 2){
            userColor = userIn[2];
        }
        return new JoinGameRequest(authToken, userColor, gameID);
    }

    private JoinGameRequest createObserveReq(String[] userIn){
        int gameID;
        String userColor = null;
        if(userIn.length > 1){
            try {
                gameID = Integer.parseInt(userIn[1]);
            } catch (NumberFormatException e) {
                System.out.println("Not a valid gameID");
                return null;
            }
        }
        else{
            System.out.println("Not enough arguments");
            return null;
        }
        return new JoinGameRequest(authToken, userColor, gameID);
    }

    private String[] readCommand(){
        while(!scanner.hasNext()){}
        String input = scanner.nextLine();
        String inputArray[] = input.split("\\s+");
        return inputArray;
    }

    private void printChessBoardToTerminalWhite(){
        String spacing = "\u2001\u2005\u2006";
        String[] backwardLetters ={(" h"+spacing), (" g"+spacing), (" f"+spacing), (" e"+spacing), (" d"+spacing), (" c"+spacing), (" b"+spacing), " a\u2005"};
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 1 ");

        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + BLACK_ROOK);
        System.out.print(SET_BG_COLOR_RED + BLACK_KNIGHT);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + BLACK_BISHOP);
        System.out.print(SET_BG_COLOR_RED + BLACK_QUEEN);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + BLACK_KING);
        System.out.print(SET_BG_COLOR_RED + BLACK_BISHOP);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + BLACK_KNIGHT);
        System.out.print(SET_BG_COLOR_RED + BLACK_ROOK);
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 1 " + SET_BG_COLOR_DARK_GREY);

        System.out.print(SET_BG_COLOR_BLACK+" 2 ");

        // print the pawns
        String backgroundColor = SET_BG_COLOR_LIGHT_GREY;
        for(int i = 0; i < 8; i++){
            if((i % 2) >= 1) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + BLACK_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 2 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK+" 3 ");

        // print the middle board
        int counter = 0;
        for(int row = 0; row < 4; row++){
            for(int i = 0; i < 8; i++){
                counter++;
                if((counter % 2) == 1) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
                else backgroundColor = SET_BG_COLOR_RED;
                System.out.print(backgroundColor + EMPTY);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + (row + 3) + " " + SET_BG_COLOR_DARK_GREY);
            System.out.print(SET_BG_COLOR_BLACK + " " + (row+4) + " ");
        }

        //print the white pawns
        for(int i = 0; i < 8; i++){
            if((i % 2) == 0) backgroundColor = SET_BG_COLOR_LIGHT_GREY;
            else backgroundColor = SET_BG_COLOR_RED;
            System.out.print(backgroundColor + SET_TEXT_COLOR_WHITE + WHITE_PAWN);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " 7 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK +" 8 ");

        System.out.print(SET_BG_COLOR_LIGHT_GREY + WHITE_ROOK);
        System.out.print(SET_BG_COLOR_RED + WHITE_KNIGHT);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + WHITE_BISHOP);
        System.out.print(SET_BG_COLOR_RED + WHITE_QUEEN);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + WHITE_KING);
        System.out.print(SET_BG_COLOR_RED + WHITE_BISHOP);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + WHITE_KNIGHT);
        System.out.print(SET_BG_COLOR_RED + WHITE_ROOK);

        System.out.println(SET_BG_COLOR_BLACK + " 8 " + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);

        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
    }


}
