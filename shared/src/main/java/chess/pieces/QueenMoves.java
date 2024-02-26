package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves extends PieceMoves{


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        BishopMoves bishop = new BishopMoves();
        RookMoves rook = new RookMoves();

        possibleMoves.addAll(bishop.pieceMoves(board, myPosition));
        possibleMoves.addAll(rook.pieceMoves(board, myPosition));

        return possibleMoves;
    }


}
