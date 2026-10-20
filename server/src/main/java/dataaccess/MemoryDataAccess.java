package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users;
    private Map<String, String> auth;
    private Map<String, GameData> games;

    public MemoryDataAccess(){
        users = new HashMap<>();
        auth = new HashMap<>();
        games = new HashMap<>();
    }

    @Override
    public void clearDB() throws DataAccessException {
        users.clear();
        auth.clear();
        games.clear();
    }

    @Override
    public Boolean authorized(String authToken) throws DataAccessException {
        return auth.containsKey(authToken);
    }

    @Override
    public void createGame(String gameName, String gameId) throws DataAccessException {
        games.put(gameId, new GameData(gameId, null, null, gameName, null));
    }

    @Override
    public GameData findGame(String gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void deleteSession(String authToken) throws DataAccessException {
        auth.remove(authToken);
    }

    @Override
    public UserData findUser(String username, String password) throws DataAccessException {
        var user = users.get(username);
        if (user == null || !Objects.equals(user.password(), password)) {
            return null;
        }
        return user;
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
