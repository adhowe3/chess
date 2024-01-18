package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    public static final int MAX_BOARD_INDEX = 7;

}
