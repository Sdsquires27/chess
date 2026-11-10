package client;

import chess.ChessGame;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;

public class ClientController implements Observer{
    private final ServerFacade facade;
    public State state;
    public ClientController(int port) {
        this.facade = new HTTPServerFacade(port, this);
        state = State.SIGNED_OUT;
    }
    public void register(String username, String password, String email) {
        facade.register(username, password, email);
        state = State.SIGNED_IN;
    }

    public void login(String username, String password) {
        facade.login(username, password);
        state = State.SIGNED_IN;
    }

    public void quit(){
        if (state == State.SIGNED_IN) {
            facade.logout();
        }
        state = State.QUIT;
    }

    public ChessGame join(String idString, String color) throws ClientControlException {
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new ClientControlException("invalid number input.");
        }
        var games = listGames();
        if (id > games.size()) {
            throw new ClientControlException("requested game does not exist.");
        }
        var game = games.get(id - 1);
        facade.joinGame(color.toUpperCase(), String.valueOf(game.gameID()));
        return game.game();
    }

    public int create(String gameName) {
        facade.createGame(gameName);
        return facade.listGames().size();
    }

    public List<GameData> listGames() {
        return new ArrayList<>(facade.listGames());
    }

    public GameData findGame(String idString) {
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new ClientControlException("invalid number input.");
        }
        var games = listGames();
        if (id > games.size()) {
            throw new ClientControlException("requested game does not exist.");
        }
        return games.get(id - 1);
    }

    public void logout() {
        facade.logout();
        state = State.SIGNED_OUT;
    }

    @Override
    public void notify(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            handleNotificationMessage((NotificationMessage) message);
            return;
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            handleLoadGameMessage((LoadGameMessage) message);
            return;
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            handleErrorMessage((ErrorMessage) message);
        }
    }

    private void handleNotificationMessage(NotificationMessage message) {
        throw new RuntimeException("Not implemented");
    }

    private void handleErrorMessage(ErrorMessage message) {
        throw new RuntimeException("Not implemented");
    }

    private void handleLoadGameMessage(LoadGameMessage message) {
        throw new RuntimeException("Not implemented");
    }
}
