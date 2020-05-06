package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.VariableNotInScopeException;
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

    public ExternalFunction(@NotNull List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Applies external function defined by name and parameters
     * */
    public Value apply() throws IOException, ExternalFunctionRuntimeException {
        String osName = System.getProperty("os.name").toLowerCase();
        String command = concatCommand();
        if (osName.contains("win")) {
            command = "cmd /c " + command;
        }

        var process = Runtime.getRuntime().exec(command);
        var result = new ArrayList<String>();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             var errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }

            while ((line = errorReader.readLine()) != null) {
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
