package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

public class UserService extends ChessService{


    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public AuthData RegisterUser(RegisterRequest request) throws InvalidInputException, DataAccessException,
            AlreadyTakenException{
        if (invalidInput(new String[]{request.username(), request.password(), request.email()})){
            throw new InvalidInputException();
        }

        var user = dataAccess.findUser(request.username(), request.password());
        if (user != null) {
            throw new AlreadyTakenException();
        }

        dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        var authData = new AuthData(AuthService.generateToken(), request.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData LoginUser(LoginRequest request) throws InvalidInputException, DataAccessException,
            UnauthorizedException{
        if(invalidInput(new String[]{request.username(), request.password()})) {
            throw new InvalidInputException();
        }
        var user = dataAccess.findUser(request.username(), request.password());
        if (user == null) {
            throw new UnauthorizedException();
        }
        var authData = new AuthData(AuthService.generateToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void Logout(AuthTokenRequest request) throws DataAccessException, UnauthorizedException{
        if (!dataAccess.authorized(request.authToken())) {
            throw new UnauthorizedException();
        }
        dataAccess.deleteSession(request.authToken());
    }


}
