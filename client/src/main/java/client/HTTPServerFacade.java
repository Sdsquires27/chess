package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class HTTPServerFacade implements ServerFacade{
        int port;
        String authToken;
        Observer observer;

        private static final HttpClient HTTP_CLIENT =  HttpClient.newHttpClient();
        public HTTPServerFacade(int port, Observer observer){
            this.port = port;
            this.observer = observer;

        }
        static class AuthResponse {
            String authToken;
        }
        static class GameResponse {
            List<GameData> games;
        }
        public void register(String username, String password, String email) throws ServerFacadeException{
            try {
                var body = Map.of("username", username, "password", password, "email", email);
                var response = post(port, "/user", body);
                this.authToken = new Gson().fromJson(response, AuthResponse.class).authToken;
            } catch(Exception e) {
                switch(e.getMessage()){
                    case "400":
                        throw new ServerFacadeException("Bad request");
                    case "403":
                        throw new ServerFacadeException("Username already taken");
                    default:
                        throw new ServerFacadeException("Server responded with error");
                }
            }
        }

        public Collection<GameData> listGames() throws ServerFacadeException {
            try {
                var response = get(port, "/game", "authorization", authToken);
                return new Gson().fromJson(response, GameResponse.class).games;
            } catch(Exception e) {
                throw new ServerFacadeException("Server responded with error");
            }
        }

        public void login(String username, String password) throws ServerFacadeException {
            try {
                var body = Map.of("username", username, "password", password);
                var response = post(port, "/session", body);
                authToken = new Gson().fromJson(response, AuthResponse.class).authToken;
            } catch(Exception e) {
                switch(e.getMessage()) {
                    case "400":
                        throw new ServerFacadeException("Bad request");
                    case "401":
                        throw new ServerFacadeException("Failed to authenticate user");
                    default:
                        throw new ServerFacadeException("Server responded with error");
                }
            }
        }

    @Override
    public void logout() throws ServerFacadeException {
        try {
            delete(port, "/session", "authorization", authToken);
            authToken = null;
        } catch(Exception e) {
            if (e.getMessage().equals("401")) {
                throw new ServerFacadeException("Failed to log out user");
            }
            throw new ServerFacadeException("Server responded with error");
        }
    }

    @Override
    public void createGame(String gameName) throws ServerFacadeException {
        try {
            var body = Map.of("gameName", gameName);
            post(port, "/game", body, "authorization", authToken);
        } catch(Exception e) {
            joinOrCreateException(e);
        }
    }

    private void joinOrCreateException(Exception e){
        switch(e.getMessage()){
            case "400":
                throw new ServerFacadeException("Bad request");
            case "401":
                throw new ServerFacadeException("Invalid authorization");
            default:
                throw new ServerFacadeException("Server responded with error");
        }
    }

    @Override
    public void joinGame(String color, String gameID) throws ServerFacadeException {
        try {
            var body = Map.of("playerColor", color, "gameID", gameID);
            put(port, "/game", body, "authorization", authToken);
        } catch(Exception e) {
            joinOrCreateException(e);
        }
    }

    @Override
    public void onMessage(String message) throws ServerFacadeException {

    }

    static String get(int port, String path, String... headers) throws Exception{
            String url = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

            var builder = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofMillis(5000))
                    .GET();

            if (headers != null && headers.length > 0){
                builder.headers(headers);
            }
            HttpRequest request = builder.build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                throw new Exception(String.valueOf(response.statusCode()));
            }
        }

    static String delete(int port, String path, String... headers) throws Exception{
        String url = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

        var builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .DELETE();

        if (headers != null && headers.length > 0){
            builder.headers(headers);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception(String.valueOf(response.statusCode()));
        }
    }

    static String post(int port, String path, Map<String, String> body, String... headers) throws Exception{
        String url = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

        var builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));

        if (headers != null && headers.length > 0){
            builder.headers(headers);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception(String.valueOf(response.statusCode()));
        }
    }

    static String put(int port, String path, Map<String, String> body, String... headers) throws Exception{
        String url = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

        var builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .PUT(HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));

        if (headers != null && headers.length > 0){
            builder.headers(headers);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception(String.valueOf(response.statusCode()));
        }
    }
}
