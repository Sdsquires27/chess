package client;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import server.Server;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;
    private static final TestObserver observer = new TestObserver();

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        facade = new HTTPServerFacade(port, observer);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void resetDatabase(){
        try {
            HTTPServerFacade.delete(port, "/db");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        try {
            HTTPServerFacade.delete(port, "/db");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        server.stop();
    }


    @Test
    public void register() {
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertThrows(ServerFacadeException.class,
                () -> facade.register("", "password", "email"));
        assertThrows(ServerFacadeException.class,
                () -> facade.register("newUsername", "", "email"));
        assertThrows(ServerFacadeException.class,
                () -> facade.register("newUsername", "password", ""));
        assertThrows(ServerFacadeException.class,
                () -> facade.register("username", "newPassword", "newEmail"));
        assertDoesNotThrow(() -> facade.register("newUsername", "password", "email"));
    }

    @Test
    public void login() {
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertThrows(ServerFacadeException.class,
                () -> facade.login("newUsername", "password"));
        assertThrows(ServerFacadeException.class,
                () -> facade.login("username", "newPassword"));
        assertThrows(ServerFacadeException.class,
                () -> facade.login("", "password"));
        assertThrows(ServerFacadeException.class,
                () -> facade.login("username", ""));
        assertDoesNotThrow(() -> facade.login("username", "password"));
    }

    @Test
    public void logout() {
        assertThrows(ServerFacadeException.class, () -> facade.logout());
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertDoesNotThrow(() -> facade.logout());
        assertThrows(ServerFacadeException.class, () -> facade.logout());
    }

    @Test
    public void createGame() {
        assertThrows(ServerFacadeException.class, () -> facade.createGame("newGame"));
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertThrows(ServerFacadeException.class, () -> facade.createGame(""));
        assertDoesNotThrow(() -> facade.createGame("newGame"));
    }

    @Test
    public void listGames() {
        assertThrows(ServerFacadeException.class, () -> facade.listGames());
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertDoesNotThrow(() -> facade.createGame("newGame"));
        var size = assertDoesNotThrow(() -> facade.listGames().size());
        assertEquals(1, size);
    }

    @Test
    public void joinGame() {
        assertDoesNotThrow(() -> facade.register("username", "password", "email"));
        assertDoesNotThrow(() -> facade.createGame("newGame"));
        assertThrows(ServerFacadeException.class, () -> facade.joinGame("", "1234"));
        assertThrows(ServerFacadeException.class, () -> facade.joinGame("BLACK", ""));
        var game = assertDoesNotThrow(() -> facade.listGames()).iterator().next();
        assertDoesNotThrow(() -> facade.joinGame("WHITE", String.valueOf(game.gameID())));
        assertDoesNotThrow(() -> facade.logout());
        assertThrows(ServerFacadeException.class, () -> facade.joinGame("BLACK", String.valueOf(game.gameID())));
    }

    @Test
    public void onMessage() {
        observer.reset();
        NotificationMessage notificationMessage = new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,"Test");
        Gson gson = new Gson();
        assertThrows(ServerFacadeException.class, () -> facade.onMessage("Test"));
        assertDoesNotThrow(() -> facade.onMessage(gson.toJson(notificationMessage)));
        assertEquals(notificationMessage, observer.message);
    }

}
