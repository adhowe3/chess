package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements PieceMoves{


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        BishopMoves bishop = new BishopMoves();
        RookMoves rook = new RookMoves();

        possibleMoves.addAll(bishop.pieceMoves(board, myPosition));
        possibleMoves.addAll(rook.pieceMoves(board, myPosition));

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
