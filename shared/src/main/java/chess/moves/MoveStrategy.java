package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveStrategy {
    Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position);
}

