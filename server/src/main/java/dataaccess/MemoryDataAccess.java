package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import service.requests.AuthTokenRequest;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users;
    private Map<String, String> auth;
    private Map<Integer, GameData> games;

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
    public Boolean authorized(AuthTokenRequest request) throws DataAccessException {
        return auth.containsKey(request.authToken());
    }

    @Override
    public void createGame(String gameName, int gameId) throws DataAccessException {
        games.put(gameId, new GameData(gameId, null, null, gameName, new ChessGame()));
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void deleteSession(AuthTokenRequest request) throws DataAccessException {
        auth.remove(request.authToken());
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
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
    public void updateGame(int gameID, String game) throws DataAccessException {
        var newGame = new Gson().fromJson(game, GameData.class);
        games.put(gameID, newGame);
    }

    @Override
    public String findUser(AuthTokenRequest request) throws DataAccessException {
        return auth.get(request.authToken());
    }
}
