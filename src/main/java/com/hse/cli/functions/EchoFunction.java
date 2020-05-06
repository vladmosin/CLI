package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.InappropriateValueException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.List;

/**
 * Holder for function which just prints all its arguments
 * */
public class EchoFunction extends BashFunction {

    /**
     * Function concatenate result of previous function and arguments
     * */
    @Override
    public Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException, ParsingException, InappropriateValueException {
        var values = getValues();
        var printingString = new StringBuilder();

        if (hasPreviousResult()) {
            for (var line : getPreviousResult().storedValue()) {
                printingString.append(line);
                printingString.append(' ');
            }
        }

        for (var value : values) {
            for (var line : value.storedValue()) {
                printingString.append(line);
                printingString.append(' ');
            }
        }

        var resultString = printingString.toString();
        return new StringValue(List.of(resultString.substring(0, resultString.length() - 1)));
    }
}
