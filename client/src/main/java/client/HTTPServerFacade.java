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

        private static final HttpClient httpClient =  HttpClient.newHttpClient();
        public HTTPServerFacade(int port){
            this.port = port;
        }
        static class AuthResponse {
            String authToken;
        }
        public void register(String username, String password, String email) throws ServerFacadeException{
            try {
                var body = Map.of("username", username, "password", password, "email", email);
                var response = post(port, "/user", body);
                this.authToken = new Gson().fromJson(response, AuthResponse.class).authToken;
            } catch(Exception e) {
                System.out.println("ERROR: Service responded with: " + e);
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
                class GameResponse {
                    List<GameData> games;
                }
                var response = get(port, "/game", "authorization", authToken);
                var gson = new Gson();
                var gameList = gson.fromJson(response, GameResponse.class);
                return gameList.games;
            } catch(Exception e) {
                System.out.println("ERROR: Service responded with: " + e);
                throw new ServerFacadeException("Server responded with error");
            }
        }

        public void login(String username, String password) throws ServerFacadeException {
            try {
                var body = Map.of("username", username, "password", password);
                var response = post(port, "/session", body);
                authToken = new Gson().fromJson(response, AuthResponse.class).authToken;
            } catch(Exception e) {
                System.out.println("ERROR: Service responded with: " + e);
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
            System.out.println("ERROR: Service responded with: " + e);
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
            System.out.println("ERROR: Service responded with: " + e);
            switch(e.getMessage()){
                case "400":
                    throw new ServerFacadeException("Bad request");
                case "401":
                    throw new ServerFacadeException("Invalid authorization");
                default:
                    throw new ServerFacadeException("Server responded with error");
            }
        }
    }

    @Override
    public void joinGame(String color, String gameName) throws ServerFacadeException {
        try {
            var body = Map.of("playerColor", color, "gameName", gameName);
            put(port, "/game", body, "authorization", authToken);
        } catch(Exception e) {
            System.out.println("ERROR: Service responded with: " + e);
            switch(e.getMessage()){
                case "400":
                    throw new ServerFacadeException("Bad request");
                case "401":
                    throw new ServerFacadeException("Invalid authorization");
                default:
                    throw new ServerFacadeException("Server responded with error");
            }
        }
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

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));

        if (headers != null && headers.length > 0){
            builder.headers(headers);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception(String.valueOf(response.statusCode()));
        }
    }
}
