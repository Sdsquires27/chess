package service;

import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

public class UserService extends AuthService{


    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public AuthData RegisterUser(RegisterRequest request) throws InvalidInputException, DataAccessException,
            AlreadyTakenException{
        if (!validateInput(request)){
            throw new InvalidInputException();
        }
        var user = dataAccess.findUser(request.username(), request.password());
        if (user != null) {
            throw new AlreadyTakenException();
        }
        dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        dataAccess.createAuth(new AuthData(AuthService.generateToken(), request.username()));
    }

    public AuthData LoginUser(LoginRequest request){
        throw new RuntimeException("Not implemented");
    }

    public void Logout(AuthTokenRequest request){
        throw new RuntimeException("Not implemented");
    }

    private boolean validateInput(RegisterRequest request){
        return true;
    }

    private boolean validateInput(LoginRequest request){
        throw new RuntimeException("Not implemented");
    }
}
