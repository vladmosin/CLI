package com.hse.cli.parser;

import com.hse.cli.VariableHolder;
import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.functions.*;
import com.hse.cli.interpretator.Environment;
import com.hse.cli.interpretator.StringValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of parser
 * */
public class Parser {

    /** Parse line and return result in form of bash function */
    public static BashFunction parse(@NotNull String line, @NotNull Environment environment)
            throws ParsingException, VariableNotInScopeException, IOException, ExternalFunctionRuntimeException {
        BashFunction previousFunction = null;
        for (String pipePart : splitIntoPipes(line, '|')) {
            var bashFunction = parseString(pipePart, environment);

            if (bashFunction == null) {
                bashFunction = parseBashFunction(pipePart.trim(), environment);
            }
            if (bashFunction == null) {
                return null;
            }

            if (previousFunction != null) {
                bashFunction.addPrevious(previousFunction);
            }

            previousFunction = bashFunction;
        }

        return previousFunction;
    }

    private static BashFunction parseBashFunction(@NotNull String line, @NotNull Environment environment)
            throws ParsingException, IOException, VariableNotInScopeException, ExternalFunctionRuntimeException {
        var tokens = splitIntoTokens(line, ' ');
        tokens = substituteVariables(tokens, environment);
        if (tokens.size() == 0) {
            return null;
        } else {
            var bashFunction = BashFunction.create(tokens.get(0));
            if (bashFunction == null) {
                return new ExternalFunction(tokens);
            } else {
                for (int i = 1; i < tokens.size(); i++) {
                    var parameter = parseStringByDefault(tokens.get(i).trim(), environment);
                    bashFunction.addValue(parameter);
                }

                return bashFunction;
            }
        }
    }

    private static BashFunction parseVariableFunction(@NotNull String line, @NotNull Environment environment)
            throws VariableNotInScopeException, ParsingException, ExternalFunctionRuntimeException, IOException {

        var parts = splitIntoPipes(line, ' ');
        if (parts.size() == 0 || parts.get(0).length() == 0 || parts.get(0).charAt(0) != '$') {
            return null;
        } else {
            var builder = new StringBuilder();
            builder.append(environment.getVariable(parts.get(0).substring(1).trim()));

            for (int i = 1; i < parts.size(); i++) {
                var part = parts.get(i);
                if (part.trim().length() > 0) {
                    builder.append(' ');
                    builder.append(part.trim());
                }
            }

            return parse(builder.toString(), environment);
        }
    }

    /**
     * Format: a = expression
     * Parses new variable declaration
     * */
    public static VariableHolder parseNewVariable(@NotNull String line) {
        line = line.trim();

        if (line.length() == 0) {
            return null;
        }

        int firstEqualitySymbol = firstEqualitySymbol(line);
        if (firstEqualitySymbol == -1) {
            return null;
        }

        var variableName = line.substring(0, firstEqualitySymbol).trim();

        var expression = line.substring(firstEqualitySymbol + 1).trim();
        return new VariableHolder(variableName, expression);
    }

    private static int firstEqualitySymbol(@NotNull String line) {
        boolean spacesStarted = false;

        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);

            if (symbol == '=') {
                return i;
            }

            if (spacesStarted && symbol != ' ') {
                return -1;
            }

