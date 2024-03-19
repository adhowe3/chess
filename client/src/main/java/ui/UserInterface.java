package ui;

import exception.ResponseException;
import model.*;
import requests.LoginRequest;
import requests.LogoutRequest;
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
//                if (userInput.length > 2) {
//                    GameData game = new GameData(userInput[1]);
//                    try {
//                        authToken = server.registerUser(usr).getAuthToken();
//                    } catch (ResponseException e) {
//                        System.out.println(e.getMessage());
//                    }
//                    isLoggedin = true;
//                    System.out.print("[LOGGED_IN] >>> ");
//                    printPostLoginUI();
//                }
                break;
            case ("list"):

                break;
            case ("join"):
                exit = true;
                break;
            case ("observe"):
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

    private String[] readCommand(){
        while(!scanner.hasNext()){}
        String input = scanner.nextLine();
        String inputArray[] = input.split("\\s+");
        return inputArray;
    }


}
