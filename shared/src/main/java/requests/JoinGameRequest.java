package requests;

import java.util.Objects;

public class JoinGameRequest {
    private String authorization;
    private String playerColor;
    private Integer gameID;

    public JoinGameRequest(String authorization, String playerColor, Integer gameID) {
        this.authorization = authorization;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
    public JoinGameRequest(int gameID){
        this.gameID = gameID;
    }
    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public Integer getgameID() {
        return gameID;
    }

    public String getAuthorization(){
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
