package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.AuthTokenRequest;

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
    }

    @Test
    void joinGame() {
    }
}
