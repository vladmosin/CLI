package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception for mismatching expected and real types for objects implementing Value interface
 * */
public class InappropriateValueException extends Exception {
    @NotNull
    private String message;
    private Exception suppressedException;

    public InappropriateValueException(@NotNull String message, Exception suppressedException) {
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
