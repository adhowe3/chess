package ui;

import exception.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginUI {

    private Scanner scanner = new Scanner(System.in);
    private ServerFacade server;

    public PreloginUI(String serverUrl){
        server = new ServerFacade(serverUrl);
        System.out.println(SET_TEXT_COLOR_WHITE + WHITE_KING + "Welcome to 240 chess. Type help to get started." + WHITE_KING);
        System.out.print("[LOGGED_OUT] >>> ");
    }

    public void getHelpCmd(){
        String input = "";
        while(!input.equals("help")){
            input = scanner.nextLine();
            if(!input.equals("help")) System.out.println("Not a recognized command");
        }
    }

    public void printPreloginUI() throws ResponseException {
        System.out.println(SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE +"- to create an account");
        System.out.println(SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE +" - to play chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_WHITE +"- playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_WHITE +"- with possible commands");
        System.out.print("[LOGGED_OUT] >>> ");
        String userInput[] = readCommand();
        switch(userInput[0]){
            case("register"):
                if(userInput.length > 3){
                    UserData usr = new UserData(userInput[1], userInput[2], userInput[3]);
                    try{
                        server.registerUser(usr);
                    }catch(ResponseException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case("login"):
                break;
            case("quit"):
                // do nothing and exit program
                break;
            case("help"):
                printPreloginUI();
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
