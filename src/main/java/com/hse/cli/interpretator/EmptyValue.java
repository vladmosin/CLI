package com.hse.cli.interpretator;

import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class EmptyValue implements Value {
    @Override
    public List<String> storedValue() {
        throw new IllegalStateException("cannot get value from EmptyValue");
    }

    @Override
    public void execute() {
        throw new IllegalStateException("Cannot execute empty object");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }


}
