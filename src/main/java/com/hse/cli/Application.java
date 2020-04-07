package com.hse.cli;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        CommandLauncher launcher = new CommandLauncher();

        while (true) {
            var command = in.nextLine();
            try {
                var results = launcher.launch(command);
                if (results == null) {
                    break;
                }
                for (var answer : results) {
                    System.err.println(answer);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