            if (symbol == ' ') {
                spacesStarted = true;
            } else if (!Character.isDigit(symbol) && !Character.isLetter(symbol)) {
                return -1;
            }

        }

        return -1;
    }

    private static BashFunction parseStringByDefault(@NotNull String line, @NotNull Environment environment)
            throws IOException, VariableNotInScopeException, ParsingException, ExternalFunctionRuntimeException {
        if (Pattern.matches("^\"\\$\\([^']+\\)\"$", line)) {
            return parse(line.substring(3, line.length() - 2) , environment);
        } else {
            var stringFunction = parseString(line, environment);
            if (stringFunction == null) {
                stringFunction = new IdentityFunction(new StringValue(List.of(line)));
            }

            return stringFunction;
        }
    }

    private static List<String> substituteVariables(@NotNull List<String> parts, @NotNull Environment environment) throws VariableNotInScopeException, ParsingException {
        var result = new ArrayList<String>();
        for (var part : parts) {
            result.addAll(splitIntoTokens(substituteString(part, environment), ' '));
        }

        return result;
    }

    private static String substituteString(@NotNull String line, @NotNull Environment environment) throws VariableNotInScopeException {
        line = line.trim();
        if (line.charAt(0) == '\'' && line.charAt(line.length() - 1) == '\'') {
            return line;
        }
        var parts = line.split("\\$"); // name ... something else

        if (parts.length == 1) {
            return line;
        }

        var substitutionResult = new StringBuilder();

        substitutionResult.append(parts[0].trim());

        for (int i = 1; i < parts.length; i++) {
            var part = parts[i];
            int nameLength = getLastNameIndex(part);
            if (nameLength != 0) {
                var name = part.substring(0, nameLength);

                substitutionResult.append(environment.getVariable(name));

                if (name.length() < part.length()) {
                    substitutionResult.append(part.substring(nameLength));
                }
            } else {
                substitutionResult.append("$");
                substitutionResult.append(part);
            }
        }

        return substitutionResult.toString();
    }

    private static int getLastNameIndex(@NotNull String line) throws VariableNotInScopeException {
        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);
            if (!Character.isLetter(symbol) && !Character.isDigit(symbol)) {
                return i;
            }
        }

        return line.length();
    }

    private static List<String> splitIntoTokens(@NotNull String line, char defaultDelimiter) throws ParsingException {
        int start = 0;
        boolean fragmentStarted = false;
        char quota = defaultDelimiter;

        var tokens = new ArrayList<String>();

        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);
            if (fragmentStarted) {
                if (quota == '\'' && symbol == '\"') {
                    throw new ParsingException("Cannot parse: " + line, null);
                } else if (symbol == '\"') {
                    if (quota == '\"') {
                        tokens.add(line.substring(start, i + 1));
                        fragmentStarted = false;
                        quota = defaultDelimiter;
                    } else {
                        tokens.add(line.substring(start, i));
                        fragmentStarted = true;
                        start = i;
                        quota = '\"';
                    }
                } else if (symbol == '\'') {
                    if (quota == '\'') {
                        tokens.add(line.substring(start, i + 1));
                        fragmentStarted = false;
                        quota = defaultDelimiter;
                    } else {
                        tokens.add(line.substring(start, i));
                        fragmentStarted = true;
                        start = i;
                        quota = '\"';
                    }
                } else if (quota == defaultDelimiter && symbol == defaultDelimiter) {
                    tokens.add(line.substring(start, i));
                    fragmentStarted = false;
                    quota = defaultDelimiter;
                }
            } else {
                if (symbol != defaultDelimiter) {
                    fragmentStarted = true;
                    start = i;
                }

                if (symbol == '\'' || symbol == '\"') {
                    quota = symbol;
                }
            }
        }

        if (fragmentStarted) {
            if (quota != defaultDelimiter) {
                throw new ParsingException("Extra quota", null);
            } else {
                tokens.add(line.substring(start));
            }

        }

        return tokens;
    }

    private static List<String> splitIntoPipes(@NotNull String line, char delimiter) {
        char quota = 0;
        int start = 0;
        var pipes = new ArrayList<String>();

        for (int i = 0; i < line.length(); i++) {
            if (quota == 0) {
                if (line.charAt(i) == delimiter) {
                    pipes.add(line.substring(start, i));
                    start = i + 1;
                } else if (line.charAt(i) == '\'' || line.charAt(i) == '\"') {
                    quota = line.charAt(i);
                }
            } else if (line.charAt(i) == quota) {
                quota = 0;
            }
        }

        pipes.add(line.substring(start));

        return pipes;
    }

    private static BashFunction parseString(@NotNull String line, @NotNull Environment environment)
            throws VariableNotInScopeException, ParsingException {
        line = substituteString(line.trim(), environment);
        if (Pattern.matches("^\"[^\"]+\"$", line)) {
            return new IdentityFunction(new StringValue(
                    substituteVariables(List.of(line.substring(1, line.length() - 1)), environment)));
        } else if (Pattern.matches("^\'[^\"]+\'$", line)) {
            return new IdentityFunction(new StringValue(List.of(line.substring(1, line.length() - 1))));
        } else {
            return null;
        }
    }
}
