package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, HashMap<Session, String>> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Session, String> sessionToGameID = new ConcurrentHashMap<>();

    public void join(String gameID, Session session, String authToken) {
        connections.computeIfAbsent(gameID, id -> new HashMap<>())
                .put(session, authToken);
        sessionToGameID.putIfAbsent(session, gameID);
    }

    public void leave(String gameID, Session session){
        connections.get(gameID).remove(session);
        sessionToGameID.remove(session);
    }

    public String findGameID(Session session) {
        return sessionToGameID.get(session);
    }

    public String findAuth(Session session) {
        return connections.get(sessionToGameID.get(session)).get(session);
    }

    public void broadcast(Session excludeSession, ServerMessage message, String gameID) throws IOException {
        String stringMessage = new GsonBuilder().enableComplexMapKeySerialization().create().toJson(message);
        for (Session session : connections.get(gameID).keySet()) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(stringMessage);
                }
            }
        }
    }

    public void send(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            var stringMessage = new Gson().toJson(message);
            session.getRemote().sendString(stringMessage);
        }
    }
}
