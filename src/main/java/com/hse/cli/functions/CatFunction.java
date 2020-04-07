package com.hse.cli.functions;

import com.hse.cli.exceptions.CliException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.ArrayList;

import static com.hse.cli.Constants.CURRENT_DIRECTORY_ENV;
import static com.hse.cli.Utils.readFile;


/**
 * Holder for function which reads data from files, which names are given in arguments
 * */
public class CatFunction extends BashFunction {
    public CatFunction(Environment environment) {
        super(environment);
    }

    /**
     * If cat is not first function in pipe than it just return result of previous function,
     * otherwise, reads all files from given list of names
     */
    @Override
    public Value apply() throws IOException, CliException {
        String currentDir = getEnvironment().getVariable(CURRENT_DIRECTORY_ENV);
        if (!hasPreviousResult()) {
            var lines = new ArrayList<String>();
            for (var paths : getValues()) {
                for (var path : paths.storedValue()) {
                    lines.addAll(readFile(currentDir, path.trim()));
                }
            }

            return new StringValue(lines);

        } else {
            return getPreviousResult();
        }
    }
}
