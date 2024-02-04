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

    // This is helpful to get the opposite of whatever color you pass in
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
                validMoves.add(move);
            }
            catch(Exception e){
                continue;
            }
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
        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        // there is no piece at start positions
        if(pieceToMove == null){
            throw new InvalidMoveException("No piece at this position");
        }
        // this is not their turn
        if(pieceToMove.getTeamColor() != currTeamTurn){
            throw new InvalidMoveException("Not your turn");
        }
        Collection<ChessMove> possibleMoves = new ArrayList<>(pieceToMove.pieceMoves(gameBoard, move.getStartPosition()));
        // the endPosition of the move is not in the possible moves of hte piece
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException("This move is not allowed for this piece");
        }
        // run a test game and see if this move puts their king in check
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(gameBoard.getCopy());
        tempGame.getBoard().addPiece(move.getEndPosition(), tempGame.getBoard().getPiece(move.getStartPosition()));
        tempGame.getBoard().removePiece(move.getStartPosition());
        if(tempGame.isInCheck(pieceToMove.getTeamColor())){
            throw new InvalidMoveException("This move puts you in check");
        }
        // the end move must be contained in the pieceMoves
        else{
            // case that it is a PAWN need to check for promotion pieces
            if(move.getPromotionPiece() != null){
                gameBoard.addPiece(move.getEndPosition(), new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece()));
            }
            else gameBoard.addPiece(move.getEndPosition(), pieceToMove);
            gameBoard.removePiece(move.getStartPosition());
            setTeamTurn(getOppositeColor(pieceToMove.getTeamColor()));
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
        // loop through the board positions
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
        // loop through the moves of all the possibleAttackMoves and see if any match the kings position
        for(ChessMove move : possibleAttackMoves){
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
            // loop through and see if the king has an escape
            return !kingCanMove(teamColor);
        }
        return false;
    }

    // takes a team color and returns true if the king can move, returns false if the king cannot move
    public boolean kingCanMove(TeamColor teamColor){
        ChessPosition kingPos = gameBoard.getKingPosition(teamColor);
        // loop through and see if the king has an escape
        for(ChessMove kingMove : gameBoard.getPiece(kingPos).pieceMoves(gameBoard, kingPos)){
            ChessPosition endPos = kingMove.getEndPosition();
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(gameBoard.getCopy());
            tempGame.getBoard().addPiece(endPos, tempGame.getBoard().getPiece(kingPos));
            tempGame.getBoard().removePiece(kingPos);
            // if the king is in check at the new spot, then he still cannot move
            if(tempGame.isInCheck(tempGame.getBoard().getPiece(endPos).getTeamColor())){
                continue;
            }
            // if there is a spot that the king can move, return true
            else return true;
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
        return (!isInCheck(teamColor) && !kingCanMove(teamColor));
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
