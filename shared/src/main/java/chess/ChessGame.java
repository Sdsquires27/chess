package chess;

import jdk.jshell.spi.ExecutionControl;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private HashMap<ChessPosition, TeamColor> canCastle = new HashMap<ChessPosition, TeamColor>();
    private ChessPosition enPassant = null;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        canCastle.put(new ChessPosition(1, 1), TeamColor.WHITE);
        canCastle.put(new ChessPosition(1, 8), TeamColor.WHITE);
        canCastle.put(new ChessPosition(8, 1), TeamColor.BLACK);
        canCastle.put(new ChessPosition(8, 8), TeamColor.BLACK);

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        var curColor = piece.getTeamColor();
        var pieceMoves = piece.pieceMoves(board, startPosition);
        var possibleMoves = new ArrayList<ChessMove>();

        for (var move : pieceMoves){
            var newBoard = board.boardAfterMove(move);
            if (isInCheckHelper(newBoard, curColor)) {
                continue;
            }
            possibleMoves.add(move);
        }

        if(canCastle.containsValue(curColor) && piece.getPieceType() == ChessPiece.PieceType.KING){
            possibleMoves.addAll(castleMoves(startPosition, curColor));
        }

        if (enPassant != null && piece.getPieceType() == ChessPiece.PieceType.PAWN){
            var enPassantColor = board.getPiece(enPassant).getTeamColor();
            if (curColor != enPassantColor){
                var possibleMove = enPassantMoves(startPosition, enPassant, curColor);
                if (possibleMove != null) {
                    possibleMoves.add(possibleMove);
                };
            }
        }

        return possibleMoves;
    }

    private ChessMove enPassantMoves(ChessPosition startPos, ChessPosition enPassantPos, TeamColor color){
        var diff = startPos.getColumn() - enPassantPos.getColumn();
        if (startPos.getRow() == enPassantPos.getRow() && (diff == 1 || diff == -1)){
            var dir = color == TeamColor.WHITE ? 1 : -1;
            return new ChessMove(startPos, new ChessPosition(startPos.getRow() + dir, enPassantPos.getColumn()),
                    null);
        }
        return null;
    }


    private Collection<ChessMove> castleMoves(ChessPosition kingPosition, TeamColor color) {
        var moves = new ArrayList<ChessMove>();

        for (var castleInfo : canCastle.entrySet()) {
            if (castleInfo.getValue() != color) {
                continue;
            }

            var move = testCastle(kingPosition, color, castleInfo.getKey());
            if (move != null) {
                moves.add(move);
            }
        }
        return moves;
    }

    private ChessMove testCastle(ChessPosition kingPos, TeamColor color, ChessPosition targetPos){
        int row = kingPos.getRow();
        int kingCol = kingPos.getColumn();
        int targetCol = targetPos.getColumn();
        var newBoard = new ChessBoard(board);
        var curPos = kingPos;
        int increment = kingCol - targetCol > 0 ? -1 : 1;
        var nextKingCol = kingCol + (increment * 2);

        if (isInCheckHelper(newBoard, color)){
            return null;
        }

        for (int curCol = kingCol + increment; curCol != nextKingCol + increment;  curCol += increment){
            var nextPos = new ChessPosition(row, curCol);

            if (newBoard.getPiece(nextPos) != null){
                return null;
            }

            newBoard.movePiece(new ChessMove(curPos, nextPos, null));
            if (isInCheckHelper(newBoard, color)){
                return null;
            }

            curPos = nextPos;
        }

        return new ChessMove(kingPos, new ChessPosition(row, nextKingCol), null);

    }

    private TeamColor oppositeColor(TeamColor color){
        return color == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPos = move.getStartPosition();
        var piece = board.getPiece(startPos);
        if (!isValidMove(move) || (piece != null && piece.getTeamColor() != teamTurn)) {
            throw new InvalidMoveException();
        }

        enPassant = null;
        teamTurn = oppositeColor(teamTurn);

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING){
            var curCol = startPos.getColumn();
            var targetCol = move.getEndPosition().getColumn();
            var diff = curCol - targetCol;
            if(diff > 1 || diff < - 1){
                var origCol = diff < -1 ? 8 : 1;
                var newCol = diff < -1 ? 6 : 4;
                var row = startPos.getRow();
                board.castlePiece(move, new ChessMove(new ChessPosition(row, origCol),
                        new ChessPosition(row, newCol), null));
                return;

            }
            canCastle.entrySet().removeIf(entry -> entry.getValue() == oppositeColor(teamTurn));
        }
        canCastle.remove(startPos);
        canCastle.remove(move.getEndPosition());

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN){
            var diff = startPos.getRow() - move.getEndPosition().getRow();
            if (diff > 1 || diff < -1){
                enPassant = move.getEndPosition();
            }
            if (startPos.getColumn() != move.getEndPosition().getColumn()){
                if (board.getPiece(move.getEndPosition()) == null){
                    board.deletePiece(new ChessPosition(move.getStartPosition().getRow(),
                            move.getEndPosition().getColumn()));
                }
            }
        }

        board.movePiece(move);
    }

    private boolean isValidMove(ChessMove move) {
        var startPos = move.getStartPosition();
        var piece = board.getPiece(startPos);
        if (piece == null) {
            return false;
        }
        var validMoves = validMoves(startPos);
        for (var validMove : validMoves) {
            if (validMove.equals(move)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(board, teamColor);
    }

    private boolean isInCheckHelper(ChessBoard board, TeamColor teamColor){
        var enemyMoves = board.allColorMoves(oppositeColor(teamColor));
        for (var enemyMove : enemyMoves){
            if (enemyMove.getEndPosition().equals(board.kingPosition(teamColor))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return teamHasNoMoves(teamColor);
    }

    private boolean teamHasNoMoves(TeamColor teamColor){
        var moves = board.allColorMoves(teamColor);
        for (var move : moves){
            if (isValidMove(move)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return teamHasNoMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        var kingPositions = new ChessPosition[] {new ChessPosition(1, 5),
                new ChessPosition(8, 5)};
        var kingColors = new TeamColor[] {TeamColor.WHITE, TeamColor.BLACK};
        for (int i = 0; i < kingPositions.length; i ++){
            var curColor = kingColors[i];
            var kingPiece = board.getPiece(kingPositions[i]);
            if (kingPiece == null){
                canCastle.entrySet().removeIf(entry -> entry.getValue() == curColor);
                return;
            }
            if (kingPiece.getPieceType() != ChessPiece.PieceType.KING ||
            kingPiece.getTeamColor() != curColor){
                canCastle.entrySet().removeIf(entry -> entry.getValue() == curColor);
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
