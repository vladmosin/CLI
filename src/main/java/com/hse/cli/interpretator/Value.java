package com.hse.cli.interpretator;

import java.util.List;

public interface Value {
    public List<String> storedValue();
    public void execute();
    public boolean isEmpty();
}
