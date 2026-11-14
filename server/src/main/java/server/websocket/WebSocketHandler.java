package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("WebSocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("WebSocket connexted");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        UserGameCommand command = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, wsMessageContext.session);
            case MAKE_MOVE -> makeMove(command, wsMessageContext.session);
            case LEAVE -> leave(command, wsMessageContext.session);
            case RESIGN -> resign(command, wsMessageContext.session);
        }
    }

    private void connect(UserGameCommand command, Session session) {
        var gameID = String.valueOf(command.getGameID());
        connections.join(gameID, session);
        /*
         ServerMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                 "Player ");
        connections.broadcast(session, )
         */
    }

    private void makeMove(UserGameCommand command, Session session) {

    }

    private void leave(UserGameCommand command, Session session) {

    }

    private void resign(UserGameCommand command, Session session) {

    }
}
