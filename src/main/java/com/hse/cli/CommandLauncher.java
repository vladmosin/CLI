package com.hse.cli;

import com.hse.cli.exceptions.CliException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Shell in this CLI
 * Gets lines and returns result of execution
 * */
public class CommandLauncher {
    @NotNull private Environment environment = new Environment();

    /**
     * Launches command and returns result
     * */
    public List<String> launch(@NotNull String line) throws IOException, CliException {
        var newVariable = Parser.parseNewVariable(line);
        if (newVariable != null) {
            environment.addVariable(newVariable);
            return new ArrayList<>();
        } else {
            var bashFunction = Parser.parse(line, environment);
            if (bashFunction != null) {
                if (bashFunction.containsExitFunction()) {
                    return null;
                }
                return bashFunction.apply().storedValue();
            } else {
                throw new ParsingException("Cannot parse command: " + line, null);
            }
        }
    }
}
