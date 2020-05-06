package com.hse.cli.interpretator;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Stores list of string
 * Using for storing results of functions and arguments
 * */
public class StringValue implements Value {
    private List<String> value;

    public StringValue(@NotNull List<String> value) {
        this.value = value;
    }

    /**
     * Return storing value
     * */
    @NotNull
    public List<String> storedValue() {
        return value;
    }

}
