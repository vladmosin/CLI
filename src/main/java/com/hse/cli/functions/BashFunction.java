package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hse.cli.Constants.*;

/**
 * Base class for all executable functions in shell
 * */
public abstract class BashFunction {
    /**
     * Parameters for bash function
     * */
    private List<BashFunction> parameters = new ArrayList<>();

    /**
     * Previous function in pipe
     * */
    private BashFunction previous;

    /**
     * Runs function with given parameters
     * */
    public abstract Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException;

    /**
     * Adds new parameter
     * */
    public void addValue(BashFunction value) {
        parameters.add(value);
    }

    /**
     * Computes values of all parameters and returns result
     * */
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

    /**
     * Adds function which is previous in pipe
     * */
    public void addPrevious(@NotNull BashFunction value) {
        previous = value;
    }

    /**
     * Creates one of descendant by given name
     * */
    public static BashFunction create(@NotNull String name) throws ParsingException {
        switch (name) {
            case CAT:
                return new CatFunction();
            case ECHO:
                return new EchoFunction();
            case EXIT:
                return new ExitFunction();
            case PWD:
                return new PwdFunction();
            case WC:
                return new WcFunction();
            default:
                return null;
        }
    }

    /**
     * Checks that this function is Exit function or one of previous in pipe
     * */
    public boolean containsExitFunction() {
        if (this instanceof ExitFunction) {
            return true;
        } else {
            if (previous == null) {
                return false;
            }
            return previous.containsExitFunction();
        }
    }
}
