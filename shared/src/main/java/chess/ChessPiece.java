package chess;

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
    public static final int NULL_SPOT = 1;
    public static final int OPPOSITE_COLOR_SPOT = 2;

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
                possibleMoves = getKingMoves(board, myPosition);
                break;
            case QUEEN:
                break;
            case ROOK:
                break;
            case BISHOP:
                possibleMoves = getBishopMoves(board, myPosition);
                break;
            case KNIGHT:
                possibleMoves = getKnightMoves(board, myPosition);
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

    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition endPos = new ChessPosition(row + 1, col);
        if(isValidMove(myPosition, endPos, board)){
            ChessMove move = new ChessMove(myPosition, endPos, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos1 = new ChessPosition(row, col + 1);
        if(isValidMove(myPosition, endPos1, board)){
            ChessMove move = new ChessMove(myPosition, endPos1, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos2 = new ChessPosition(row - 1, col);
        if(isValidMove(myPosition, endPos2, board)){
            ChessMove move = new ChessMove(myPosition, endPos2, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos3 = new ChessPosition(row, col - 1);
        if(isValidMove(myPosition, endPos3, board)){
            ChessMove move = new ChessMove(myPosition, endPos3, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos4 = new ChessPosition(row-1, col-1);
        if(isValidMove(myPosition, endPos4, board)){
            ChessMove move = new ChessMove(myPosition, endPos4, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos5 = new ChessPosition(row+1, col-1);
        if(isValidMove(myPosition, endPos5, board)){
            ChessMove move = new ChessMove(myPosition, endPos5, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos6 = new ChessPosition(row-1, col+1);
        if(isValidMove(myPosition, endPos6, board)){
            ChessMove move = new ChessMove(myPosition, endPos6, null);
            possibleMoves.add(move);
        }

        ChessPosition endPos7 = new ChessPosition(row+1, col+1);
        if(isValidMove(myPosition, endPos7, board)){
            ChessMove move = new ChessMove(myPosition, endPos7, null);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Case 1
        ChessPosition endPos = new ChessPosition(row+1, col+2);
        if(isValidMove(myPosition, endPos, board)){
            ChessMove move = new ChessMove(myPosition, endPos, null);
            possibleMoves.add(move);
        }

        // Case 2
        ChessPosition endPos1 = new ChessPosition(row+1, col-2);
        if(isValidMove(myPosition, endPos1, board)){
            ChessMove move = new ChessMove(myPosition, endPos1, null);
            possibleMoves.add(move);
        }

        // Case 3
        ChessPosition endPos2 = new ChessPosition(row+2, col+1);
        if(isValidMove(myPosition, endPos2, board)){
            ChessMove move = new ChessMove(myPosition, endPos2, null);
            possibleMoves.add(move);
        }

        // Case 4
        ChessPosition endPos3 = new ChessPosition(row+2, col-1);
        if(isValidMove(myPosition, endPos3, board)){
            ChessMove move = new ChessMove(myPosition, endPos3, null);
            possibleMoves.add(move);
        }

        // Case 5
        ChessPosition endPos4 = new ChessPosition(row-1, col+2);
        if(isValidMove(myPosition, endPos4, board)){
            ChessMove move = new ChessMove(myPosition, endPos4, null);
            possibleMoves.add(move);
        }

        // Case 6
        ChessPosition endPos5 = new ChessPosition(row-1, col-2);
        if(isValidMove(myPosition, endPos5, board)){
            ChessMove move = new ChessMove(myPosition, endPos5, null);
            possibleMoves.add(move);
        }

        // Case 7
        ChessPosition endPos6 = new ChessPosition(row-2, col+1);
        if(isValidMove(myPosition, endPos6, board)){
            ChessMove move = new ChessMove(myPosition, endPos6, null);
            possibleMoves.add(move);
        }

        // Case 8
        ChessPosition endPos7 = new ChessPosition(row-2, col-1);
        if(isValidMove(myPosition, endPos7, board)){
            ChessMove move = new ChessMove(myPosition, endPos7, null);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // White adds to ROW, Black subtracts from ROW

        if(this.pieceColor == ChessGame.TeamColor.WHITE){
            ChessPosition endPos1 = new ChessPosition(row+1, col);
            if(NULL_SPOT == isValidMove(myPosition, endPos1, board)){
                ChessMove move = new ChessMove(myPosition, endPos1, null);
                possibleMoves.add(move);
            }

            //check attack
            ChessPosition endPos2 = new ChessPosition(row+1, col+1);
            if(OPPOSITE_COLOR_SPOT == isValidMove(myPosition, endPos2, board)){
                ChessMove move = new ChessMove(myPosition, endPos2, null);
                possibleMoves.add(move);
            }

            //check attack 2
            ChessPosition endPos3 = new ChessPosition(row+1, col-1);
            if(OPPOSITE_COLOR_SPOT == isValidMove(myPosition, endPos3, board)){
                ChessMove move = new ChessMove(myPosition, endPos3, null);
                possibleMoves.add(move);
            }

            // check first move, is 2 spaces, row 2 is starting for white pawns
            if(myPosition.getRow() == 2) {
                ChessPosition endPos4 = new ChessPosition(row + 2, col);
                if (OPPOSITE_COLOR_SPOT == isValidMove(myPosition, endPos4, board)) {
                    ChessMove move = new ChessMove(myPosition, endPos4, null);
                    possibleMoves.add(move);
                }
            }
        }

        return possibleMoves;

    }


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
        return 0;
    }




// END CLASS
}
