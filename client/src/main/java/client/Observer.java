package client;

import websocket.messages.ServerMessage;

public interface Observer {
    public void notify(ServerMessage message);
}
