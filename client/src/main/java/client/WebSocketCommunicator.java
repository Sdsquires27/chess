package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import websocket.messages.ServerMessage;

public class WebSocketCommunicator {
    Observer observer;

    public WebSocketCommunicator(Observer observer) {
        this.observer = observer;
    }

    public void onMessage(String message) throws ServerFacadeException {
        try {
            var serverMessage = new Gson().fromJson(message, ServerMessage.class);
            observer.notify(serverMessage);
        } catch (JsonSyntaxException e) {
            throw new ServerFacadeException("Could not parse message");
        }

    }
}
