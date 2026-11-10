package client;

import java.util.Scanner;

import chess.ChessGame;
import model.GameData;
import ui.EscapeSequences;

public class Repl {
    Scanner scanner = new Scanner(System.in);
    ClientController controller;

    Repl(ClientController controller) {
        this.controller = controller;
    }

    public void run() {
        resetTextColors();
        System.out.println("Welcome to Chess! Type help for a list of commands.");
        while(!controller.state.equals(State.QUIT)){
            String input = scanner.nextLine();
            if (controller.state.equals(State.SIGNED_OUT)){
                handlePreLoginInput(input);
            } else {
                handlePostLoginInput(input);
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
                if (!validateParameters(4, parameters.length)){
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
                if (!validateParameters(3, parameters.length)){
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
                if (!validateParameters(2, parameters.length)){
                    return;
                }
                try {
                    controller.create(parameters[1]);
                    printLine("Created game %s", parameters[1]);
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "list":
                try {
                    var games = controller.listGames();
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
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "join":
                ChessGame game = null;
                if (!validateParameters(3, parameters.length)){
                    return;
                }
                try {
                    game = controller.join(parameters[1], parameters[2]);
                    printLine("Joined game successfully!");
                } catch(ServerFacadeException | ClientControlException | BoardRendererException e) {
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                var renderer = new BoardRenderer(parameters[2]);
                printLine(renderer.displayBoard(game.getBoard()));
                return;
            case "observe":
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
            case "quit":
                controller.quit();
                return;
            default:
                printError("ERROR: command not recognized. Type help for a list of commands.");
        }
    }

    private static boolean validateParameters(int minParameters, int numParameters) {
        if (numParameters < minParameters) {
            printError(String.format(
                    "ERROR: please input %s parameters. Type help for more information.", minParameters));
            return false;
        }
        return true;
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
