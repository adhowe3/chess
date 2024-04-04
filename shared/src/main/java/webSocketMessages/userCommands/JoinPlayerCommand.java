package webSocketMessages.userCommands;

import chess.ChessGame;
public class JoinPlayerCommand extends UserGameCommand{
    private ChessGame.TeamColor color;
    public JoinPlayerCommand(String authToken, ChessGame.TeamColor color) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.color = color;
    }

    public ChessGame.TeamColor getColor(){
        return color;
    }

    public String getColorStr(){
        if(color == null) return "observer";
        if(color.equals(ChessGame.TeamColor.WHITE))
            return "WHITE";
        else return "BLACK";
    }
}
