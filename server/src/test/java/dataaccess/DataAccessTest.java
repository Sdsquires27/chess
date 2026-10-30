package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.AuthTokenRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    static DataAccess dataAccess;
    UserData user = new UserData("username", "password", "email");
    List<GameData> games = new ArrayList<>(Arrays.asList(
            new GameData(1, null, null, "game", new ChessGame()),
            new GameData(2, null, null, "game2", new ChessGame())));

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

        // Test Create Game
        for (var game : games) {
            assertDoesNotThrow(() -> dataAccess.createGame(game.gameName(), game.gameID()));
        }
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
    void findGame() {
        for (var game : games){
            var result = assertDoesNotThrow(() -> dataAccess.findGame(game.gameID()));
            assertEquals(game, result);
        }
        assertNull(assertDoesNotThrow(() -> dataAccess.findGame(1234)));
    }

    @Test
    void listGames() {
        var games = assertDoesNotThrow(() -> dataAccess.listGames());
        assertEquals(games, this.games);
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
        var newGameData = new GameData(1, "white", null,
                "game1", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.updateGame(1, newGameData));
        assertEquals(newGameData, assertDoesNotThrow(() -> dataAccess.findGame(1)));
    }
}