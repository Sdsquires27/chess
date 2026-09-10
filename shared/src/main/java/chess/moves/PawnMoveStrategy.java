package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveStrategy extends MoveStrategy{

    private final ChessPosition position;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;
    private final int direction;

    public PawnMoveStrategy(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(null, board, position, color);
        this.position = position;
        this.board = board;
        this.color = color;
        this.direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;
    }

    @Override
    public Collection<ChessMove> validMoves() {
        List<ChessMove> moves = new ArrayList<>();
        // move side to side
        var nextRow = position.getRow() + direction;
        var leftColumn = position.getColumn() - 1;
        var rightColumn = position.getColumn() + 1;
        for (int[] path : new int[][] {{nextRow, leftColumn}, {nextRow, rightColumn}}){
            if (outsideBounds(path[0], path[1])){
                continue;
            }
            var newPos = new ChessPosition(path[0], path[1]);
            var pieceOnPos = board.getPiece(newPos);
            if (pieceOnPos != null && pieceOnPos.getTeamColor() != color) {
                addMove(position, newPos, moves);
            }
        }

        // move straight
        var firstRow = position.getRow() + direction;
        var secondRow = position.getRow() + (direction * 2);
        var col = position.getColumn();
        for (int[] path : new int[][] {{firstRow, col},{secondRow, col}}){
            var newPos = new ChessPosition(path[0], path[1]);
            if (board.getPiece(newPos) != null) {
                // if a piece is there, return
                return moves;
            }
            addMove(position, newPos, moves);
            if (leftStartingPosition()){
                return moves;
            }
        }
        return moves;
    }

    private boolean leftStartingPosition(){
        var row = position.getRow();
        return !((color == ChessGame.TeamColor.BLACK && row == 7) || (color == ChessGame.TeamColor.WHITE && row == 2));
    }

    private void addMove(ChessPosition curPos, ChessPosition newPos, List<ChessMove> moves){
        var targetRow = color == ChessGame.TeamColor.WHITE ? 8 : 1;
        if (newPos.getRow() != targetRow){
            moves.add(new ChessMove(curPos, newPos, null));
            return;
        }
        for (ChessPiece.PieceType pieceType : ChessPiece.PieceType.values()){
            if (pieceType == ChessPiece.PieceType.KING || pieceType == ChessPiece.PieceType.PAWN){
                continue;
            }
            moves.add(new ChessMove(curPos, newPos, pieceType));
        }

    }
}
