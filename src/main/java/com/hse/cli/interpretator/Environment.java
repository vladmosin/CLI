package com.hse.cli.interpretator;

import com.hse.cli.VariableHolder;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.functions.BashFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, String> variables = new HashMap<>();

    public void addVariable(@NotNull VariableHolder variableHolder) {
        String name = variableHolder.getName();
        var expression = variableHolder.getExpression();

        if (variables.containsKey(name)) {
            variables.replace(name, expression);
        } else {
            variables.put(name, expression);
        }
    }

    public String getVariable(@NotNull String name) throws VariableNotInScopeException {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else {
            throw new VariableNotInScopeException("Variable " + name + "not in scope", null);
        }
    }


}
