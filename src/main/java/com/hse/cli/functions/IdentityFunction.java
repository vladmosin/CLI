package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * Stores value and returns it independently from its parameters values
 * */
public class IdentityFunction extends BashFunction {
    @NotNull private StringValue value;

    public IdentityFunction(@NotNull StringValue value) {
        this.value = value;
    }

    @Override
    public Value apply() throws VariableNotInScopeException, ExternalFunctionRuntimeException, IOException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        return value;
    }

}
