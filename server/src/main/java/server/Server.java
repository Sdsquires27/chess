package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.requests.*;

import java.util.Map;

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
                .exception(InvalidInputException.class, this::invalidInputExceptionHandler)
                .exception(DataAccessException.class, this::dataAccessExceptionHandler)
                .exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void register(Context ctx) throws InvalidInputException,
            DataAccessException, AlreadyTakenException {
        var body = (Map<String, String>) getBodyObject(ctx, Map.class);
        var authData = userService.RegisterUser(new RegisterRequest(
                body.get("username"), body.get("password"), body.get("email")));
        String registerReturn = new Gson().toJson(Map.of("username", authData.username(),
        "authToken", authData.authToken()));
        ctx.contentType("application/json");
        ctx.result(registerReturn);
    }

    private void login(Context ctx) throws InvalidInputException, DataAccessException,
            UnauthorizedException{
        throw new RuntimeException("not implemented");
    }

    private void logout(Context ctx) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    private void listGames(Context ctx) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    private void createGame(Context ctx) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    private void joinGame(Context ctx) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    private void clearDB(Context ctx) throws DataAccessException {
        gameService.clearDB();
    }

    private void invalidInputExceptionHandler(InvalidInputException exception, Context ctx){
        ctx.contentType("application/json");
        ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
    }

    private void dataAccessExceptionHandler(DataAccessException exception, Context ctx){
        ctx.contentType("application/json");
        ctx.status(500).result(new Gson().toJson(Map.of("message", exception.getMessage())));
    }

    private void alreadyTakenExceptionHandler(AlreadyTakenException exception, Context ctx){
        ctx.contentType("application/json");
        ctx.status(403).result(new Gson().toJson(Map.of("message", "Error: already taken")));
    }

    public void stop() {
        javalin.stop();
    }

    private static <T> T getBodyObject(Context context, Class<T> tClass) {
        var bodyObject = new Gson().fromJson(context.body(), tClass);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }
}
