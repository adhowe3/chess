package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;
    public MakeMoveCommand(String authToken, ChessMove move, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
        this.gameID = gameID;
    }

    public ChessMove getMove(){
        return move;
    }


}
