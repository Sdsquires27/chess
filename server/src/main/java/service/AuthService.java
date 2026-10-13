package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

class AuthService {
    private final DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }
}
