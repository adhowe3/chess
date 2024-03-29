package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves extends PieceMoves{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            Collection<ChessMove> possibleMoves = new ArrayList<>();
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            boolean canPromote = false;
            ChessPiece.PieceType promoQueen = ChessPiece.PieceType.QUEEN;
            ChessPiece.PieceType promoRook =  ChessPiece.PieceType.ROOK;
            ChessPiece.PieceType promoKnight =  ChessPiece.PieceType.KNIGHT;
            ChessPiece.PieceType promoBishop =  ChessPiece.PieceType.BISHOP;

            // White adds to ROW, Black subtracts from ROW
            int newRow = row - 1;
            int doubleNewRow = row - 2;
            if(newRow == 1) canPromote = true;
            int startRow = 7;

        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                newRow = row + 1;
                doubleNewRow = row + 2;
                if(newRow == 8) canPromote = true;
                else canPromote = false;
                startRow = 2;
            }

            ChessPosition endPos1 = new ChessPosition(newRow, col);
        addPromotions(board, myPosition, possibleMoves, canPromote, promoQueen, promoRook, promoKnight, promoBishop, endPos1, NULL_SPOT);

        //check attack
            ChessPosition endPos2 = new ChessPosition(newRow, col+1);
        addPromotions(board, myPosition, possibleMoves, canPromote, promoQueen, promoRook, promoKnight, promoBishop, endPos2, OPPOSITE_COLOR_SPOT);

        //check attack 2
            ChessPosition endPos3 = new ChessPosition(newRow, col-1);
        addPromotions(board, myPosition, possibleMoves, canPromote, promoQueen, promoRook, promoKnight, promoBishop, endPos3, OPPOSITE_COLOR_SPOT);

        // check first move, is 2 spaces, row 2 is starting for white pawns
            if(myPosition.getRow() == startRow) {
                ChessPosition endPos4 = new ChessPosition(doubleNewRow, col);
                if (NULL_SPOT == this.isValidMove(myPosition, endPos4, board) &&
                    NULL_SPOT == this.isValidMove(myPosition, endPos1, board)) {
                    ChessMove move = new ChessMove(myPosition, endPos4, null);
                    possibleMoves.add(move);
                }
            }
            return possibleMoves;
    }

    private void addPromotions(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, boolean canPromote, ChessPiece.PieceType promoQueen, ChessPiece.PieceType promoRook, ChessPiece.PieceType promoKnight, ChessPiece.PieceType promoBishop, ChessPosition endPos1, int nullSpot) {
        if(nullSpot == this.isValidMove(myPosition, endPos1, board)){
            if(canPromote){
                ChessMove move1 = new ChessMove(myPosition, endPos1, promoQueen);
                possibleMoves.add(move1);
                ChessMove move2 = new ChessMove(myPosition, endPos1, promoRook);
                possibleMoves.add(move2);
                ChessMove move3 = new ChessMove(myPosition, endPos1, promoBishop);
                possibleMoves.add(move3);
                ChessMove move4 = new ChessMove(myPosition, endPos1, promoKnight);
                possibleMoves.add(move4);
            }
            else{
                ChessMove move = new ChessMove(myPosition, endPos1);
                possibleMoves.add(move);
            }
        }
    }

    //END CLASS
}
