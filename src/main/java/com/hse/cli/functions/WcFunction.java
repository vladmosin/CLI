package com.hse.cli.functions;

import com.hse.cli.exceptions.ExternalFunctionRuntimeException;
import com.hse.cli.exceptions.InappropriateValueException;
import com.hse.cli.exceptions.ParsingException;
import com.hse.cli.exceptions.VariableNotInScopeException;
import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hse.cli.Utils.readFile;

/**
 * Holder for function counting lines, words and bytes
 */
public class WcFunction extends BashFunction {

    /**
     * IF YES, than dataHolder should be output with file name, otherwise without
     * */
    private enum WITH_FILENAME {YES, NO}

    /**
     * Stores result of wc function running
     * */
    private class WcDataHolder {
        private int symbols;
        private int words;
        private int lines;
        @NotNull private String fileName;

        private WcDataHolder(int symbols, int words, int lines, @NotNull String fileName) {
            this.symbols = symbols;
            this.words = words;
            this.lines = lines;
            this.fileName = fileName;
        }

        private int getSymbols() {
            return symbols;
        }

        private int getWords() {
            return words;
        }

        private int getLines() {
            return lines;
        }

        private String toString(WITH_FILENAME withFilename) {
            String dataInString = lines + " " + words + " " + symbols;
            if (withFilename == WITH_FILENAME.YES) {
                return dataInString + " " + fileName;
            } else {
                return dataInString;
            }
        }

        @NotNull
        private String getFileName() {
            return fileName;
        }
    }

    /**
     * If there is no previous function, then read files, which names are given as parameters and counts lines,
     * words and symbols, otherwise, count lines, words and symbols in result of previous function
     * */
    @Override
    public Value apply() throws VariableNotInScopeException, IOException, ExternalFunctionRuntimeException, ParsingException, InappropriateValueException {
        if (!hasPreviousResult()) {
            var fileInfos = new ArrayList<WcDataHolder>();
            for (var paths : getValues()) {
                for (var path : paths.storedValue()) {
                    fileInfos.add(getFileInfo(path));
                }
            }
            return new StringValue(convertAnswerToString(fileInfos));

        } else {
            Value previousResult = getPreviousResult();

            var input = previousResult.storedValue();
            int lines = countLines(input);
            int words = countWords(input);
            int symbols = countSymbols(input);

            String result = (new WcDataHolder(symbols, words, lines, " ")).toString(WITH_FILENAME.NO);
            return new StringValue(List.of(result));
        }
    }

    private List<String> convertAnswerToString(@NotNull List<WcDataHolder> fileInfos) {
        if (fileInfos.size() == 0) {
            throw new IllegalArgumentException("No filenames were given");
        } else {
            var stringResult = new ArrayList<String>();
            for (var fileInfo : fileInfos) {
                stringResult.add(fileInfo.toString(WITH_FILENAME.YES));
            }

            if (fileInfos.size() != 1) {
                stringResult.add(getSummaryData(fileInfos));
            }

            return stringResult;
        }
    }

    private String getSummaryData(@NotNull List<WcDataHolder> fileInfos) {
        int totalLines = 0;
        int totalWords = 0;
        int totalSymbols = 0;

        for (var fileInfo : fileInfos) {
            totalLines += fileInfo.getLines();
            totalWords += fileInfo.getWords();
            totalSymbols += fileInfo.getSymbols();
        }

        return new WcDataHolder(totalSymbols, totalWords, totalLines, "total").toString(WITH_FILENAME.YES);
    }

    private WcDataHolder getFileInfo(@NotNull String path) throws IOException, InappropriateValueException {
        var input = readFile(path);
        int lines = countLines(input);
        int words = countWords(input);
        int symbols = countSymbols(input);

        return new WcDataHolder(symbols, words, lines, path);
    }

    private int countLines(@NotNull List<String> input) {
        return input.size();
    }

    private int countWords(@NotNull List<String> input) {
        int words = 0;
        for (String line : input) {
            for (String word : line.split(" ")) {
                if (word.length() > 0) {
                    words += 1;
                }
            }
        }

        return words;
    }

    private int countSymbols(@NotNull List<String> input) {
        int symbols = 0;
        for (String line : input) {
            symbols += line.length();
        }

        return symbols;
    }
}
