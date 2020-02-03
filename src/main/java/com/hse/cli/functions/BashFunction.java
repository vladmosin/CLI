package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BashFunction {
    private List<BashFunction> parameters = new ArrayList<>();
    private BashFunction previous;
    private static final String PREVIOUS = "previous";

    public Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException {
        throw new IllegalStateException("Cannot apply abstract bash function");
    }

    public void addValue(BashFunction value) {
        parameters.add(value);
    }

    public List<Value> getValues() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException {
        var values = new ArrayList<Value>();
        for (var parameter : parameters) {
            Value value = parameter.apply();
            values.add(value);
        }

        return values;
    }

    protected Value getPreviousResult() throws IOException, VariableNotInScopeException, ExternalFunctionRuntimeException {
        if (previous == null) {
            throw new VariableNotInScopeException("No previous function", null);
        }
        return previous.apply();
    }

    protected boolean hasPreviousResult() {
        return previous != null;
    }

    public void addPrevious(@NotNull BashFunction value) {
        previous = value;
    }
}
