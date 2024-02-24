package model;

public class JoinGameRequest {
    private String playerColor;
    private int GameID;

    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.GameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return GameID;
    }

    public void setGameID(int gameID) {
        GameID = gameID;
    }
}
