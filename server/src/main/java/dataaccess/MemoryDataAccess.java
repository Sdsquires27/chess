package dataaccess;

import model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users;
    private Map<String, String> auth;
    @Override
    public void clearDB() throws DataAccessException {

    }

    @Override
    public String authorized(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public void createGame() throws DataAccessException {

    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteSession(String authToken) throws DataAccessException {

    }

    @Override
    public UserData findUser(String username, String password) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        auth.put(authData.authToken(), authData.username());
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public void updateGame(int GameID, String game) throws DataAccessException {

    }
}
