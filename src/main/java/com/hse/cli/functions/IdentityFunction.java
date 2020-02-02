package com.hse.cli.functions;

import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import static com.hse.cli.Constants.DEFAULT;

public class IdentityFunction extends BashFunction {
    @NotNull private StringValue value;

    public IdentityFunction(@NotNull StringValue value) {
        this.value = value;
    }

    @Override
    public Value apply() {
        return value;
    }

}
