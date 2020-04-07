package com.hse.cli.functions;

import com.hse.cli.exceptions.CliException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.List;

/**
 * Holder for function which just prints all its arguments
 * */
public class EchoFunction extends BashFunction {
    public EchoFunction(Environment environment) {
        super(environment);
    }

    /**
     * Function concatenate result of previous function and arguments
     * */
    @Override
    public Value apply() throws IOException, CliException {
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
