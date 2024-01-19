package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.MAX_BOARD_INDEX;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;
    public ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
    public ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
    public ChessBoard() {
        this.board = new ChessPiece[MAX_BOARD_INDEX+1][MAX_BOARD_INDEX+1];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.addPiece(new ChessPosition(1,1), new ChessPiece(white, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,2), new ChessPiece(white, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,3), new ChessPiece(white, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,4), new ChessPiece(white, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1,5), new ChessPiece(white, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1,6), new ChessPiece(white, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,7), new ChessPiece(white, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,8), new ChessPiece(white, ChessPiece.PieceType.ROOK));

        this.addPiece(new ChessPosition(8,1), new ChessPiece(black, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,2), new ChessPiece(black, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,3), new ChessPiece(black, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,4), new ChessPiece(black, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8,5), new ChessPiece(black, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8,6), new ChessPiece(black, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,7), new ChessPiece(black, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,8), new ChessPiece(black, ChessPiece.PieceType.ROOK));


        for (int col = 1; col <= MAX_BOARD_INDEX+1; col++) {
            this.addPiece(new ChessPosition(7,col), new ChessPiece(black, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(2,col), new ChessPiece(white, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board) && white == that.white && black == that.black;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(white, black);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");
        for(int row = 1; row <= MAX_BOARD_INDEX+1; row++){
            for(int col = 1; col <= MAX_BOARD_INDEX+1; col++){
                String stringToAdd = " ";
                String colLine = "|";
                ChessPiece piece = (this.getPiece(new ChessPosition(row,col)));
                if(piece == null) {
                    boardString.append(colLine).append(stringToAdd);
                    continue;
                }
                if(piece.getPieceType() == ChessPiece.PieceType.KING){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "K";
                    }
                    else stringToAdd = "k";
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "Q";
                    }
                    else stringToAdd = "q";
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "N";
                    }
                    else stringToAdd = "n";
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.ROOK){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "R";
                    }
                    else stringToAdd = "r";
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "B";
                    }
                    else stringToAdd = "b";
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
                    if(piece.getTeamColor() == white){
                        stringToAdd = "P";
                    }
                    else stringToAdd = "p";
                }
                boardString.append(colLine).append(stringToAdd);
            }
            boardString.append("|\n");
        }
        return boardString.toString();
    }

    // END CLASS
}

