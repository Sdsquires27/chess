package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.AuthTokenRequest;

import java.util.*;

public class FakeDataAccess implements DataAccess {
    public Map<String, Boolean> calledMethod = new HashMap<>();
    public UserData defaultUser;
    public String password = "password";
    public String defaultAuth = "AuthToken";
    public UserData userData;
    public AuthData authData;
    public String authToken;
    public GameData game;
    public String[] methods = new String[] {"clearDB", "authorized", "createGame", "findGame", "listGames",
    "deleteSession", "findUser", "createAuth", "createUser", "updateGame"};
    public GameData defaultWhiteGame = new GameData(1234, "whiteUser", null,
            "RealName", new ChessGame());
    public GameData defaultBlackGame = new GameData(5678, null, "blackUser",
            "RealName", new ChessGame());
    public List<GameData> gameList = List.of(defaultWhiteGame, defaultBlackGame,
            new GameData(1010, "test",
            "test", "test", new ChessGame()));

    public void resetAccess() {
        userData = null;
        authData = null;
        game = null;
        authToken = null;
        calledMethod = new HashMap<>();
        defaultUser =  new UserData("RealUser",
                BCrypt.hashpw(password, BCrypt.gensalt()), "email");
        for (var method: methods) {
            calledMethod.put(method, false);
        }
    }


    @Override
    public void clearDB() throws DataAccessException {
        calledMethod.put("clearDB", true);
    }

    @Override
    public Boolean authorized(AuthTokenRequest request) throws DataAccessException {
        calledMethod.put("authorized", true);
        return Objects.equals(request.authToken(), defaultAuth);
    }

    @Override
    public void createGame(String gameName, int gameID) throws DataAccessException {
        calledMethod.put("createGame", true);
        game = new GameData(gameID, "", "", gameName, null);

    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        calledMethod.put("findGame", true);
        if (gameID == defaultWhiteGame.gameID()){
            return defaultWhiteGame;
        }
        if (gameID == defaultBlackGame.gameID()){
            return defaultBlackGame;
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        calledMethod.put("listGames", true);
        return gameList;
    }

    @Override
    public void deleteSession(AuthTokenRequest request) throws DataAccessException {
        calledMethod.put("deleteSession", true);
        this.authToken = request.authToken();
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
        calledMethod.put("findUser", true);
        if (Objects.equals(username, defaultUser.username())){
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
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        calledMethod.put("updateGame", true);
        this.game = game;
    }

    @Override
    public String findUser(AuthTokenRequest request) throws DataAccessException {
        calledMethod.put("findUser", true);
        if (Objects.equals(request.authToken(), defaultAuth)) {
            return defaultUser.username();
        }
        return null;
    }
}
