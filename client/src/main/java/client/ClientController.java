package client;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;

public class ClientController implements Observer{
    private final ServerFacade facade;
    private final ClientOutput output;
    public State state;
    public ClientController(int port, ClientOutput output) throws Exception {
        this.facade = new HTTPServerFacade(port, this);
        this.output = output;
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
        if (id > games.size() || id < 1) {
            throw new ClientControlException("requested game does not exist.");
        }
        var game = games.get(id - 1);
        facade.joinGame(color.toUpperCase(), String.valueOf(game.gameID()));
        state = State.IN_GAME;
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

    public void resign() throws ClientControlException {
        try {
            facade.resign();
        } catch (ServerFacadeException e) {
            throw new ClientControlException(e.getMessage());
        }
    }

    public void leave() throws ClientControlException {
        try {
            facade.leave();
            state = State.SIGNED_IN;
        } catch (ServerFacadeException e) {
            throw new ClientControlException(e.getMessage());
        }

    }

    public void move(ChessMove move) throws ClientControlException{
        try {
            facade.move(move);
        } catch (ServerFacadeException e) {
            throw new ClientControlException(e.getMessage());
        }
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println("Received notification");
        switch (message.getServerMessageType()){
            case ServerMessage.ServerMessageType.NOTIFICATION ->
                    handleNotificationMessage((NotificationMessage) message);
            case ServerMessage.ServerMessageType.LOAD_GAME ->
                    handleLoadGameMessage((LoadGameMessage) message);
            case ServerMessage.ServerMessageType.ERROR ->
                    handleErrorMessage((ErrorMessage) message);
        }
    }

    private void handleNotificationMessage(NotificationMessage message) {
        output.sendNotification(message.getMessage());
    }

    private void handleErrorMessage(ErrorMessage message) {
        output.sendError(message.getMessage());
    }

    private void handleLoadGameMessage(LoadGameMessage message) {
        output.sendGame(message.getGame());
    }
}
