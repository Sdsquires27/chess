package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.AuthTokenRequest;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    static DataAccess dataAccess;
    UserData user = new UserData("username", "password", "email");

    @BeforeAll
    static void setDataAccess() {
        dataAccess = assertDoesNotThrow(SQLDataAccess::new);
    }

    @BeforeEach
    void clearDB() {
        assertDoesNotThrow(dataAccess::clearDB);

        // Test Create Auth
        assertDoesNotThrow(() -> dataAccess.createAuth(
                new AuthData("token", "username")));

        // Test Create User
        assertDoesNotThrow(() -> dataAccess.createUser(user));
    }

    @Test
    void authorized() {
        var result = assertDoesNotThrow(() -> dataAccess.authorized(
                new AuthTokenRequest("token")));
        assert(result);
        result = assertDoesNotThrow(() -> dataAccess.authorized(
                new AuthTokenRequest("fake token")));
        assert(!result);
    }

    @Test
    void createGame() {
    }

    @Test
    void findGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void deleteSession() {
        assertDoesNotThrow(() -> dataAccess.deleteSession(
                new AuthTokenRequest("token")));
        assert(!assertDoesNotThrow(() -> dataAccess.authorized(
                new AuthTokenRequest("token"))));
        assertDoesNotThrow(() -> dataAccess.deleteSession(
                new AuthTokenRequest("token")));
    }

    @Test
    void findUser() {
        var user = assertDoesNotThrow(() -> dataAccess.findUser("username"));
        assertEquals(user, this.user);
        user = assertDoesNotThrow(() -> dataAccess.findUser("fake user"));
        assertNull(user);
        var username = assertDoesNotThrow(() -> dataAccess.findUser(
                new AuthTokenRequest("token")
        ));
        assertEquals("username", username);
        username = assertDoesNotThrow(() -> dataAccess.findUser(
                new AuthTokenRequest("fake token")
        ));
        assertNull(username);
    }

    @Test
    void updateGame() {
    }
}