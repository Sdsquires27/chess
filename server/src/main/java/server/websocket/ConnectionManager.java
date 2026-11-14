package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
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

    public void broadcast(Session excludeSession, ServerMessage message, String gameID) throws IOException {
        String stringMessage = new Gson().toJson(message);
        for (Session session : connections.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(stringMessage);
                }
            }
        }
    }
}
