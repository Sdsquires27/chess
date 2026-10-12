package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
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
        return null;
    }

    @Override
    public void createAuth(String authData) throws DataAccessException {

    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void updateGame(int GameID, String game) throws DataAccessException {

    }
}
