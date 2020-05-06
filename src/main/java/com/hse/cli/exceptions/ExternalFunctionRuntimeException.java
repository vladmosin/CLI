package com.hse.cli.exceptions;

import org.jetbrains.annotations.NotNull;


/**
 * Holder for exception caused by running external functions
 * */
public class ExternalFunctionRuntimeException extends Exception {
    @NotNull
    private String message;
    private Exception suppressedException;

    /**
     * Creates exception
     * */
    public ExternalFunctionRuntimeException(@NotNull String message, Exception suppressedException) {
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
