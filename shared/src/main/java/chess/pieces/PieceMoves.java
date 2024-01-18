package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    public int isValidMove(ChessPosition myPosition, ChessPosition endPos, ChessBoard board);
    public static final int MAX_BOARD_INDEX = 7;
    public static final int NOT_VALID_SPOT = 0;
    public static final int NULL_SPOT = 1;
    public static final int OPPOSITE_COLOR_SPOT = 2;

}
