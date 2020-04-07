package com.hse.cli.functions;

import com.hse.cli.exceptions.CliException;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hse.cli.Constants.CURRENT_DIRECTORY_ENV;

/**
 * Lists information about the specified directory/file (the current directory if no arguments are specified).
 */
public class LsFunction extends BashFunction {
    public LsFunction(Environment environment) {
        super(environment);
    }

    @Override
    public Value apply() throws IOException, CliException {
        if (hasPreviousResult()) {
            getPreviousResult();
        }

        var arguments = getValues();
        String targetPath = "";
        if (!arguments.isEmpty()) {
            targetPath = String.join("\n", arguments.get(0).storedValue());
        }
        String currentDir = getEnvironment().getVariable(CURRENT_DIRECTORY_ENV);
        Path resolvedPath = Paths.get(currentDir).resolve(targetPath);
        if (!Files.exists(resolvedPath)) {
            throw new IOException("cannot access '" + targetPath + "': no such file or directory");
        }
        if (!Files.isDirectory(resolvedPath)) {
            return new StringValue(List.of(resolvedPath.getFileName().toString()));
        }
        try (Stream<Path> files = Files.list(resolvedPath)) {
            var filenames = files
                    .filter(file -> {
                        try {
                            return !Files.isHidden(file);
                        } catch (IOException ignored) {
                            return false;
                        }
                    })
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            return new StringValue(filenames);
        }
    }
}
