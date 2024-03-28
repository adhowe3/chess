package webSocketMessages.userCommands;

import chess.ChessGame;
public class JoinPlayer extends UserGameCommand{
    private ChessGame.TeamColor color;
    public JoinPlayer(String authToken, ChessGame.TeamColor color) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.color = color;
    }
}
