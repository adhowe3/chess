package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends PieceMoves{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Case 1
        ChessPosition endPos = new ChessPosition(row+1, col+2);
        if(this.isValidMove(myPosition, endPos, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos);
            possibleMoves.add(move);
        }

        // Case 2
        ChessPosition endPos1 = new ChessPosition(row+1, col-2);
        if(this.isValidMove(myPosition, endPos1, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos1);
            possibleMoves.add(move);
        }

        // Case 3
        ChessPosition endPos2 = new ChessPosition(row+2, col+1);
        if(this.isValidMove(myPosition, endPos2, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos2);
            possibleMoves.add(move);
        }

        // Case 4
        ChessPosition endPos3 = new ChessPosition(row+2, col-1);
        if(this.isValidMove(myPosition, endPos3, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos3);
            possibleMoves.add(move);
        }

        // Case 5
        ChessPosition endPos4 = new ChessPosition(row-1, col+2);
        if(this.isValidMove(myPosition, endPos4, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos4);
            possibleMoves.add(move);
        }

        // Case 6
        ChessPosition endPos5 = new ChessPosition(row-1, col-2);
        if(this.isValidMove(myPosition, endPos5, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos5);
            possibleMoves.add(move);
        }

        // Case 7
        ChessPosition endPos6 = new ChessPosition(row-2, col+1);
        if(this.isValidMove(myPosition, endPos6, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos6);
            possibleMoves.add(move);
        }

        // Case 8
        ChessPosition endPos7 = new ChessPosition(row-2, col-1);
        if(this.isValidMove(myPosition, endPos7, board) != NOT_VALID_SPOT){
            ChessMove move = new ChessMove(myPosition, endPos7);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }

} // END CLASS
