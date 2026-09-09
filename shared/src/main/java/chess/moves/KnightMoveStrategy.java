package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveStrategy extends MoveStrategy {

    private final int[][] paths = {
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
        super(null, board, position, color);
        this.position = position;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<ChessMove> validMoves() {
        List<ChessMove> moves = new ArrayList<>();
        for (var path : paths) {
            var row = position.getRow() + path[0];
            var col = position.getColumn() + path[1];
            if (OutsideBounds(row, col)) {
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
