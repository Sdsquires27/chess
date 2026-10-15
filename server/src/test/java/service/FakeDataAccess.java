package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.List;

public class FakeDataAccess implements DataAccess {
    public boolean calledMethod = false;

    @BeforeEach
    public void resetAccess() {
        calledMethod = false;
    }


    @Override
    public void clearDB() throws DataAccessException {
        calledMethod = true;
    }

    @Override
    public String authorized(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public void createGame(String gameName, String gameID) throws DataAccessException {

    }

    @Override
    public GameData findGame(String gameID) throws DataAccessException {
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
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void updateGame(int GameID, String game) throws DataAccessException {

    }
}
