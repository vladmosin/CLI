package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.ArrayList;

import static com.hse.cli.Utils.readFile;


/**
 * Holder for function which reads data from files, which names are given in arguments
 * */
public class CatFunction extends BashFunction {

    /**
     * If cat is not first function in pipe than it just return result of previous function,
     * otherwise, reads all files from given list of names
     */
    @Override
    public Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException, ParsingException {
        if (!hasPreviousResult()) {
            var lines = new ArrayList<String>();
            for (var paths : getValues()) {
                for (var path : paths.storedValue()) {
                    lines.addAll(readFile(path.trim()));
                }
            }

            return new StringValue(lines);

        } else {
            return getPreviousResult();
        }
    }
}
