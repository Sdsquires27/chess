package dataaccess;

import model.*;
import service.requests.AuthTokenRequest;

import java.util.Collection;

public interface DataAccess {
    public void clearDB() throws DataAccessException;
    public Boolean authorized(AuthTokenRequest request) throws DataAccessException;
    public void createGame(String gameName, int gameID) throws DataAccessException;
    public GameData findGame(int gameID) throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public void deleteSession(AuthTokenRequest request) throws DataAccessException;
    public UserData findUser(String username) throws DataAccessException;
    public void createAuth(AuthData authData) throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public void updateGame(int gameID, GameData game) throws DataAccessException;
    public String findUser(AuthTokenRequest request) throws DataAccessException;
}
