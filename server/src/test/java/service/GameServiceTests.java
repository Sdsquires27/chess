package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests extends ServiceTests{

    GameService gameService = new GameService(dataAccess);

    @Test
    void listGames() {
        assertThrows(UnauthorizedException.class,
                () -> gameService.listGames(new AuthTokenRequest("FakeAuth")));
        var games = assertDoesNotThrow(() -> gameService.listGames(new AuthTokenRequest(dataAccess.defaultAuth)));
        assertEquals(games, dataAccess.gameList);
        assertMethodsAccessed(new String[] {"authorized", "listGames"});
    }

    @Test
    void createGame() {
        assertThrows(UnauthorizedException.class,
                () -> gameService.createGame(new CreateGameRequest("FakeAuth", "TestGame")));
        assertThrows(InvalidInputException.class,
                () -> gameService.createGame(new CreateGameRequest(dataAccess.defaultAuth, "")));
        assertThrows(InvalidInputException.class,
                () -> gameService.createGame(new CreateGameRequest(dataAccess.defaultAuth, null)));
        var id = assertDoesNotThrow(() -> gameService.createGame(new CreateGameRequest(dataAccess.defaultAuth,
                "NewGame")));
        assertEquals(id, dataAccess.game.gameID());
        assertEquals("NewGame", dataAccess.game.gameName());
        assertMethodsAccessed(new String[] {"authorized", "createGame"});
    }

    @Test
    void joinGame() {
    }
}
