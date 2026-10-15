package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.UUID;

public class AuthService extends ChessService {
    public AuthService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
