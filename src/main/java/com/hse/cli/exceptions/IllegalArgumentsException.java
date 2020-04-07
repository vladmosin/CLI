package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;


/**
 * Thrown to indicate that a function has been passed illegal or inappropriate arguments.
 * */
public class IllegalArgumentsException extends CliException {
    @NotNull
    private String message;
    private Exception suppressedException;

    public IllegalArgumentsException(@NotNull String message, Exception suppressedException) {
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
