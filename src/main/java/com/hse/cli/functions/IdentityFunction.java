package com.hse.cli.functions;

import com.hse.cli.exceptions.CliException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * Stores value and returns it independently from its parameters values
 * */
public class IdentityFunction extends BashFunction {
    @NotNull private StringValue value;

    public IdentityFunction(@NotNull StringValue value, Environment environment) {
        super(environment);
        this.value = value;
    }

    @Override
    public Value apply() throws IOException, CliException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        return value;
    }

}
