package service;

import dataaccess.DataAccess;
import model.AuthData;
import service.requests.*;

public class UserService extends AuthService{


    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public AuthData RegisterUser(RegisterRequest request) throws InvalidInputException{
        throw new RuntimeException("Not implemented");
    }

    public AuthData LoginUser(LoginRequest request){
        throw new RuntimeException("Not implemented");
    }

    public void Logout(AuthTokenRequest request){
        throw new RuntimeException("Not implemented");
    }

    private boolean validateInput(RegisterRequest request){
        throw new RuntimeException("Not implemented");
    }

    private boolean validateInput(LoginRequest request){
        throw new RuntimeException("Not implemented");
    }
}
