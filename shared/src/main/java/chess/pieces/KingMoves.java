package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements PieceMoves{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition endPos = new ChessPosition(row + 1, col);
        if(this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos);
            possibleMoves.add(move);
        }

        ChessPosition endPos1 = new ChessPosition(row, col + 1);
        if(this.isValidMove(myPosition, endPos1, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos1);
            possibleMoves.add(move);
        }

        ChessPosition endPos2 = new ChessPosition(row - 1, col);
        if(this.isValidMove(myPosition, endPos2, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos2);
            possibleMoves.add(move);
        }

        ChessPosition endPos3 = new ChessPosition(row, col - 1);
        if(this.isValidMove(myPosition, endPos3, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos3);
            possibleMoves.add(move);
        }

        ChessPosition endPos4 = new ChessPosition(row-1, col-1);
        if(this.isValidMove(myPosition, endPos4, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos4);
            possibleMoves.add(move);
        }

        ChessPosition endPos5 = new ChessPosition(row+1, col-1);
        if(this.isValidMove(myPosition, endPos5, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos5);
            possibleMoves.add(move);
        }

        ChessPosition endPos6 = new ChessPosition(row-1, col+1);
        if(this.isValidMove(myPosition, endPos6, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos6);
            possibleMoves.add(move);
        }

        ChessPosition endPos7 = new ChessPosition(row+1, col+1);
        if(this.isValidMove(myPosition, endPos7, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos7);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }

    @Override
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
        return NOT_VALID_SPOT;
    }

    //END CLASS
}
