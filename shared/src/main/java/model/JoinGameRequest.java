package model;

import java.util.Objects;

public class JoinGameRequest {
    private String playerColor;
    private int GameID;
    private String authorization;

    public JoinGameRequest(String playerColor, int gameID, String authorization) {
        this.playerColor = playerColor;
        this.GameID = gameID;
        this.authorization = authorization;
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

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinGameRequest that = (JoinGameRequest) o;
        return GameID == that.GameID && Objects.equals(playerColor, that.playerColor) && Objects.equals(authorization, that.authorization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor, GameID, authorization);
    }
}
