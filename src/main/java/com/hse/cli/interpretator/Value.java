package com.hse.cli.interpretator;

import java.util.List;

/**
 * Stored different objects, however, each of them should be able to transform into string
 * */
public interface Value {
    /**
     * Returns stored list of strings in Value
     * */
    public List<String> storedValue();
}
