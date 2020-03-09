package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.InappropriateValueException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.List;

import static com.hse.cli.Constants.CURRENT_DIRECTORY;

/**
 * Prints current path to root
 * */
public class PwdFunction extends BashFunction {
    @Override
    public Value apply() throws VariableNotInScopeException, ExternalFunctionRuntimeException, IOException, ParsingException, InappropriateValueException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        return new StringValue(List.of(System.getProperty(CURRENT_DIRECTORY)));
    }
}
