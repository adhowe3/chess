package webSocketMessages.userCommands;

import chess.ChessGame;
public class JoinPlayerCommand extends UserGameCommand{
    private ChessGame.TeamColor color;
    public JoinPlayerCommand(String authToken, ChessGame.TeamColor color) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.color = color;
    }
}
