package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;

public class VariableNotInScopeException extends Exception {
    @NotNull
    private String message;
    private Exception suppressedException;

    public VariableNotInScopeException(@NotNull String message, Exception suppressedException) {
        this.message = message;
        this.suppressedException = suppressedException;
    }

    public Exception getSuppressedException() {
        return suppressedException;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
}
