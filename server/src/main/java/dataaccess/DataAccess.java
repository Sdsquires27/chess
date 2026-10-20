package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    public void clearDB() throws DataAccessException;
    public Boolean authorized(String authToken) throws DataAccessException;
    public void createGame(String gameName, int gameID) throws DataAccessException;
    public GameData findGame(int gameID) throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public void deleteSession(String authToken) throws DataAccessException;
    public UserData findUser(String username, String password) throws DataAccessException;
    public void createAuth(AuthData authData) throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public void updateGame(int gameID, String game) throws DataAccessException;
    public String findUser(String authToken) throws DataAccessException;
}
