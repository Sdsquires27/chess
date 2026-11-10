package client;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientController{
    private final ServerFacade facade;
    public State state;
    public ClientController(ServerFacade facade) {
        this.facade = facade;
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

    public void create(String gameName) {
        facade.createGame(gameName);
    }

    public List<GameData> listGames() {
        return new ArrayList<>(facade.listGames());
    }

    public void logout() {
        facade.logout();
        state = State.SIGNED_OUT;
    }
}
