package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveStrategy extends MoveStrategy {

    private static final int[][] DIRECTIONS = {
            {2, 1},
            {2, -1},
            {1, -2},
            {1, 2},
            {-2, 1},
            {-2, -1},
            {-1, -2},
            {-1, 2},
    };
    private final ChessPosition position;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;
    public KnightMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(DIRECTIONS, board, position, color);
        this.position = position;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<ChessMove> validMoves() {
        return moveToPosition();
    }
}
