package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessPiece.PieceType.PAWN;

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
                break;
            case QUEEN:
                break;
            case ROOK:
                break;
            case BISHOP:
                possibleMoves = getBishopMoves(board, myPosition);
                break;
            case KNIGHT:
                break;
            case PAWN:
                break;
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

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 1; i <= MAX_BOARD_INDEX; i++) {
            ChessPosition endPos = new ChessPosition(row + i, col + i);
            if (endPos.getRow() <= (MAX_BOARD_INDEX + 1) && endPos.getColumn() <= (MAX_BOARD_INDEX + 1)) {
                if (board.getPiece(endPos) == null) {
                    ChessMove move = new ChessMove(myPosition, endPos, null);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos, null);
                        possibleMoves.add(move);
                    }
                    break;
                }
            }
        }

        for (int i = 1; i <= MAX_BOARD_INDEX; i++) {
            ChessPosition endPos1 = new ChessPosition(row + i, col - i);
            if (endPos1.getRow() <= (MAX_BOARD_INDEX + 1) && endPos1.getColumn() > 0) {
                if (board.getPiece(endPos1) == null) {
                    ChessMove move = new ChessMove(myPosition, endPos1, null);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos1).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos1, null);
                        possibleMoves.add(move);
                    }
                    break;
                }
            }
        }

        for (int i = 1; i <= MAX_BOARD_INDEX; i++) {
            ChessPosition endPos2 = new ChessPosition(row - i, col - i);
            if (endPos2.getRow() > 0 && endPos2.getColumn() > 0) {
                if (board.getPiece(endPos2) == null) {
                    ChessMove move = new ChessMove(myPosition, endPos2, null);
                    possibleMoves.add(move);
                } else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos2).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos2, null);
                        possibleMoves.add(move);
                    }
                    break;
                }
            }
        }

        for (int i = 1; i <= MAX_BOARD_INDEX; i++) {
            ChessPosition endPos3 = new ChessPosition(row - i, col + i);
            if(endPos3.getRow() > 0 && endPos3.getColumn() <= MAX_BOARD_INDEX+1) {
                if (board.getPiece(endPos3) == null) {
                    ChessMove move = new ChessMove(myPosition, endPos3, null);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos3).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos3, null);
                        possibleMoves.add(move);
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }


// END CLASS
}
