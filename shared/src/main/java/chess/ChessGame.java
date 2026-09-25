package chess;

import jdk.jshell.spi.ExecutionControl;

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
    private HashMap<TeamColor, Boolean> canCastle;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        canCastle.put(TeamColor.WHITE, true);
        canCastle.put(TeamColor.BLACK, true);
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
        if (piece == null) return null;
        var curColor = piece.getTeamColor();
        var pieceMoves = piece.pieceMoves(board, startPosition);
        var possibleMoves = new ArrayList<ChessMove>();

        for (var move : pieceMoves){
            var newBoard = board.boardAfterMove(move);
            if (isInCheckHelper(newBoard, curColor)) continue;
            possibleMoves.add(move);
        }

        if(canCastle.get(teamTurn) && piece.getPieceType() == ChessPiece.PieceType.KING){
            possibleMoves.addAll(castleMoves(startPosition));
        }

        return possibleMoves;
    }


    private Collection<ChessMove> castleMoves(ChessPosition kingPosition) {
        // check right
        var col = kingPosition.getColumn();
        var row = kingPosition.getRow();
        // check left
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
        var piece = board.getPiece(move.getStartPosition());
        if (!isValidMove(move) || (piece != null && piece.getTeamColor() != teamTurn))throw new InvalidMoveException();
        board.movePiece(move);
        if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
            canCastle.replace(teamTurn, false);
        }
        teamTurn = oppositeColor(teamTurn);
    }

    private boolean isValidMove(ChessMove move) {
        var startPos = move.getStartPosition();
        var piece = board.getPiece(startPos);
        if (piece == null) return false;
        var validMoves = validMoves(startPos);
        for (var validMove : validMoves) {
            if (validMove.equals(move)) return true;
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
            if (enemyMove.getEndPosition().equals(board.kingPosition(teamColor))) return true;
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
        if (!isInCheck(teamColor)) return false;
        return teamHasNoMoves(teamColor);
    }

    private boolean teamHasNoMoves(TeamColor teamColor){
        var moves = board.allColorMoves(teamColor);
        for (var move : moves){
            if (isValidMove(move)) return false;
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
        if (isInCheck(teamColor)) return false;
        return teamHasNoMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
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
