package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import service.*;
import service.requests.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final DataAccess dataAccess;
    private final GameService gameService;
    private final AuthService authService;
    private final UserService userService;

    public Server() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        authService = new AuthService(dataAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clearDB)
                .exception(InvalidInputException.class,
                        (exception, ctx) -> exceptionHandler(400, "bad request", ctx))
                .exception(DataAccessException.class,
                        (exception, ctx) -> exceptionHandler(500, exception.getMessage(), ctx))
                .exception(AlreadyTakenException.class,
                        (exception, ctx) -> exceptionHandler(403, "already taken", ctx))
                .exception(UnauthorizedException.class,
                        (exception, ctx) -> exceptionHandler(401, "failed to authenticate user", ctx));

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
        respondWithAuthData(authData, ctx);
    }

    private void login(Context ctx) throws InvalidInputException, DataAccessException,
            UnauthorizedException{
        var body = (Map<String, String>) getBodyObject(ctx, Map.class);
        var authData = userService.LoginUser(new LoginRequest(
                body.get("username"), body.get("password")));
        respondWithAuthData(authData, ctx);
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
        authService.clearDB();
    }

    private void exceptionHandler(int code, String message, Context ctx){
        ctx.contentType("application/json");
        ctx.status(code).result(new Gson().toJson(Map.of("message", "Error: " + message)));
    }

    private void respondWithAuthData(AuthData authData, Context ctx) {
        String authReturn = new Gson().toJson(Map.of("username", authData.username(),
                "authToken", authData.authToken()));
        ctx.contentType("application/json");
        ctx.result(authReturn);
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
