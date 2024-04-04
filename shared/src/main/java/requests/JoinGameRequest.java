package requests;

import chess.ChessGame;

import java.util.Objects;

public class JoinGameRequest {
    private String authorization;
    String playerColor;
    private Integer gameID;

    private Integer gameIndex;

    public JoinGameRequest(String authorization, String playerColor, Integer gameID, Integer gameIndex) {
        this.authorization = authorization;
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.gameIndex = gameIndex;
    }
    public JoinGameRequest(int gameID){
        this.gameID = gameID;
    }
    public String getPlayerColor() {
        return playerColor;
    }

    public Integer getgameID() {
        return gameID;
    }

    public Integer getGameIndex() { return this.gameIndex;}

    public String getAuthorization(){
        return authorization;
    }

    public void setPlayerColor(String color){
        if(color.equals("WHITE") || color.equals("BLACK")){
            this.playerColor = color;
        }
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

}
