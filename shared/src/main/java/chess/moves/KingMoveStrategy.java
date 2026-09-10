package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveStrategy extends MoveStrategy{

    private static final int[][]directions = {
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
        super(directions, board, position, color);
        this.position = position;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<ChessMove> validMoves() {
        List<ChessMove> moves = new ArrayList<>();
        for (var direction : directions) {
            var row = position.getRow() + direction[0];
            var col = position.getColumn() + direction[1];
            if (outsideBounds(row, col)) {
                continue;
            }
            var newPos = new ChessPosition(row, col);
            var piece = board.getPiece(newPos);
            if (piece != null && piece.getTeamColor() == color) {
                continue;
            }
            moves.add(new ChessMove(position, newPos, null));
        }
        return moves;
    }
}
