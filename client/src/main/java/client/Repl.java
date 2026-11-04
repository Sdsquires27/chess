package client;

import java.util.Scanner;

public class Repl {
    State state;
    Scanner scanner = new Scanner(System.in);
    ServerFacade facade;

    Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println("Welcome to Chess! Type help for a list of commands.");
        while(!state.equals(State.QUIT)){
            String input = scanner.nextLine();
            if (state.equals(State.SIGNED_OUT)){

            } else {

            }
        }
    }

    private void handlePreLoginInput(String input) {

    }

    private void handlePostLoginInput(String input) {

    }
}
