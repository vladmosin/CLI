package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception for storing errors while parsing
 * */
public class ParsingException extends Exception {
    @NotNull
    private String message;
    private Exception suppressedException;

    /**
     * Creates exception
     * */
    public ParsingException(@NotNull String message, Exception suppressedException) {
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
