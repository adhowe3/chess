package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLUE;

public class ChessBoardPrinter {

    public void printChessBoard(ChessBoard board, ChessGame.TeamColor playerColor){
        if(playerColor == null){
            printChessBoardToTerminalWhite(board);
            return;
        }
        if(playerColor.equals(ChessGame.TeamColor.BLACK)){
            printChessBoardToTerminalBlack(board);
        }
        else printChessBoardToTerminalWhite(board);
    }

    public void printChessBoardHighlight(ChessBoard board, Collection<ChessMove> validMoves, ChessGame.TeamColor playerColor){
        Collection<ChessPosition> validPositions = new ArrayList<>();
        for(ChessMove move : validMoves){
            validPositions.add(move.getEndPosition());
            validPositions.add(move.getStartPosition());
        }
        if(playerColor == null){
            printChessBoardHighlightWhite(board, validPositions);
        }
        else if(playerColor.equals(ChessGame.TeamColor.BLACK)){
            printChessBoardHighlightBlack(board, validPositions);
        }
        else {
            printChessBoardHighlightWhite(board, validPositions);
        }
    }

    private void printChessBoardHighlightWhite(ChessBoard board, Collection<ChessPosition> validPositions){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_DARK_GREY;
        String[] forwardLetters = printLettersTopForward(spacing);

        for(int row = 8; row >= 1; row--){
            for(int col = 8; col >= 1; col--){
                if(validPositions.contains(new ChessPosition(row, col))){
                    printSquareToTerminal(setHighlightColor(backgroundColor), board.getPiece(new ChessPosition(row,col)));
                }
                else{
                    printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                }
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row > 1) System.out.print(SET_BG_COLOR_BLACK+ " " + (row-1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
    }

    private String[] printLettersTopForward(String spacing) {
        String[] forwardLetters ={(" a"+spacing), (" b"+spacing), (" c"+spacing), (" d"+spacing), (" e"+spacing), (" f"+spacing), (" g"+spacing), " h\u2005"};

        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 8 ");
        return forwardLetters;
    }

    private void printChessBoardHighlightBlack(ChessBoard board, Collection<ChessPosition> validPositions){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_DARK_GREY;
        String[] backwardLetters = printLettersTop(spacing);

        for(int row = 1; row < 9; row++){
            for(int col = 1; col < 9; col++){
                if(validPositions.contains(new ChessPosition(row, col))){
                    printSquareToTerminal(setHighlightColor(backgroundColor), board.getPiece(new ChessPosition(row,col)));
                }
                else{
                    printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                }
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row < 8) System.out.print(SET_BG_COLOR_BLACK+ " " + (row+1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY  + SET_BG_COLOR_DARK_GREY);
    }

    private String[] printLettersTop(String spacing) {
        String[] backwardLetters = {(" h"+spacing), (" g"+spacing), (" f"+spacing), (" e"+spacing), (" d"+spacing), (" c"+spacing), (" b"+spacing), " a\u2005"};
        System.out.print(SET_BG_COLOR_BLACK + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_BG_COLOR_BLACK + " 1 ");
        return backwardLetters;
    }

    private void printChessBoardToTerminalBlack(ChessBoard board){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_DARK_GREY;
        String[] backwardLetters = printLettersTop(spacing);

        for(int row = 1; row < 9; row++){
            for(int col = 1; col < 9; col++){
                printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row < 8) System.out.print(SET_BG_COLOR_BLACK+ " " + (row+1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + backwardLetters[i]);
        }
        System.out.println(EMPTY  + SET_BG_COLOR_DARK_GREY);
    }

    private void printChessBoardToTerminalWhite(ChessBoard board){
        String spacing = "\u2001\u2005\u2006";
        String backgroundColor = SET_BG_COLOR_DARK_GREY;
        String[] forwardLetters = printLettersTopForward(spacing);

        for(int row = 8; row >= 1; row--){
            for(int col = 8; col >= 1; col--){
                printSquareToTerminal(backgroundColor, board.getPiece(new ChessPosition(row,col)));
                backgroundColor = flipBgColor(backgroundColor);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + SET_BG_COLOR_DARK_GREY);
            if(row > 1) System.out.print(SET_BG_COLOR_BLACK+ " " + (row-1) + " ");
            backgroundColor = flipBgColor(backgroundColor);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + forwardLetters[i]);
        }
        System.out.println(EMPTY + SET_BG_COLOR_DARK_GREY);
    }

    private void printSquareToTerminal(String bgColor, ChessPiece chessPiece){
        String textColor = SET_TEXT_COLOR_WHITE;
        String pieceString;
        if(chessPiece == null) pieceString = EMPTY;
        else if(chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            textColor = SET_TEXT_COLOR_BLACK;
            pieceString = getPieceString(chessPiece, BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT, BLACK_PAWN);
        }
        else{
            pieceString = getPieceString(chessPiece, WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_PAWN);
        }
        System.out.print(bgColor + textColor +  pieceString);
    }

    private String getPieceString(ChessPiece chessPiece, String blackKing, String blackQueen, String blackRook, String blackBishop, String blackKnight, String blackPawn) {
        String pieceString;
        if(chessPiece == null) return EMPTY;
        switch(chessPiece.getPieceType()){
            case KING -> pieceString = blackKing;
            case QUEEN -> pieceString = blackQueen;
            case ROOK -> pieceString = blackRook;
            case BISHOP -> pieceString = blackBishop;
            case KNIGHT -> pieceString = blackKnight;
            case PAWN -> pieceString = blackPawn;
            default -> pieceString = EMPTY;
        }
        return pieceString;
    }

    private String setHighlightColor(String currColor){
        if(currColor.equals(SET_BG_COLOR_LIGHT_GREY)){
            return SET_BG_COLOR_GREEN;
        }
        if(currColor.equals(SET_BG_COLOR_DARK_GREY)){
            return SET_BG_COLOR_DARK_GREEN;
        }
        return SET_BG_COLOR_BLUE;
    }

    private String flipBgColor(String currColor){
        if(currColor.equals(SET_BG_COLOR_LIGHT_GREY)){
            return SET_BG_COLOR_DARK_GREY;
        }
        if(currColor.equals(SET_BG_COLOR_DARK_GREY)){
            return SET_BG_COLOR_LIGHT_GREY;
        }
        // error case
        return SET_BG_COLOR_BLUE;
    }

}
