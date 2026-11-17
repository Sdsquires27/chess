package client;

import chess.ChessGame;

public interface ClientOutput {
    void sendNotification(String notification);
    void sendError(String error);
    void sendGame(ChessGame game);
}
