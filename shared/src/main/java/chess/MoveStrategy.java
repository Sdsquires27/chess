package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveStrategy {
    private final boolean movesInLine;
    private final int[][] directions;
    private final boolean isPawn;

    public MoveStrategy(boolean movesInLine, int[][] directions, boolean isPawn){
        this.movesInLine = movesInLine;
        this.directions = directions;
        this.isPawn = isPawn;
    }

    private Collection<ChessMove> findPawnTransformations(ChessGame.TeamColor color,
                                                          ChessPosition pos, ChessPosition newPos){
        var moves = new ArrayList<ChessMove>();
        var row = newPos.getRow();
        if ((row == 1 && color == ChessGame.TeamColor.BLACK) || (row == 8 && color == ChessGame.TeamColor.WHITE)){
            for (var type : ChessPiece.PieceType.values()){
                if (type != ChessPiece.PieceType.KING && type != ChessPiece.PieceType.PAWN){
                    moves.add(new ChessMove(pos, newPos, type));
                }
            }
            return moves;
        }
        moves.add(new ChessMove(pos, newPos, null));
        return moves;
    }

    private boolean isInStartingPosition(ChessPosition pos, ChessGame.TeamColor color){
        var row = pos.getRow();
        return ((row == 2 && color == ChessGame.TeamColor.WHITE) || (row == 7 && color == ChessGame.TeamColor.BLACK));
    }

    private Collection<ChessMove> findPawnMoves(ChessPosition pos, ChessBoard board, ChessGame.TeamColor color) {
        var mult = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        var moves = new ArrayList<ChessMove>();
        for(var newPos : new ChessPosition[] {
                new ChessPosition(pos.getRow() + mult, pos.getColumn() + 1),
                new ChessPosition(pos.getRow() + mult, pos.getColumn() - 1)}){
            if (outOfBounds(newPos)){
                continue;
            }
            var newPiece = board.getPiece(newPos);
            if(newPiece != null && newPiece.getTeamColor() != color){
                moves.addAll(findPawnTransformations(color, pos, newPos));
            }
        }
        for (var newPos : new ChessPosition[] {
                new ChessPosition(pos.getRow() + mult, pos.getColumn()),
                new ChessPosition(pos.getRow() + (mult * 2), pos.getColumn())}){
            if (outOfBounds(newPos)){
                break;
            }
            if (board.getPiece(newPos) == null){
                moves.addAll(findPawnTransformations(color, pos, newPos));
                if (!isInStartingPosition(pos, color)){
                    break;
                }
                continue;
            }
            break;

        }
        return moves;
    }

    public Collection<ChessMove> findMoves(ChessPosition pos, ChessBoard board, ChessGame.TeamColor color){
        if (isPawn) {
            return findPawnMoves(pos, board, color);
        }

        var moves = new ArrayList<ChessMove>();

        for (var direction : directions) {
            var newPos = new ChessPosition(pos.getRow() + direction[0], pos.getColumn() + direction[1]);
            while (true){
                if (outOfBounds(newPos)){
                    break;
                }
                var newPiece = board.getPiece(newPos);
                if (newPiece == null) {
                    moves.add(new ChessMove(pos, newPos, null));
                    if (!movesInLine) break;
                    newPos = new ChessPosition(newPos.getRow() + direction[0], newPos.getColumn() + direction[1]);
                    continue;
                }
                if (newPiece.getTeamColor() != color){
                    moves.add(new ChessMove(pos, newPos, null));
                }
                break;
            }
        }
        return moves;
    }

    private boolean outOfBounds(ChessPosition pos) {
        return (pos.getRow() >= 9 || pos.getRow() <= 0 || pos.getColumn() >= 9 || pos.getColumn() <= 0);
    }
}
