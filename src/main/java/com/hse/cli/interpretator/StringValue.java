package com.hse.cli.interpretator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringValue implements Value {
    private List<String> value;

    public StringValue(@NotNull List<String> value) {
        this.value = value;
    }

    @NotNull
    public List<String> storedValue() {
        return value;
    }

}
