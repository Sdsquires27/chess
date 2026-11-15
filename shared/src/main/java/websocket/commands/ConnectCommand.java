package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    private final String role;
    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String role) {
        super(commandType, authToken, gameID);
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
