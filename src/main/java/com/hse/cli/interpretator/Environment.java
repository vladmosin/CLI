package com.hse.cli.interpretator;

import com.hse.cli.VariableHolder;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.functions.BashFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hse.cli.Constants.CURRENT_DIRECTORY_ENV;
import static com.hse.cli.Constants.CURRENT_DIRECTORY_PROPERTY;

public class Environment {
    private Map<String, String> variables = new HashMap<>();

    public Environment() {
        var systemVariables = System.getenv();
        for (var variableName : systemVariables.keySet()) {
            variables.put(variableName, systemVariables.get(variableName));
        }
        variables.put(CURRENT_DIRECTORY_ENV, System.getProperty(CURRENT_DIRECTORY_PROPERTY));
    }

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
            throw new VariableNotInScopeException("Variable with name \"" + name + "\" is not in scope", null);
        }
    }

    /**
     * Returns unmodifiable view to the environment variables.
     */
    public Map<String, String> getEnvironmentMap() {
        return Collections.unmodifiableMap(variables);
    }
}
