package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {

    }

    private ChessBoard gameBoard;
    private TeamColor currTeamTurn;

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeamTurn;
    }

    public TeamColor getOppositeColor(TeamColor color){
        if(color == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        else return TeamColor.WHITE;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(gameBoard.getPiece(startPosition) == null) return null;
        Collection<ChessMove> possibleMoves = new ArrayList<>(gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition));
        for(ChessMove move : possibleMoves){
            ChessGame tempGame = new ChessGame();
            ChessBoard tempBoard = gameBoard.getCopy();
            tempGame.setBoard(tempBoard);
            tempGame.setTeamTurn(tempGame.getBoard().getPiece(startPosition).getTeamColor());
            try{
                tempGame.makeMove(move);
            }
            catch(Exception e){
                continue;
            }
            validMoves.add(move);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(gameBoard);
        tempGame.getBoard().addPiece(move.getEndPosition(), tempGame.getBoard().getPiece(move.getStartPosition()));
        if(tempGame.isInCheck(getTeamTurn())){
            throw new InvalidMoveException();
        }
        else{
            gameBoard.addPiece(move.getEndPosition(), gameBoard.getPiece(move.getStartPosition()));
            gameBoard.removePiece(move.getStartPosition());
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = gameBoard.getKingPosition(teamColor);
        Collection<ChessMove> possibleAttackMoves = new ArrayList<>();
        System.out.print(gameBoard.toString());
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition currPos = new ChessPosition(i,j);
                // if its the opposite color as the king, then add to possible moves
                if(gameBoard.getPiece(currPos) == null) continue;
                if(gameBoard.getPiece(currPos).getTeamColor() == getOppositeColor(teamColor)){
                    possibleAttackMoves.addAll(gameBoard.getPiece(currPos).pieceMoves(gameBoard, currPos));
                }
            }
        }
        for(ChessMove move : possibleAttackMoves){
            // now loop through opponents possible moves and see if any match with the king
            if(move.getEndPosition().equals(kingPos)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // king is in check, and has not valid moves
        if(isInCheck(teamColor)){
            ChessPosition kingPos = gameBoard.getKingPosition(teamColor);
            return validMoves(kingPos).isEmpty();
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }
}
