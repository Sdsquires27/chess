package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class RookMoveStrategy extends MoveStrategy {

    private static final int[][]directions = {
            {0, 1},
            {0, -1},
            {1, 0},
            {-1,0},
    };
    public RookMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        super(directions, board, position, color);
    }
    @Override
    public Collection<ChessMove> validMoves() {

        return MoveDownLine();
    }
}
