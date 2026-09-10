package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMoveStrategy extends MoveStrategy {

    private static final int[][] DIRECTIONS = {
            {0, 1},
            {0, -1},
            {1, 0},
            {-1,0},
    };
    public RookMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        super(DIRECTIONS, board, position, color);
    }
    @Override
    public Collection<ChessMove> validMoves() {

        return moveDownLine();
    }
}
