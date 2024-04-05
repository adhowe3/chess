package webSocketMessages.serverMessages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage{

    private ChessGame game;
    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
