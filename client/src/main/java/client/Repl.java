package client;

import java.util.Scanner;
import ui.EscapeSequences;

public class Repl {
    State state = State.SIGNED_OUT;
    Scanner scanner = new Scanner(System.in);
    ServerFacade facade;

    Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        resetTextColors();
        System.out.println("Welcome to Chess! Type help for a list of commands.");
        while(!state.equals(State.QUIT)){
            String input = scanner.nextLine();
            if (state.equals(State.SIGNED_OUT)){
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
                printLine("help%s - list commands",
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
                return;
            case "register":
                if (parameters.length < 4) {
                    printError("ERROR: please input four parameters. Type help for more information.");
                    return;
                }
                var username = parameters[1];
                var password = parameters[2];
                var email = parameters[3];
                try {
                    facade.register(username, password, email);
                    printLine("Logged in as %s", username);
                    state = State.SIGNED_IN;
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "login":
                if (parameters.length < 3) {
                    printError("ERROR: please input three parameters. Type help for more information.");
                    return;
                }
                username = parameters[1];
                password = parameters[2];
                try {
                    facade.login(username, password);
                    printLine("Logged in as %s", username);
                    state = State.SIGNED_IN;
                } catch (ServerFacadeException e){
                    printError("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    printError("ERROR: we've encountered an error. Please try again later.");
                }
                return;
            case "quit":
                state = State.QUIT;
                System.out.println("Bye!");
                return;
            default:
                printError("ERROR: command not recognized. Type help for a list of commands.");
        }
    }

    private void handlePostLoginInput(String input) {

    }

    private void printError(String line){
        printLine("%s%s", EscapeSequences.SET_TEXT_COLOR_RED, line);
    }

    private void printLine(String line, Object... args) {
        System.out.printf(line, args);
        resetTextColors();
        System.out.println();
    }

    private void resetTextColors() {
        System.out.printf("%s%s", EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
}
