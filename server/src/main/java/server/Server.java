package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.requests.*;

public class Server {

    private final Javalin javalin;
    private final DataAccess dataAccess;
    private final GameService gameService;
    private final UserService userService;

    public Server() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clearDB)
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void register(Context ctx) throws APIException {
        var header = ctx.headerMap();
        try {
            userService.RegisterUser(new RegisterRequest(
                    header.get("username"), header.get("password"), header.get("email")));
        } catch (InvalidInputException | AlreadyTakenException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void login(Context ctx) throws APIException {
        throw new RuntimeException("not implemented");
    }

    private void logout(Context ctx) throws APIException {
        throw new RuntimeException("not implemented");
    }

    private void listGames(Context ctx) throws APIException {
        throw new RuntimeException("not implemented");
    }

    private void createGame(Context ctx) throws APIException {
        throw new RuntimeException("not implemented");
    }

    private void joinGame(Context ctx) throws APIException {
        throw new RuntimeException("not implemented");
    }

    private void clearDB(Context ctx) throws APIException {
        try {
            gameService.clearDB();
        }
        catch (DataAccessException e) {
            throw new APIException(e.getMessage());
        }
    }

    private void exceptionHandler(DataAccessException exception, Context ctx){
        throw new RuntimeException("not implemented");
    }

    public void stop() {
        javalin.stop();
    }
}
