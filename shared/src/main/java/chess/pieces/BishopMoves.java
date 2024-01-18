package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves implements PieceMoves {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 1; i <= MAX_BOARD_INDEX; i++) {
            ChessPosition endPos = new ChessPosition(row + i, col + i);
            if (endPos.getRow() <= (MAX_BOARD_INDEX + 1) && endPos.getColumn() <= (MAX_BOARD_INDEX + 1)) {
                if (board.getPiece(endPos) == null) {
                    ChessMove move = new ChessMove(myPosition, endPos);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos);
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
                    ChessMove move = new ChessMove(myPosition, endPos1);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos1).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos1);
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
                    ChessMove move = new ChessMove(myPosition, endPos2);
                    possibleMoves.add(move);
                } else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos2).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos2);
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
                    ChessMove move = new ChessMove(myPosition, endPos3);
                    possibleMoves.add(move);
                }
                else {
                    // if it is different color, we can take it
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(endPos3).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, endPos3);
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
