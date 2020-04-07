package com.hse.cli;

import com.hse.cli.exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void echoWithoutQuotas() throws CliException, IOException {
        var result = launcher.launch("echo 123");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithSingleQuotas() throws CliException, IOException {
        var result = launcher.launch("echo '123'");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithDoubleQuotas() throws CliException, IOException {
        var result = launcher.launch("echo \"123\"");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void echoWithMultipleArgs() throws CliException, IOException {
        var result = launcher.launch("echo '123' 456 \"789\"");
        assertTrue(listEquals(result, List.of("123 456 789")));
    }

    @Test
    void wcOneLineFile() throws CliException, IOException {
        var result = launcher.launch("wc Test/1.txt");
        assertTrue(listEquals(result, List.of("1 2 11 Test/1.txt")));
    }

    @Test
    void wcMultiLineFile() throws CliException, IOException {
        var result = launcher.launch("wc Test/2.txt");
        assertTrue(listEquals(result, List.of("3 3 15 Test/2.txt")));
    }

    @Test
    void wcTwoFiles() throws CliException, IOException {
        var result = launcher.launch("wc Test/2.txt Test/1.txt");
        assertTrue(listEquals(result, List.of("3 3 15 Test/2.txt", "1 2 11 Test/1.txt", "4 5 26 total")));
    }

    @Test
    void pwdWorks() throws CliException, IOException {
        var result = launcher.launch("pwd");
    }

    @Test
    void catSingleLineFile() throws CliException, IOException {
        var result = launcher.launch("cat Test/1.txt");
        assertTrue(listEquals(result, List.of("simple line")));
    }

    @Test
    void catMultiLineFile() throws CliException, IOException {
        var result = launcher.launch("cat Test/2.txt");
        assertTrue(listEquals(result, List.of("multiline", "in", "file")));
    }

    @Test
    void catTwoFiles() throws CliException, IOException {
        var result = launcher.launch("cat Test/2.txt Test/1.txt");
        assertTrue(listEquals(result, List.of("multiline", "in", "file", "simple line")));
    }

    @Test
    void variablePrimitive() throws CliException, IOException {
        var result = launcher.launch("a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableFunction() throws CliException, IOException {
        var result = launcher.launch("a = echo \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableChange() throws CliException, IOException {
        var result = launcher.launch("a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("a = \"345\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$a");
        assertTrue(listEquals(result, List.of("345")));
    }

    @Test
    void variableFunctionAndPrimitive() throws CliException, IOException {
        var result = launcher.launch("a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("b = echo");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("$b $a");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void variableFunctionInQuotas() throws CliException, IOException {
        var result = launcher.launch("a = echo");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("echo \"$a\"");
        assertTrue(listEquals(result, List.of("echo")));
    }

    @Test
    void recursiveCat() throws CliException, IOException {
        var result = launcher.launch("cat \"$(cat Test/3.txt)\"");
        assertTrue(listEquals(result, List.of("simple line", "multiline", "in", "file")));
    }

    @Test
    void pipeCat() throws CliException, IOException {
        var result = launcher.launch("echo 123 | cat");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void pipeEcho() throws CliException, IOException {
        var result = launcher.launch("cat Test/1.txt | echo");
        assertTrue(listEquals(result, List.of("simple line")));
    }

    @Test
    void pipeWc() throws CliException, IOException {
        var result = launcher.launch("cat Test/1.txt | wc");
        assertTrue(listEquals(result, List.of("1 2 11")));
    }

    @Test
    void pipeVariable() throws CliException, IOException {
        var result = launcher.launch("a = \"123\"");
        assertTrue(listEquals(result, List.of()));

        result = launcher.launch("echo $a | echo");
        assertTrue(listEquals(result, List.of("123")));
    }

    @Test
    void twoPipes() throws CliException, IOException {
        var result = launcher.launch("cat Test/1.txt | wc | wc");
        assertTrue(listEquals(result, List.of("1 3 6")));
    }

    @Test
    void externalFunction() throws CliException, IOException {
        launcher.launch("find \"simple\" Test/1.txt");
    }

    @Test
    void externalFunctionWithPipe() throws CliException, IOException {
        launcher.launch("find \"simple\" Test/1.txt | wc");
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
    void failedBefore1() throws CliException, IOException {
        launcher.launch("x=1");
        var result = launcher.launch("echo \"12$x\"");
        assertTrue(listEquals(result, List.of("121")));
    }

    @Test
    void failedBefore2() throws CliException, IOException {
        launcher.launch("x=ex");
        launcher.launch("y=it");
        launcher.launch("$x$y");
    }

    @Test
    void failedBefore3() throws CliException, IOException {
        launcher.launch("x=echo");
        var result = launcher.launch("$x 1");
        assertTrue(listEquals(result, List.of("1")));

        result = launcher.launch("echo $x");
        assertTrue(listEquals(result, List.of("echo")));
    }

    @Test
    void cdToERelativeDirectory() throws CliException, IOException {
        launcher.launch("cd Test");
        launcher.launch("cd ..");
        launcher.launch("cd Test");
        var cdResult = launcher.launch("cd .");
        assertEquals(cdResult, Collections.emptyList());
        checkCdToTestDir();
    }

    @Test
    void cdToAbsoluteDirectory() throws CliException, IOException {
        var currentPath = System.getProperty("user.dir");
        var absolutePath = currentPath + "/Test";
        var cdResult = launcher.launch("cd " + absolutePath);
        checkCdToTestDir();
    }

    @Test
    void cdWithMultipleArguments() {
        assertThrows(IllegalArgumentsException.class, () -> launcher.launch("cd Test ThisDirDoesNotExist"));
    }

    @Test
    void cdToHome() throws CliException, IOException {
        var cdResult = launcher.launch("cd");
        assertEquals(cdResult, Collections.emptyList());
        var pwdResult = launcher.launch("pwd");
        assertEquals(System.getProperty("user.home"), pwdResult.get(0));
    }

    @Test
    void cdToNonexistingDirectory() {
        IOException e = assertThrows(IOException.class, () -> launcher.launch("cd ThisDirDoesNotExist"));
        assertNull(e.getCause());
        assertEquals("cannot cd to ThisDirDoesNotExist", e.getMessage());
    }

    @Test
    void cdChangesDirForSubprocess() throws CliException, IOException {
        launcher.launch("cd Test");
        var currentPath = System.getProperty("user.dir");
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            var result = launcher.launch("cmd.exe /c echo %cd%").get(0); // external command
            assertEquals(currentPath + "\\Test", result);
        } else {
            var result = launcher.launch("bash -c pwd").get(0); // external command
            assertEquals(currentPath + "/Test", result);
        }

    }

    private void checkCdToTestDir() throws CliException, IOException {
        var pwdResult = launcher.launch("pwd");
        assertTrue(pwdResult.get(0).endsWith("Test"));
        var catResult = launcher.launch("cat 1.txt");
        assertEquals(catResult, List.of("simple line"));
        var wcResult = launcher.launch("wc 1.txt");
        assertEquals(wcResult, List.of("1 2 11 1.txt"));
    }

    @Test
    void lsCurrentDirectory() throws CliException, IOException {
        launcher.launch("cd Test");
        var lsResult = launcher.launch("ls");
        assertEquals(List.of("1.txt", "2.txt", "3.txt"), lsResult);
    }

    @Test
    void lsRelativeDirectory() throws CliException, IOException {
        var lsResult = launcher.launch("ls Test");
        assertEquals(List.of("1.txt", "2.txt", "3.txt"), lsResult);
    }

    @Test
    void lsAbsoluteDirectory() throws CliException, IOException {
        var currentPath = System.getProperty("user.dir");
        var absolutePath = currentPath + "/Test";
        var lsResult = launcher.launch("ls " + absolutePath);
        assertEquals(List.of("1.txt", "2.txt", "3.txt"), lsResult);
    }

    @Test
    void lsFile() throws CliException, IOException {
        var lsResult = launcher.launch("ls Test/1.txt");
        assertEquals(List.of("1.txt"), lsResult);
    }

    @Test
    void lsNonexistingDirectory() {
        IOException e = assertThrows(IOException.class, () -> launcher.launch("ls ThisDirDoesNotExist"));
        assertNull(e.getCause());
        assertEquals("cannot access 'ThisDirDoesNotExist': no such file or directory", e.getMessage());
    }
}