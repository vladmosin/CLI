package com.hse.cli.functions;

import com.hse.cli.VariableHolder;
import com.hse.cli.exceptions.CliException;
import com.hse.cli.exceptions.IllegalArgumentsException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static com.hse.cli.Constants.CURRENT_DIRECTORY_ENV;
import static com.hse.cli.Constants.HOME_DIRECTORY_PROPERTY;

/**
 *  Changes the current working directory to the value of the first argument.
 *  If no arguments are specified then changes working directory to the users' HOME.
 */
public class CdFunction extends BashFunction {
    public CdFunction(Environment environment) {
        super(environment);
    }

    @Override
    public Value apply() throws IOException, CliException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        var arguments = getValues();
        String targetDir;
        if (arguments.size() > 1) {
            throw new IllegalArgumentsException("Cd function accepts 0 or 1 argument", null);
        } else if (arguments.size() == 1) {
            targetDir = String.join("\n", arguments.get(0).storedValue());
        } else {
            targetDir = System.getProperty(HOME_DIRECTORY_PROPERTY);
        }
        String currentDir = getEnvironment().getVariable(CURRENT_DIRECTORY_ENV);
        Path resolvedDir = Paths.get(currentDir).resolve(targetDir);
        if (!Files.isDirectory(resolvedDir)) {
            throw new IOException("cannot cd to " + targetDir);
        }
        getEnvironment().addVariable(new VariableHolder(CURRENT_DIRECTORY_ENV, resolvedDir.normalize().toString()));
        return new StringValue(Collections.emptyList());
    }
}
