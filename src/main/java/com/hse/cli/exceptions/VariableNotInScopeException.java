package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which is caused when variable do not exists in the environment
 * */
public class VariableNotInScopeException extends CliException {
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
