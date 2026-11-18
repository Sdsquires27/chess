package client;

import chess.ChessMove;
import model.GameData;

import java.util.Collection;

public interface ServerFacade {
    public void register(String username, String password, String email) throws ServerFacadeException;
    public Collection<GameData> listGames() throws ServerFacadeException;
    public void login(String username, String password) throws ServerFacadeException;
    public void logout() throws ServerFacadeException;
    public void createGame(String gameName) throws ServerFacadeException;
    public void joinGame(String color, String gameID) throws ServerFacadeException;
    public void leave() throws  ServerFacadeException;
    public void resign() throws ServerFacadeException;
    public void move(ChessMove move) throws ServerFacadeException;
    public void observe(String gameName) throws ServerFacadeException;
}
