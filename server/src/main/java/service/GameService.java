package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

import java.util.Collection;
import java.util.Random;

public class GameService extends ChessService{

    public GameService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public Collection<GameData> listGames(AuthTokenRequest request) throws DataAccessException, UnauthorizedException{
        if (!dataAccess.authorized(request.authToken())) {
            throw new UnauthorizedException();
        }
        return dataAccess.listGames();
    }

    public int createGame(CreateGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException{
        if (invalidInput(new String[] {request.gameName()})) {
            throw new InvalidInputException();
        }
        if (!dataAccess.authorized(request.authToken())) {
            throw new UnauthorizedException();
        }
        int id = new Random().nextInt();
        dataAccess.createGame(request.gameName(), id);
        return id;
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException, AlreadyTakenException{

    }
}
