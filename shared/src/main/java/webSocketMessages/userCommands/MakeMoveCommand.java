package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;
    public MakeMoveCommand(String authToken, ChessMove move, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.move = move;
        this.gameID = gameID;
    }

    public ChessMove getMove(){
        return move;
    }


}
