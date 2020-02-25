package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExternalFunction extends BashFunction {
    @NotNull private List<String> parameters;

    public ExternalFunction(@NotNull List<String> parameters, Environment environment) {
        super(environment);
        this.parameters = parameters;
    }

    public Value apply() throws IOException, ExternalFunctionRuntimeException {
        String osName = System.getProperty("os.name").toLowerCase();
        String command = concatCommand();
        if (osName.contains("win")) {
            command = "cmd /c " + command;
        }

        var process = Runtime.getRuntime().exec(command);
        var result = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (Exception e) {
            throw new ExternalFunctionRuntimeException("Cannot launch external function: " + parameters.get(0), null);
        }

        return new StringValue(result);
    }

    private String concatCommand() {
        var builder = new StringBuilder();
        for (var parameter : parameters) {
            builder.append(' ');
            builder.append(parameter);
        }

        return builder.toString().substring(1);
    }
}
