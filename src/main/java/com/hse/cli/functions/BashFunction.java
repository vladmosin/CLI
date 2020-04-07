package com.hse.cli.functions;

import com.hse.cli.exceptions.*;
import com.hse.cli.interpretator.Environment;
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
     * Current mutable environment
     */
    private final Environment environment;

    protected BashFunction(Environment environment) {
        this.environment = environment;
    }

    /**
     * Runs function with given parameters
     * */
    public abstract Value apply() throws CliException, IOException;

    public void addValue(BashFunction value) {
        parameters.add(value);
    }

    public List<Value> getValues() throws CliException, IOException {
        var values = new ArrayList<Value>();
        for (var parameter : parameters) {
            Value value = parameter.apply();
            values.add(value);
        }

        return values;
    }

    protected Value getPreviousResult() throws CliException, IOException {
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

    public static BashFunction create(@NotNull String name, @NotNull Environment environment) throws ParsingException {
        switch (name) {
            case CAT:
                return new CatFunction(environment);
            case ECHO:
                return new EchoFunction(environment);
            case EXIT:
                return new ExitFunction(environment);
            case PWD:
                return new PwdFunction(environment);
            case WC:
                return new WcFunction(environment);
            case CD:
                return new CdFunction(environment);
            case LS:
                return new LsFunction(environment);
            default:
                return null;
        }
    }

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

    protected Environment getEnvironment() {
        return environment;
    }
}
