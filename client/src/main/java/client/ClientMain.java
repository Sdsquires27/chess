package client;

import chess.*;

import java.util.ArrayList;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        Repl repl;
        try {
            repl = new Repl(8080);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        repl.run();
    }
}
