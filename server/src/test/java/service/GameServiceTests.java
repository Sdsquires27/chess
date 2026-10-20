package service;

import model.GameData;
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
        assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame(new JoinGameRequest("FakeAuth", "1234", "WHITE")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "ID", "WHITE")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "", "WHITE")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "1234", "")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "1234", "")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "", "WHITE")));
        assertThrows(InvalidInputException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth, "1234", "WH!TE")));
        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth,
                        String.valueOf(dataAccess.defaultWhiteGame.gameID()), "WHITE")));
        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth,
                        String.valueOf(dataAccess.defaultBlackGame.gameID()), "BLACK")));
        assertDoesNotThrow(() -> gameService.joinGame(new JoinGameRequest(dataAccess.defaultAuth,
                String.valueOf(dataAccess.defaultBlackGame.gameID()), "WHITE")));
        assertEquals(dataAccess.game, new GameData(dataAccess.defaultBlackGame.gameID(), dataAccess.defaultUser.username(),
                dataAccess.defaultBlackGame.blackUsername(), dataAccess.defaultBlackGame.gameName(),
                dataAccess.defaultBlackGame.game()));
        assertMethodsAccessed(new String[] {"findUser", "findGame", "updateGame"});
    }
}
