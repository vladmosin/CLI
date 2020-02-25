package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.util.List;

import static com.hse.cli.Constants.CURRENT_DIRECTORY_ENV;

/**
 * Prints current path to root
 * */
public class PwdFunction extends BashFunction {
    public PwdFunction(Environment environment) {
        super(environment);
    }

    @Override
    public Value apply() throws VariableNotInScopeException, ExternalFunctionRuntimeException, IOException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        return new StringValue(List.of(getEnvironment().getVariable(CURRENT_DIRECTORY_ENV)));
    }
}
