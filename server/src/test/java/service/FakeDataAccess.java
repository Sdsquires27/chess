package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.*;

public class FakeDataAccess implements DataAccess {
    public Map<String, Boolean> calledMethod = new HashMap<>();
    public UserData defaultUser = new UserData("RealUser", "password", "email");
    public String defaultAuth = "AuthToken";
    public UserData userData;
    public AuthData authData;
    public String authToken;
    public String[] methods = new String[] {"clearDB", "authorized", "createGame", "findGame", "listGames",
    "deleteSession", "findUser", "createAuth", "createUser", "updateGame"};

    public void resetAccess() {
        userData = null;
        authData = null;
        authToken = null;
        calledMethod = new HashMap<>();
        for (var method: methods) {
            calledMethod.put(method, false);
        }
    }


    @Override
    public void clearDB() throws DataAccessException {
        calledMethod.put("clearDB", true);
    }

    @Override
    public String authorized(String authToken) throws DataAccessException {
        calledMethod.put("authorized", true);
        if (Objects.equals(authToken, defaultAuth)){
            return defaultUser.username();
        }
        return null;
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
        calledMethod.put("deleteSession", true);
        this.authToken = authToken;
    }

    @Override
    public UserData findUser(String username, String password) throws DataAccessException {
        calledMethod.put("findUser", true);
        if (Objects.equals(username, defaultUser.username()) && Objects.equals(password, defaultUser.password())){
            return new UserData(defaultUser.username(), defaultUser.password(), defaultUser.email());
        }
        return null;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        calledMethod.put("createAuth", true);
        this.authData = authData;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        calledMethod.put("createUser", true);
        this.userData = userData;
    }

    @Override
    public void updateGame(int GameID, String game) throws DataAccessException {

    }
}
