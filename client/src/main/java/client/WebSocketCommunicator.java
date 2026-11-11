package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketCommunicator extends Endpoint {
    Observer observer;
    public Session session;
    public URI uri;

    public WebSocketCommunicator(Observer observer, int port) throws Exception {
        this.observer = observer;
        this.uri = URI.create(String.format("ws://localhost:%s/ws", port));
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;

        this.session.addMessageHandler((MessageHandler.Whole<String>) s -> {
            try {
                var serverMessage = new Gson().fromJson(s, ServerMessage.class);
                observer.notify(serverMessage);
            } catch (JsonSyntaxException e) {
                throw new ServerFacadeException("Could not parse message");
            }
        });
    }
}
