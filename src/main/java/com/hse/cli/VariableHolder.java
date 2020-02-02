package com.hse.cli;

import com.hse.cli.functions.BashFunction;
import org.jetbrains.annotations.NotNull;

public class VariableHolder {
    @NotNull private String name;
    @NotNull private BashFunction expression;

    public VariableHolder(@NotNull String name, @NotNull BashFunction expression) {
        this.name = name;
        this.expression = expression;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public BashFunction getExpression() {
        return expression;
    }
}
