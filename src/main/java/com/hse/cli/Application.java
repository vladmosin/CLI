package com.hse.cli;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;

import java.io.IOException;
import java.util.Scanner;


/**
 * Main class. Reads users input and launches commands
 * */
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
