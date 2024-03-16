package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginUI {

    private Scanner scanner = new Scanner(System.in);

    public PreloginUI(){
        System.out.println(WHITE_KING + "Welcome to 240 chess. Type help to get started." + WHITE_KING);
    }

    public void getHelpCmd(){
        String input = "";
        while(!input.equals("help")){
            input = scanner.nextLine();
            if(!input.equals("help")) System.out.println("Not a recognized command");
        }
    }

    public void printPreloginUI() {
        System.out.println(SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE +"- to create an account");
        System.out.println(SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE +" - to play chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_WHITE +"- playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE+"help - " + SET_TEXT_COLOR_WHITE +"with possible commands");

    }


}
