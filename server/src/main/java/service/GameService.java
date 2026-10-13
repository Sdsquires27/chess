package service;

import dataaccess.DataAccess;

public class GameService extends AuthService{

    public GameService(DataAccess dataAccess) {
        super(dataAccess);
    }
}
