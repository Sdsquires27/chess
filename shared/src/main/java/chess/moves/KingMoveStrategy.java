package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveStrategy extends MoveStrategy{

    private static final int[][] DIRECTIONS = {
            {1,1},
            {-1, 1},
            {-1, -1},
            {1, -1},
            {0,1},
            {1, 0},
            {-1, 0},
            {0, -1}
    };

    private final ChessPosition position;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public KingMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
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
