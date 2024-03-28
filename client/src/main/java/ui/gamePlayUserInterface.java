package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.List;

public class gamePlayUserInterface {

    GameData gameData;
    ChessGame chessGame;
    String playerColor;

    public gamePlayUserInterface(Integer gameIndex, List<GameData> gameDatalist, String playerColor){
        this.gameData = gameDatalist.get(gameIndex - 1);
        this.chessGame = gameData.getGame();
        this.playerColor = playerColor;
        this.runUI();
    }
    public void runUI(){
        System.out.println("running gamePlayUI");

    }

}
