package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public abstract class PieceMoves {
    abstract public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    public int isValidMove(ChessPosition myPosition, ChessPosition endPos, ChessBoard board){
        // check that it is inside the board
        if(endPos.getRow() <= MAX_BOARD_INDEX+1 && endPos.getColumn() <= MAX_BOARD_INDEX+1 &&
                endPos.getRow() > 0 && endPos.getColumn() > 0) {
            if(board.getPiece(endPos) == null) {
                return NULL_SPOT;
            }
            else if(board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos).getTeamColor()) {
                return OPPOSITE_COLOR_SPOT;
            }
        }
        return 0;
    }
    public static final int MAX_BOARD_INDEX = 7;
    public static final int NOT_VALID_SPOT = 0;
    public static final int NULL_SPOT = 1;
    public static final int OPPOSITE_COLOR_SPOT = 2;

}
