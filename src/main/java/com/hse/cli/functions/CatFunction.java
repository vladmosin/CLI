package com.hse.cli.functions;

import com.hse.cli.Utils;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.ArrayList;

import static com.hse.cli.Constants.FILENAME;
import static com.hse.cli.Utils.readFile;

public class CatFunction extends BashFunction {
    @Override
    public Value apply() throws VariableNotInScopeException, IOException {
        Value previousResult = getPreviousResult();

        if (previousResult.isEmpty()) {
            var lines = new ArrayList<String>();
            for (var paths : getValues()) {
                for (var path : paths.storedValue()) {
                    lines.addAll(readFile(path));
                }
            }

            return new StringValue(lines);

        } else {
            return previousResult;
        }
    }
}
