package com.hse.cli.functions;

import com.hse.cli.Utils;
import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import picocli.CommandLine.Model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class GrepFunction extends BashFunction {
    private class GrepInfoHolder {
        private boolean caseInsensitive = false;
        private boolean matchFullWords = false;
        private int linesAfterMatched = 0;
        private String regex;
        private List<String> fileNames = null;
    }

    private static String CASE_INSENSITIVE = "-i";
    private static String FULL_WORDS = "-w";
    private static String LINES_AFTER_MATCH = "-A";

    @Override
    public Value apply() throws IOException,
            ExternalFunctionRuntimeException, VariableNotInScopeException, ParsingException {
        var info = parseArguments();
        var result = new ArrayList<String>();

        if (hasPreviousResult()) {
            var grepResult = grepContent(getPreviousResult().storedValue(), info);
            return new StringValue(grepResult);
        }

        for (var fileName : info.fileNames) {
            var fileContent = Utils.readFile(fileName);
            result.addAll(grepContent(fileContent, info));
        }

        return new StringValue(result);

    }

    private List<String> grepContent(@NotNull List<String> content, @NotNull GrepInfoHolder infoHolder) {
        var grepResult = new ArrayList<String>();
        for (int i = 0; i < content.size(); i++) {
            var line = content.get(i);
            var lineMatches = grepLine(line, infoHolder);
            if (lineMatches) {
                for (int j = 0; j <= infoHolder.linesAfterMatched; j++) {
                    grepResult.add(content.get(i + j));
                }
            }
        }

        return grepResult;
    }

    private boolean grepLine(@NotNull String line, @NotNull GrepInfoHolder infoHolder) {
        var regex = infoHolder.regex;

        if (regex == null) {
            return true;
        }
        Pattern pattern = infoHolder.caseInsensitive ?
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);

        if (infoHolder.matchFullWords) {
            for (var word : line.split(" ")) {
                if (pattern.matcher(word).matches()) {
                    return true;
                }
            }

            return false;
        } else {
            return pattern.matcher(line).find();
        }
    }

    private GrepInfoHolder parseArguments() throws VariableNotInScopeException,
            ExternalFunctionRuntimeException, IOException, ParsingException {
        CommandSpec spec = CommandSpec.create();
        spec.addOption(OptionSpec.builder(CASE_INSENSITIVE).build());
        spec.addOption(OptionSpec.builder(FULL_WORDS).build());
        spec.addOption(OptionSpec.builder(LINES_AFTER_MATCH).type(int.class).build());

        var infoHolder = new GrepInfoHolder();
        var args = prepareArgs(infoHolder);

        var commandLine = new CommandLine(spec);
        var parseResult = commandLine.parseArgs(args);

        setArgs(parseResult, infoHolder);

        return infoHolder;
    }

    private void setArgs(@NotNull CommandLine.ParseResult parseResult, @NotNull GrepInfoHolder infoHolder) {
        if (parseResult.hasMatchedOption(CASE_INSENSITIVE)) {
            infoHolder.caseInsensitive = true;
        }

        if (parseResult.hasMatchedOption(FULL_WORDS)) {
            infoHolder.matchFullWords = true;
        }

        infoHolder.linesAfterMatched = parseResult.matchedOptionValue(LINES_AFTER_MATCH, 0);
    }

    private String[] prepareArgs(@NotNull GrepInfoHolder infoHolder) throws IOException,
            ExternalFunctionRuntimeException, VariableNotInScopeException, ParsingException {

        var args = new ArrayList<String>();
        var result = getValues();

        int lastIndex = result.size() - 1;
        int hasPrevious = 1;
        if (hasPreviousResult()) {
            hasPrevious = 0;
        }

        if (result.size() < 2 - hasPrevious) {
            throw new ParsingException("not enough arguments", null);
        }

        if (hasPrevious == 1) {
            infoHolder.fileNames = result.get(lastIndex).storedValue();
        }

        var possibleRegex = result.get(lastIndex - hasPrevious).storedValue();
        if (possibleRegex.size() != 1) {
            throw new ParsingException("illegal regex", null);
        }

        infoHolder.regex = possibleRegex.get(0);

        for (int i = 0; i < lastIndex - hasPrevious; i++) {
            args.addAll(result.get(i).storedValue());
        }

        var argsArray = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            argsArray[i] = args.get(i);
        }

        return argsArray;
    }
}
