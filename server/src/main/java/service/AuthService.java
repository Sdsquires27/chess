package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.UUID;

class AuthService {
    final DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
