package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import java.util.UUID;


import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTests {

    @Test
    void clearDB() {
        var dataAccess = new FakeDataAccess();
        var authService = new AuthService(dataAccess);
        assertDoesNotThrow(authService::clearDB);
        assert(dataAccess.calledMethod);
    }

    @Test
    void generateToken() {
        String token = AuthService.generateToken();
        UUID parsed = assertDoesNotThrow(() -> UUID.fromString(token));
        assertEquals(parsed.toString(), token);
    }
}