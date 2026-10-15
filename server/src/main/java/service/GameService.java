package service;

import dataaccess.DataAccess;

public class GameService extends ChessService{

    public GameService(DataAccess dataAccess) {
        super(dataAccess);
    }
}
