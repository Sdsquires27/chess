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
    public UserData userData;
    public AuthData authData;

    @BeforeEach
    public void resetAccess() {
        userData = null;
        authData = null;
        calledMethod = Map.of("clearDB", false,
        "authorized", false,
                "createGame", false,
                "findGame", false,
                "listGames", false,
                "deleteSession", false,
                "findUser", false,
                "createAuth", false,
                "createUser", false,
                "updateGame", false
        );
    }


    @Override
    public void clearDB() throws DataAccessException {
        calledMethod.put("clearDB", true);
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
