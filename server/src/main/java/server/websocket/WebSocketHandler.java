package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import service.requests.AuthTokenRequest;
import service.requests.FindGameRequest;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    UserService userService;
    GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

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
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, wsMessageContext.session);
                case MAKE_MOVE -> makeMove(command, wsMessageContext.session);
                case LEAVE -> leave(command, wsMessageContext.session);
                case RESIGN -> resign(command, wsMessageContext.session);
            }
        } catch (DataAccessException e) {
            sendError(new Error("unable to access data"), wsMessageContext.session);
        } catch (IOException e) {
            sendError(new Error("encountered error sending data"), wsMessageContext.session);
        }
    }

    private void connect(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        ConnectCommand command = (ConnectCommand) userGameCommand;
        var gameID = String.valueOf(command.getGameID());
        var username = userService.findUser(new AuthTokenRequest(command.getAuthToken()));
        connections.join(gameID, session);
        var game = gameService.findGame(new FindGameRequest(command.getGameID()));
        connections.send(session,
                new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game()));
         ServerMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                 "Player " + username + " joined as " + command.getColor().toString().toLowerCase());
        connections.broadcast(session, message, gameID);
    }

    private void sendError(Error error, Session session) {
        try {
            connections.send(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            error.getMessage()));
        } catch (IOException e){
            System.out.println("Unable to send message");
        }
    }

    private void makeMove(UserGameCommand command, Session session) {

    }

    private void leave(UserGameCommand command, Session session) {

    }

    private void resign(UserGameCommand command, Session session) {

    }
}
