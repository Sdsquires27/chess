package client;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import ui.EscapeSequences;

public class Repl implements ClientOutput {
    Scanner scanner = new Scanner(System.in);
    ClientController controller;
    BoardRenderer renderer;
    ChessGame game;

    Repl(int port) throws Exception {
        this.controller = new ClientController(port, this);
    }

    @Override
    public void sendNotification(String notification) {
        printLine("%sNOTIFICATION: %s%s", EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_BLACK, notification);
    }

    @Override
    public void sendError(String error) {
        printError("ERROR: " + error);
    }

    @Override
    public void sendGame(ChessGame game) {
        printLine("Game sent");
        this.game = game;
        printLine(renderer.displayBoard(game.getBoard()));
    }

    private static class ReplException extends Exception {
        public ReplException(String message) {
            super(message);
        }
    }

    public void run() {
        resetTextColors();
        System.out.println("Welcome to Chess! Type help for a list of commands.");
        while(!controller.state.equals(State.QUIT)){
            String input = scanner.nextLine();
            if (controller.state.equals(State.SIGNED_OUT)){
                handlePreLoginInput(input);
            } else if (controller.state.equals(State.SIGNED_IN)) {
                handlePostLoginInput(input);
            } else if (controller.state.equals(State.IN_GAME)) {
                handleInGameInput(input);
            }
        }
    }

