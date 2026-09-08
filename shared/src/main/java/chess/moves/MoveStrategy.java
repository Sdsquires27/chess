package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MoveStrategy {

    private final int[][] directions;
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor color;

    protected MoveStrategy(int[][] directions, ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        this.directions = directions;
        this.board = board;
        this.position = position;
        this.color = color;
    }

    public abstract Collection<ChessMove> validMoves();

    Collection<ChessMove> MoveDownLine() {
        List<ChessMove> moves = new ArrayList<>();
        for (var direction : directions) {
            var curPos = position;
            while (true) {
                var row = curPos.getRow() + direction[0];
                var col = curPos.getColumn() + direction[1];
                if (row > 8 || col > 8 || row < 1 || col < 1) {
                    break;
                }
                var newPos = new ChessPosition(row, col );
                var piece = board.getPiece(newPos);
                if (piece != null){
                    if (piece.getTeamColor() != color){
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
                moves.add(new ChessMove(position, newPos, null));
                curPos = newPos;
            }

        }
        return moves;
    }
}

