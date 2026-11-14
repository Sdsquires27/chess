package server.websocket;

import com.google.gson.Gson;
import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Set<Session>> connections = new ConcurrentHashMap<>();

    public void join(String gameID, Session session) {
        connections.computeIfAbsent(gameID, id -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void leave(String gameID, Session session){
        connections.get(gameID).remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage message, String gameID) {
        String stringMessage = new Gson().toJson(message);
        for (Session session : connections.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getAsyncRemote().sendText(stringMessage, sendResult -> {
                        if (!sendResult.isOK()) {
                            System.out.println("ERROR: WebSocket send failed: " + sendResult.getException());
                        }
                    });
                }
            }
        }
    }
}
