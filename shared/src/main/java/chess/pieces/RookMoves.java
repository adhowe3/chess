package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves implements PieceMoves{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row + i, col);
            if (this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT) {
                ChessMove move = new ChessMove(myPosition, endPos);
                possibleMoves.add(move);
            }
            if(this.isValidMove(myPosition, endPos, board) == OPPOSITE_COLOR_SPOT ||
                this.isValidMove(myPosition, endPos, board) == NOT_VALID_SPOT){
                // break out of loop once you reach edge of board or another piece
                break;
            }
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row - i, col);
            if (this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT) {
                ChessMove move = new ChessMove(myPosition, endPos);
                possibleMoves.add(move);
            }
            if(this.isValidMove(myPosition, endPos, board) == OPPOSITE_COLOR_SPOT ||
                    this.isValidMove(myPosition, endPos, board) == NOT_VALID_SPOT){
                // break out of loop once you reach edge of board or another piece
                break;
            }
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row, col+i);
            if (this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT) {
                ChessMove move = new ChessMove(myPosition, endPos);
                possibleMoves.add(move);
            }
            if(this.isValidMove(myPosition, endPos, board) == OPPOSITE_COLOR_SPOT ||
                    this.isValidMove(myPosition, endPos, board) == NOT_VALID_SPOT){
                // break out of loop once you reach edge of board or another piece
                break;
            }
        }

        for(int i = 1; i <= MAX_BOARD_INDEX + 1; i++) {
            ChessPosition endPos = new ChessPosition(row, col - i);
            if (this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT) {
                ChessMove move = new ChessMove(myPosition, endPos);
                possibleMoves.add(move);
            }
            if(this.isValidMove(myPosition, endPos, board) == OPPOSITE_COLOR_SPOT ||
                    this.isValidMove(myPosition, endPos, board) == NOT_VALID_SPOT){
                // break out of loop once you reach edge of board or another piece
                break;
            }
        }
        return possibleMoves;
    }

    @Override
    public int isValidMove(ChessPosition myPosition, ChessPosition endPos, ChessBoard board){
        if(endPos.getRow() <= MAX_BOARD_INDEX+1 && endPos.getColumn() <= MAX_BOARD_INDEX+1 &&
                endPos.getRow() > 0 && endPos.getColumn() > 0) {
            if(board.getPiece(endPos) == null) {
                return NULL_SPOT;
            }
            else if(board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos).getTeamColor()) {
                return OPPOSITE_COLOR_SPOT;
            }
        }
        return NOT_VALID_SPOT;
    }
}
