package client;

import chess.ChessGame;
import model.GameData;
import server.Server;

import java.util.Collection;

public interface ServerFacade {
    public void register(String username, String password, String email) throws ServerFacadeException;
    public Collection<GameData> listGames() throws ServerFacadeException;
    public void login(String username, String password) throws ServerFacadeException;
    public void logout() throws ServerFacadeException;
    public void createGame(String gameName) throws ServerFacadeException;
    public void joinGame(String color, String gameID) throws ServerFacadeException;
}
