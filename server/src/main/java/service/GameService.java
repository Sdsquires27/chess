package service;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class GameService extends ChessService{

    public GameService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public Collection<GameData> listGames(AuthTokenRequest request) throws DataAccessException, UnauthorizedException{
        if (!dataAccess.authorized(request)) {
            throw new UnauthorizedException();
        }
        return dataAccess.listGames();
    }

    public int createGame(CreateGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException{
        if (invalidInput(new String[] {request.gameName()})) {
            throw new InvalidInputException();
        }
        if (!dataAccess.authorized(new AuthTokenRequest(request.authToken()))) {
            throw new UnauthorizedException();
        }
        int id = new Random().nextInt(10000);
        dataAccess.createGame(request.gameName(), id);
        return id;
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException, AlreadyTakenException{
        var playerColor = request.playerColor();
        if(invalidInput(new String[]{playerColor})){
            throw new InvalidInputException();
        }
        if (!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK")) {
            throw new InvalidInputException();
        }
        int id;
        try {
            id = (int) Double.parseDouble(request.gameID());
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
        var username = dataAccess.findUser(new AuthTokenRequest(request.authToken()));
        if (username == null) {
            throw new UnauthorizedException();
        }
        var game = dataAccess.findGame(id);
        GameData newGame = null;
        if (Objects.equals(playerColor, "BLACK")){
            if (game.blackUsername() != null){
                throw new AlreadyTakenException();
            }
            newGame = new GameData(id, game.whiteUsername(), username, game.gameName(), game.game());
        }
        if (Objects.equals(playerColor, "WHITE")){
            if (game.whiteUsername() != null){
                throw new AlreadyTakenException();
            }
            newGame = new GameData(id, username, game.blackUsername(), game.gameName(), game.game());
        }
        dataAccess.updateGame(id, newGame);
    }
}
