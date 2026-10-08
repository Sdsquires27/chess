package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
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
        throw new RuntimeException("not implemented");
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
        throw new RuntimeException("not implemented");
    }

    private void exceptionHandler(DataAccessException exception, Context ctx){
        throw new RuntimeException("not implemented");
    }

    public void stop() {
        javalin.stop();
    }
}
