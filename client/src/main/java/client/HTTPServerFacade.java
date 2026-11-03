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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class HTTPServerFacade implements ServerFacade{
        int port;
        String authToken;

        private static final HttpClient httpClient =  HttpClient.newHttpClient();
        public HTTPServerFacade(int port){
            this.port = port;
        }

        public void register(String username, String password, String email) throws ServerFacadeException{

        }

        public Collection<GameData> listGames() throws ServerFacadeException {
            try {
                class GameResponse {
                    List<GameData> games;
                }
                var response = get("/game", "authToken", authToken);
                var gson = new Gson();
                var gameList = gson.fromJson(response, GameResponse.class);
                return gameList.games;
            } catch(Exception e) {
                System.out.println(e);
                throw new ServerFacadeException("Server did not return correctly");
            }
        }

        public void login(String username, String password) throws ServerFacadeException {

        }

    @Override
    public void logout() throws ServerFacadeException {

    }

    @Override
    public void createGame(String gameName) throws ServerFacadeException {

    }

    @Override
    public void joinGame(String gameName) throws ServerFacadeException {

    }

    private String get(String path, String... headers) throws Exception{
            String url = String.format(Locale.getDefault(), "http://localhost:%d%s", this.port, path);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofMillis(5000))
                    .headers(headers)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                throw new Exception("Error: received status code: " + response.statusCode());
            }
        }
}
