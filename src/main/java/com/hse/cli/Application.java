package com.hse.cli;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.InappropriateValueException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;

import java.io.IOException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        CommandLauncher launcher = new CommandLauncher();

        while (true) {
            var command = in.nextLine();
            try {
                for (var answer : launcher.launch(command)) {
                    System.out.println(answer);
                }
            } catch (ParsingException | ExternalFunctionRuntimeException | VariableNotInScopeException | InappropriateValueException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
