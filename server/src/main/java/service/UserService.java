package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.*;

public class UserService extends ChessService{


    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public AuthData registerUser(RegisterRequest request) throws InvalidInputException, DataAccessException,
            AlreadyTakenException{
        if (invalidInput(new String[]{request.username(), request.password(), request.email()})){
            throw new InvalidInputException();
        }

        var user = dataAccess.findUser(request.username());
        if (user != null) {
            throw new AlreadyTakenException();
        }
        var hash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        dataAccess.createUser(new UserData(request.username(), hash, request.email()));
        var authData = new AuthData(AuthService.generateToken(), request.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData loginUser(LoginRequest request) throws InvalidInputException, DataAccessException,
            UnauthorizedException{
        if(invalidInput(new String[]{request.username(), request.password()})) {
            throw new InvalidInputException();
        }
        var user = dataAccess.findUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException();
        }
        var authData = new AuthData(AuthService.generateToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(AuthTokenRequest request) throws DataAccessException, UnauthorizedException{
        if (!dataAccess.authorized(request)) {
            throw new UnauthorizedException();
        }
        dataAccess.deleteSession(request);
    }


}
