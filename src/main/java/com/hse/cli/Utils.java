package com.hse.cli;

import com.hse.cli.exceptions.InappropriateValueException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class Utils {

    /**
     * Read file into list of strings
     * */
    public static List<String> readFile(@NotNull String path) throws IOException, InappropriateValueException {
        try {
            return Files.lines(Paths.get(path)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new InappropriateValueException("cannot read file: \"" + path + "\"", null);
        }
    }
}
