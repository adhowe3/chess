package chess;

import chess.pieces.BishopMoves;
import chess.pieces.KingMoves;
import chess.pieces.KnightMoves;
import chess.pieces.PawnMoves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CancellationException;

import static chess.ChessPiece.PieceType.PAWN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor pieceColor;
    public static final int MAX_BOARD_INDEX = 7;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        switch (myPiece.getPieceType()) {
            case KING:
                KingMoves kingMoves = new KingMoves();
                return kingMoves.pieceMoves(board, myPosition);
            case QUEEN:
                break;
            case ROOK:
                break;
            case BISHOP:
                BishopMoves bishopMoves = new BishopMoves();
                return bishopMoves.pieceMoves(board, myPosition);
            case KNIGHT:
                KnightMoves knightMoves = new KnightMoves();
                return knightMoves.pieceMoves(board, myPosition);
            case PAWN:
                PawnMoves pawnMoves = new PawnMoves();
                return pawnMoves.pieceMoves(board, myPosition);
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

// END CLASS
}
