package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinObserverCommand extends UserGameCommand{
    public JoinObserverCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
    }

}
