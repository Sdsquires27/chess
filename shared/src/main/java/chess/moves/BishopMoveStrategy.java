package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class BishopMoveStrategy extends MoveStrategy {

    private static final int[][]directions = {
            {1,1},
            {-1, 1},
            {-1, -1},
            {1, -1},
    };
    public BishopMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        super(directions, board, position, color);
    }
    @Override
    public Collection<ChessMove> validMoves() {

        return MoveDownLine();
    }
}
