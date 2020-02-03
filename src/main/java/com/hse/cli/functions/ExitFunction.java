package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Value;

import java.io.IOException;

import static java.lang.System.exit;

public class ExitFunction extends BashFunction {
    @Override
    public Value apply() throws VariableNotInScopeException, ExternalFunctionRuntimeException, IOException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        exit(0);
        return null;
    }
}