    private void handlePreLoginInput(String input) {
        var parameters = input.split(" ");
        switch(parameters[0].toLowerCase()){
            case "help":
                printLine("register <USERNAME> <PASSWORD> <EMAIL>%s - to create an account",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("login <USERNAME> <PASSWORD>%s - to login to an account",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("quit%s - to stop program",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("help%s - to list commands",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                return;
            case "register":
                if (invalidParameters(4, parameters.length)){
                    return;
                }
                try {
                    controller.register(parameters[1], parameters[2], parameters[3]);
                    printLine("Logged in as %s", parameters[1]);
                } catch (Exception e) {
                    printError("ERROR: " + e.getMessage());
                }
                return;
            case "login":
                if (invalidParameters(3, parameters.length)){
                    return;
                }
                try {
                    controller.login(parameters[1], parameters[2]);
                    printLine("Logged in as %s", parameters[1]);
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "quit":
                controller.quit();
                printLine("Bye!");
                return;
            default:
                printError("ERROR: command not recognized. Type help for a list of commands.");
        }
    }

    private void handlePostLoginInput(String input) {
        var parameters = input.split(" ");
        switch (parameters[0].toLowerCase()) {
            case "help":
                printLine("create <NAME>%s - to create a chess game",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("list%s - to list all games",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("join <ID> [WHITE|BLACK]%s - to join a game as a color",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("observe <ID>%s - to observe a game as a color",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("logout%s - to logout of an account",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("quit%s - to stop program",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("help%s - to list commands",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                return;
            case "create":
                if (invalidParameters(2, parameters.length)){
                    return;
                }
                try {
                    var numGames = controller.create(parameters[1]);
                    printLine("Created game %s;%s ID: %s", parameters[1],
                            EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY,
                            numGames);
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "list":
                try {
                    list();
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "join":
                try {
                    join(parameters);
                } catch(ServerFacadeException | ClientControlException | BoardRendererException e) {
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "observe":
                try {
                    observe(parameters);
                } catch(ServerFacadeException | ClientControlException | BoardRendererException e) {
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "logout":
                try {
                    controller.logout();
                    printLine("Logged out.");
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "quit":
                controller.quit();
                return;
            default:
                printError("ERROR: command not recognized. Type help for a list of commands.");
        }
    }

    private void handleInGameInput(String input) {
        var parameters = input.split(" ");
        switch (parameters[0].toLowerCase()) {
            case "help":
                printLine("redraw%s - to redraw the board",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("leave%s - to exit the game",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("move <POSITION> <POSITION>%s - to move a piece",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("resign%s - to forfeit the game",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("highlight <POSITION>%s - highlight legal moves for a piece",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                printLine("help%s - to list commands",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                return;
            case "redraw":
                printLine(renderer.displayBoard(game.getBoard()));
                return;
            case "leave":
                controller.leave();
                printLine("Left game successfully.");
                return;
            case "move":
                try {
                    move(parameters);
                } catch (ReplException e) {
                    printError(e.getMessage());
                }
                return;
            case "resign":
                printLine("Are you sure you want to quit? (y/n)");
                input = scanner.nextLine();
                if (input.charAt(0) == 'y') {
                    controller.resign();
                }
                return;
            case "highlight":
                try {
                    highlight(parameters);
                } catch (ReplException e) {
                    printError(e.getMessage());
                }
                return;
            default:
                printError("ERROR: command not recognized. Type help for a list of commands.");
        }
    }

    private void move(String[] parameters) throws ReplException{
        if (invalidParameters(3, parameters.length)){
            return;
        }
        var startPosition = stringToPosition(parameters[1]);
        ChessMove move = new ChessMove(startPosition,
                stringToPosition(parameters[2]), null);
        if (!game.validMoves(startPosition).contains(move)){
            throw new ReplException("ERROR: invalid move");
        }
        controller.move(move);
    }

    private void highlight(String[] parameters) throws ReplException{
        if (invalidParameters(2, parameters.length)){
            return;
        }
        ChessPosition position;
        position = stringToPosition(parameters[1]);

        var moves = game.validMoves(position);
        if (moves == null) {
            throw new ReplException("ERROR: no piece on position selected.");
        }
        printLine(renderer.highlightBoard(game.getBoard(), position, (ArrayList<ChessMove>) moves));
    }

    private void join(String[] parameters) {
        if (invalidParameters(3, parameters.length)){
            return;
        }
        renderer = new BoardRenderer(parameters[2]);
        game = controller.join(parameters[1], parameters[2]);
        printLine("Joined game successfully!");
        printLine(renderer.displayBoard(game.getBoard()));
    }

    private void observe(String[] parameters) {
        if (invalidParameters(2, parameters.length)){
            return;
        }
        var game = controller.findGame(parameters[1]);
        printLine("Joined game successfully!");
        var renderer = new BoardRenderer("WHITE");
        printLine(renderer.displayBoard(game.game().getBoard()));
    }

    private void list() {
        var games = controller.listGames();
        if (games.isEmpty()) {
            printLine("There are currently no games!");
            return;
        }
        printLine("Current games:");
        for (var i = 0; i < games.size(); i++) {
            var game = games.get(i);
            printLine("%s%s.%s - %s; %sWHITE: %s | BLACK: %s",
                    EscapeSequences.SET_TEXT_COLOR_BLUE,
                    i + 1,
                    EscapeSequences.SET_TEXT_COLOR_BLACK,
                    game.gameName(),
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY,
                    game.whiteUsername() == null ? "" : game.whiteUsername(),
                    game.blackUsername() == null ? "" : game.blackUsername());
        }
    }

    private static ChessPosition stringToPosition(String position) throws ReplException{
        if (position.length() != 2) {
            throw new ReplException("Incorrect number of characters");
        }
        int row;
        switch(position.charAt(0)) {
            case 'a' -> row = 1;
            case 'b' -> row = 2;
            case 'c' -> row = 3;
            case 'd' -> row = 4;
            case 'e' -> row = 5;
            case 'f' -> row = 6;
            case 'g' -> row = 7;
            case 'h' -> row = 8;
            default -> throw new ReplException("Invalid row position");
        }
        int column;
        try {
           column = Character.getNumericValue(position.charAt(1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ReplException("Invalid column position");
        }
        if (column < 1 || column > 8) {
            System.out.println(column);
            throw new ReplException("Invalid column position");
        }
        return new ChessPosition(column, row);

    }

    private static boolean invalidParameters(int minParameters, int numParameters) {
        if (numParameters < minParameters) {
            printError(String.format(
                    "ERROR: please input %s parameters. Type help for more information.", minParameters));
            return true;
        }
        return false;
    }

    private static void printError(String line){
        printLine("%s%s", EscapeSequences.SET_TEXT_COLOR_RED, line);
    }

    private static void printLine(String line, Object... args) {
        System.out.printf(line, args);
        resetTextColors();
        System.out.println();
    }

    private static void resetTextColors() {
        System.out.printf("%s%s", EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
}
