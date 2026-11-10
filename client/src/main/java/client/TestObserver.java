package client;

import websocket.messages.ServerMessage;

public class TestObserver implements Observer {
    public ServerMessage message = null;
    public void reset() {
        this.message = null;
    }
    @Override
    public void notify(ServerMessage message) {
        this.message = message;
    }
}
