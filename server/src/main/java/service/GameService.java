package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

import java.util.Collection;

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

    public String createGame(CreateGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException{
        throw new RuntimeException("Not implemented");
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException, InvalidInputException,
            UnauthorizedException, AlreadyTakenException{

    }
}
