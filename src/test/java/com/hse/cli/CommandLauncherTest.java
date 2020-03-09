package com.hse.cli;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.InappropriateValueException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Testing scenarios
* 1. All functions alone
* 2. Single and double quotas
* 3. Variables, both functions and strings
* 4. Variables with quotas
* 5. Pipes with all commands
* 6. Long pipes
* 7. Exceptions (negative testing)
* */

class CommandLauncherTest {
    private CommandLauncher launcher;

    private boolean listEquals(@NotNull List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            var elem1 = list1.get(i);
            var elem2 = list2.get(i);
            if (!elem1.equals(elem2)) {
                return false;
            }
        }

        return true;
    }

    @BeforeEach
    void setEnvironment() {
        launcher = new CommandLauncher();
    }

    @Test
    void echoWithoutQuotas() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("echo 123");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithSingleQuotas() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("echo '123'");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithDoubleQuotas() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("echo \"123\"");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithMultipleArgs() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("echo '123' 456 \"789\"");
        assertTrue(listEquals(result, List.of("123 456 789")));
    }

    @Test
    void wcOneLineFile() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("wc Test/1.txt");
        assertTrue(listEquals(result, List.of("1 2 11 Test/1.txt")));
    }

    @Test
    void wcMultiLineFile() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("wc Test/2.txt");
        assertTrue(listEquals(result, List.of("3 3 15 Test/2.txt")));
    }

    @Test
    void wcTwoFiles() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("wc Test/2.txt Test/1.txt");
        assertTrue(listEquals(result, List.of("3 3 15 Test/2.txt", "1 2 11 Test/1.txt", "4 5 26 total")));
    }

    @Test
    void pwdWorks() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("pwd");
    }

    @Test
    void catSingleLineFile() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/1.txt");
        assertTrue(listEquals(result, List.of("simple line")));
    }

    @Test
    void catMultiLineFile() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/2.txt");
        assertTrue(listEquals(result, List.of("multiline", "in", "file")));
    }

    @Test
    void catTwoFiles() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/2.txt Test/1.txt");
        assertTrue(listEquals(result, List.of("multiline", "in", "file", "simple line")));
    }

    @Test
    void variablePrimitive() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableFunction() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = echo \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableChange() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a = \"345\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("345")));
    }

    @Test
    void variableFunctionAndPrimitive() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$b = echo");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$b $a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableFunctionInQuotas() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = echo");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("echo '$a'");
        assertTrue(listEquals(result, List.of("echo")));
    }

    @Test
    void recursiveCat() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat '$(cat Test/3.txt)'");
        assertTrue(listEquals(result, List.of("simple line", "multiline", "in", "file")));
    }

    @Test
    void pipeCat() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("echo 123 | cat");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void pipeEcho() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/1.txt | echo");
        assertTrue(listEquals(result, List.of("simple line")));
    }

    @Test
    void pipeWc() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/1.txt | wc");
        assertTrue(listEquals(result, List.of("1 2 11")));
    }

    @Test
    void pipeVariable() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("$a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("echo $a | echo");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void twoPipes() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/1.txt | wc | wc");
        assertTrue(listEquals(result, List.of("1 3 6")));
    }

    @Test
    void externalFunction() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        launcher.launch("ls");
    }

    @Test
    void externalFunctionWithPipe() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        launcher.launch("ls | wc");
    }

    @Test
    void parsingException() {
        assertThrows(ParsingException.class, () -> launcher.launch("echo \' dfsd"));
    }

    @Test
    void scopeException() {
        assertThrows(VariableNotInScopeException.class, () -> launcher.launch("echo $a"));
    }

    @Test
    void grepSimpleFile() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep 'simple' Test/1.txt");
        assertTrue(listEquals(result, List.of("simple line")));
    }

    @Test
    void grepRegex() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep \"^[a-z]{4}$\" Test/2.txt");
        assertTrue(listEquals(result, List.of("file")));
    }

    @Test
    void grepCaseInsensitive() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep -i 'MuL[A-Z]+' Test/2.txt");
        assertTrue(listEquals(result, List.of("multiline")));
    }

    @Test
    void grepWords() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep -w 'Should' Test/4.txt");
        assertTrue(listEquals(result, List.of("Should be grepped")));
    }

    @Test
    void grepExtraLines() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep -A1 'multiline' Test/2.txt");
        assertTrue(listEquals(result, List.of("multiline", "in")));
    }

    @Test
    void grepAllFlags() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("grep -A1 -iw '[A-Z]{2}' Test/2.txt");
        assertTrue(listEquals(result, List.of("in", "file")));
    }

    @Test
    void grepInPipe() throws ExternalFunctionRuntimeException, ParsingException, VariableNotInScopeException, IOException, InappropriateValueException {
        var result = launcher.launch("cat Test/2.txt | grep -A1 -iw '[A-Z]{2}'");
        assertTrue(listEquals(result, List.of("in", "file")));
    }
}