package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.List;

public class EchoFunction extends BashFunction {
    @Override
    public Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        var values = getValues();
        var printingString = new StringBuilder();

        for (var value : values) {
            for (var line : value.storedValue()) {
                printingString.append(line);
                printingString.append(' ');
            }
        }

        return new StringValue(List.of(printingString.toString()));
    }
}
