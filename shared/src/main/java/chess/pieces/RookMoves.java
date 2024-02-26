package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves extends PieceMoves{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row + i, col);
            if (addIfValidSpot(board, myPosition, possibleMoves, endPos)) break;
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row - i, col);
            if (addIfValidSpot(board, myPosition, possibleMoves, endPos)) break;
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row, col+i);
            if (addIfValidSpot(board, myPosition, possibleMoves, endPos)) break;
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row, col - i);
            if (addIfValidSpot(board, myPosition, possibleMoves, endPos)) break;
        }
        return possibleMoves;
    }

    private boolean addIfValidSpot(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, ChessPosition endPos) {
        if (this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT) {
            ChessMove move = new ChessMove(myPosition, endPos);
            possibleMoves.add(move);
        }
        // break out of loop once you reach edge of board or another piece
        return this.isValidMove(myPosition, endPos, board) == OPPOSITE_COLOR_SPOT ||
                this.isValidMove(myPosition, endPos, board) == NOT_VALID_SPOT;
    }

}
