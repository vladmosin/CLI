package com.hse.cli;

import org.jetbrains.annotations.NotNull;


/**
 * Stores variables defined in environment
 * */
public class VariableHolder {
    @NotNull private String name;
    @NotNull private String expression;

    public VariableHolder(@NotNull String name, @NotNull String expression) {
        this.name = name;
        this.expression = expression;
    }

    /**
     * Returns variable name
     * */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns expression storing in variable
     * */
    @NotNull
    public String getExpression() {
        return expression;
    }
}
