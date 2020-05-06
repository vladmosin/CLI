package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which is caused when variable do not exists in the environment
 * */
public class VariableNotInScopeException extends Exception {
    @NotNull
    private String message;
    private Exception suppressedException;

    /**
     * Creates exception
     * */
    public VariableNotInScopeException(@NotNull String message, Exception suppressedException) {
        this.message = message;
        this.suppressedException = suppressedException;
    }

    /**
     * Returns suppressed exception
     * */
    public Exception getSuppressedException() {
        return suppressedException;
    }

    /**
     * Returns message
     * */
    @NotNull
    public String getMessage() {
        return message;
    }
}
