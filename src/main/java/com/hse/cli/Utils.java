package com.hse.cli;

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
    public static List<String> readFile(@NotNull String currentDir, @NotNull String path) throws IOException {
        return Files.lines(Paths.get(currentDir).resolve(path)).collect(Collectors.toList());
    }
}
