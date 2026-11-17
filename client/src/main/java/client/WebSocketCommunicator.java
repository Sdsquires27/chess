package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.websocket.*;
import websocket.messages.*;

import java.net.URI;

public class WebSocketCommunicator extends Endpoint {
    Observer observer;
    private Session session;

    public WebSocketCommunicator(Observer observer, int port) throws Exception {
        this.observer = observer;
        URI uri = URI.create(String.format("ws://localhost:%s/ws", port));
        var container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                try {
                    Gson gson = new Gson();
                    var serverMessage = gson.fromJson(s, ServerMessage.class);
                    switch(serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> observer.notify(gson.fromJson(s, LoadGameMessage.class));
                        case ERROR -> observer.notify(gson.fromJson(s, ErrorMessage.class));
                        case NOTIFICATION -> observer.notify(gson.fromJson(s, NotificationMessage.class));
                    }
                } catch (JsonSyntaxException e) {
                    throw new ServerFacadeException("Could not parse message");
                }
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
