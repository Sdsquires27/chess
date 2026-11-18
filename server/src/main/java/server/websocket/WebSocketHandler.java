package server.websocket;

import chess.*;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;
import service.requests.AuthTokenRequest;
import service.requests.FindGameRequest;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static class WebSocketException extends Exception {
        public WebSocketException(String message) {
            super(message);
        }
    }
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
        String auth = connections.findAuth(wsCloseContext.session);
        int gameID = Integer.parseInt(connections.findGameID(wsCloseContext.session));
        var username = userService.findUser(new AuthTokenRequest(auth));
        var game = gameService.findGame(new FindGameRequest(gameID));
        gameService.updateGame(removePlayerFromGame(username, game));
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        UserGameCommand command = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, wsMessageContext.session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class), wsMessageContext.session);
                case LEAVE -> leave(command, wsMessageContext.session);
                case RESIGN -> resign(command, wsMessageContext.session);
            }
        } catch (DataAccessException e) {
            sendError(new Error("unable to access data"), wsMessageContext.session);
        } catch (IOException e) {
            sendError(new Error("encountered error sending data"), wsMessageContext.session);
        } catch (WebSocketException e) {
            sendError(new Error(e.getMessage()), wsMessageContext.session);
        } catch (UnauthorizedException e) {
            sendError(new Error("unable to authenticate user"), wsMessageContext.session);
        } catch (InvalidMoveException e) {
            sendError(new Error("invalid move"), wsMessageContext.session);
        }
    }

    private void connect(UserGameCommand command, Session session) throws DataAccessException, IOException,
            WebSocketException {
        System.out.println("Connecting player");
        var gameID = String.valueOf(command.getGameID());
        var username = userService.findUser(new AuthTokenRequest(command.getAuthToken()));
        if (username == null) {
            throw new UnauthorizedException();
        }
        connections.join(gameID, session, command.getAuthToken());
        var game = gameService.findGame(new FindGameRequest(command.getGameID()));
        if (game == null) {
            throw new WebSocketException("invalid game ID");
        }
        connections.send(session,
                new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game()));
        String color = Objects.equals(game.blackUsername(), username) ? "black." :
                Objects.equals(game.whiteUsername(), username) ? "white." : "an observer.";
         ServerMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                 "Player " + username + " joined as " + color);
        connections.broadcast(session, message, gameID);
    }

    private void sendError(Error error, Session session) {
        try {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    error.getMessage());
            connections.send(session, errorMessage);
        } catch (IOException e){
            System.out.println("Unable to send message");
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws DataAccessException,
            WebSocketException, InvalidMoveException, IOException {
        var user = userService.findUser(new AuthTokenRequest(command.getAuthToken()));
        var gameData = gameService.findGame(new FindGameRequest(command.getGameID()));
        var game = getGame(gameData, user);
        if (game.state != ChessGame.GameState.ONGOING) {
            throw new WebSocketException("Game is no longer ongoing");
        }
        var move = command.getMove();
        game.makeMove(move);
        gameService.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game));
        connections.broadcast(null, new LoadGameMessage(
                ServerMessage.ServerMessageType.LOAD_GAME, game),
                String.valueOf(gameData.gameID()));
        connections.broadcast(session, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, user + " has moved " + moveName(move)
                ), String.valueOf(gameData.gameID()));
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.state = ChessGame.GameState.WHITE_VICTORY;
            connections.broadcast(null, new NotificationMessage(
               ServerMessage.ServerMessageType.NOTIFICATION, gameData.blackUsername() + " is in checkmate! " +
                    gameData.whiteUsername() + " has won!"
            ), String.valueOf(gameData.gameID()));
            return;
        }
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.state = ChessGame.GameState.BLACK_VICTORY;
            connections.broadcast(null, new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION, gameData.whiteUsername() +
                    " is in checkmate! " + gameData.blackUsername() + " has won!"
            ), String.valueOf(gameData.gameID()));
            return;
        }
        if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            connections.broadcast(null, new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    gameData.blackUsername() + " is in check!"), String.valueOf(gameData.gameID()));
        }
        if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            connections.broadcast(null, new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION, gameData.whiteUsername() + " is in check!"
            ), String.valueOf(gameData.gameID()));
        }
    }

    private String moveName(ChessMove move) {
        StringBuilder moveName = new StringBuilder();
        for (var position : new ChessPosition[] {move.getStartPosition(), move.getEndPosition()}){
            moveName.append(switch(position.getColumn()) {
                case 1 -> "a";
                case 2 -> "b";
                case 3 -> "c";
                case 4 -> "d";
                case 5 -> "e";
                case 6 -> "f";
                case 7 -> "g";
                case 8 -> "h";
                default -> throw new RuntimeException("Invalid chess position");
            });
            moveName.append(position.getRow());
        }

        return moveName.toString();
    }

    private static ChessGame getGame(GameData gameData, String user) throws WebSocketException {
        String playerColor = Objects.equals(gameData.blackUsername(), user) ?
                "black" : Objects.equals(gameData.whiteUsername(), user) ?
                "white" : "observer";
        if (playerColor.equals("observer")) {
            throw new WebSocketException("Observers cannot make moves.");
        }
        var game = gameData.game();
        if ((game.getTeamTurn() == ChessGame.TeamColor.BLACK && !playerColor.equals("black")) ||
                (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !playerColor.equals("white"))) {
            throw new WebSocketException("Move cannot be made outside of player turn.");
        }
        return game;
    }

    private void leave(UserGameCommand command, Session session) throws DataAccessException, IOException {
        var user = userService.findUser(new AuthTokenRequest(command.getAuthToken()));
        var game = gameService.findGame(new FindGameRequest(command.getGameID()));
        gameService.updateGame(removePlayerFromGame(user, game));
        connections.leave(String.valueOf(game.gameID()), session);
        connections.broadcast(session,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        user + " has left the game."),
                String.valueOf(game.gameID()));
    }

    private GameData removePlayerFromGame(String user, GameData game) {
        String playerColor = Objects.equals(game.blackUsername(), user) ?
                "black" : Objects.equals(game.whiteUsername(), user) ?
                "white" : "observer";
        if (playerColor.equals("black")) {
            return new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(),
                    game.game());
        } else if (playerColor.equals("white")) {
            return new GameData(game.gameID(), null, game.blackUsername(), game.gameName(),
                    game.game());
        }
        return game;

    }

    private void resign(UserGameCommand command, Session session) throws DataAccessException,
            IOException, WebSocketException {
        var user = userService.findUser(new AuthTokenRequest(command.getAuthToken()));
        var game = gameService.findGame(new FindGameRequest(command.getGameID()));
        String playerColor = Objects.equals(game.blackUsername(), user) ?
                "black" : Objects.equals(game.whiteUsername(), user) ?
                "white" : "observer";
        if (playerColor.equals("observer")) {
            throw new WebSocketException("Observers cannot resign.");
        }
        var victor = playerColor.equals("black") ?
                "white" : "black";
        if (game.game().state != ChessGame.GameState.ONGOING) {
            throw new WebSocketException("Game is over; player cannot resign");
        }
        game.game().state = playerColor.equals("white") ?
                ChessGame.GameState.WHITE_VICTORY : ChessGame.GameState.BLACK_VICTORY;
        gameService.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                game.gameName(), game.game()));
        connections.broadcast(null, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, user + " resigned. " + victor + " wins!"),
                String.valueOf(command.getGameID()));
    }
}
