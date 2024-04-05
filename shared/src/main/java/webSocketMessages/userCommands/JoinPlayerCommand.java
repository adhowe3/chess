package webSocketMessages.userCommands;

import chess.ChessGame;
public class JoinPlayerCommand extends UserGameCommand{
    private ChessGame.TeamColor playerColor;
    public JoinPlayerCommand(String authToken, ChessGame.TeamColor playerColor, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getColor(){
        return playerColor;
    }

    public String getColorStr(){
        if(playerColor == null) return "observer";
        if(playerColor.equals(ChessGame.TeamColor.WHITE))
            return "WHITE";
        else if(playerColor.equals(ChessGame.TeamColor.BLACK))
            return "BLACK";
        return "observer";
    }
}
