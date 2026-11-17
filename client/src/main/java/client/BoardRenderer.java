package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BoardRenderer {
    enum Color {
        WHITE,
        BLACK
    }
    final static String BOARD_COLOR_LIGHT = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    final static String BOARD_COLOR_DARK = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    final static String PIECE_COLOR_WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE;
    final static String PIECE_COLOR_BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;

    Color color;
    char[] boardChars = new char[8];

    public BoardRenderer(String color) {
        int i = 0;
        for (char ch = 'a'; ch <= 'h'; ch++ ) {
            boardChars[i++] = ch;
        }
        if (color.equalsIgnoreCase("white")) {
            this.color = Color.WHITE;
            return;
        }
        if (color.equalsIgnoreCase("black")) {
            this.color = Color.BLACK;
            return;
        }
        throw new BoardRendererException("invalid color input");

    }

    public String displayBoard(ChessBoard board) {
        return displayBoardHelper(board, null, new ArrayList<>());
    }

    public String highlightBoard(ChessBoard board, ChessPosition piecePosition, ArrayList<ChessMove> moves){
        ArrayList<ChessPosition> positions = new ArrayList<>();
        moves.forEach(move -> positions.add(move.getEndPosition()));
        return displayBoardHelper(board, piecePosition, positions);
    }

    private String displayBoardHelper(ChessBoard board, ChessPosition piecePosition,
                                      ArrayList<ChessPosition> highlightPositions) {
        StringBuilder builder = new StringBuilder();
        builder.append(EscapeSequences.SET_BG_COLOR_WHITE +
                EscapeSequences.SET_TEXT_COLOR_BLACK +
                EscapeSequences.EMPTY);
        int i_start = color == Color.BLACK ? 1 : 8;
        int i_end = color == Color.BLACK ? 8 : 1;
        int increment = color == Color.BLACK ? 1 : -1;
        int j_start = color == Color.WHITE ? 0 : 7;
        int j_end = color == Color.WHITE ? 7 : 0;
        int spacer = 0;
        for (int i = i_start; increment > 0 ? i <= i_end : i >= i_end; i += increment) {
            builder.append(String.format(
                    spacer % 2 == 0 ? " %s " : "  %s ",
                    boardChars[8 - i]));
            spacer ++;
        }
        builder.append(EscapeSequences.EMPTY).append("\n");

        var curBackground = BOARD_COLOR_LIGHT;
        for (int i = i_start; increment > 0 ? i <= i_end : i >= i_end; i += increment) {
            builder.append(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK)
                    .append(String.format(" %s ", i));
            for (int j = j_start; increment < 0 ? j <= j_end : j >= j_end; j += (increment * -1) ) {
                var pos = new ChessPosition(i, j + 1);
                String backgroundColor = Objects.equals(piecePosition, pos) ? EscapeSequences.SET_BG_COLOR_YELLOW :
                        highlightPositions.contains(pos) ? Objects.equals(curBackground, BOARD_COLOR_DARK) ?
                        EscapeSequences.SET_BG_COLOR_RED : EscapeSequences.SET_BG_COLOR_MAGENTA :
                                curBackground;
                builder.append(backgroundColor).append(
                displayPiece(board.getPiece(pos)));
                curBackground = curBackground.equals(BOARD_COLOR_DARK) ? BOARD_COLOR_LIGHT : BOARD_COLOR_DARK;
            }
            curBackground = curBackground == BOARD_COLOR_LIGHT ? BOARD_COLOR_DARK : BOARD_COLOR_LIGHT;
            builder.append(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK)
                    .append(String.format(" %s ", i))
                    .append("\n");
        }

        builder.append(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.EMPTY);
        spacer = 0;
        for (int i = i_start; increment > 0 ? i <= i_end : i >= i_end; i += increment) {
            builder.append(String.format(
                    spacer % 2 == 0 ? " %s " : "  %s ",
                    boardChars[8 - i]));
            spacer ++;
        }
        builder.append(EscapeSequences.EMPTY).append("\n");

        return builder.toString();
    }

    private String displayPiece(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        return switch(piece.getTeamColor()) {
            case WHITE -> PIECE_COLOR_WHITE + switch(piece.getPieceType()) {
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
            case BLACK -> PIECE_COLOR_BLACK + switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        };
    }
}
