package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        facade = new HTTPServerFacade(port);
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

}
