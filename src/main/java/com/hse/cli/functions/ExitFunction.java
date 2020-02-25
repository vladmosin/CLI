package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.Value;

import java.io.IOException;

/**
 * Holder for function exiting shell
 * */
public class ExitFunction extends BashFunction {
    public ExitFunction(Environment environment) {
        super(environment);
    }

    /**
     * Calculates effects of previous function and interupts processing
     * */
    @Override
    public Value apply() throws VariableNotInScopeException, ExternalFunctionRuntimeException, IOException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        return null;
    }
}
