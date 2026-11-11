package client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

public class HTTPCommunicator {

    private static final HttpClient HTTP_CLIENT =  HttpClient.newHttpClient();

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

    static void put(int port, String path, Map<String, String> body, String... headers) throws Exception{
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
            response.body();
        } else {
            throw new Exception(String.valueOf(response.statusCode()));
        }
    }
}
