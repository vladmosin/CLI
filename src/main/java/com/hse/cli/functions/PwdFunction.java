package com.hse.cli.functions;

import com.hse.cli.interpretator.StringValue;
import com.hse.cli.interpretator.Value;

import java.util.List;

import static com.hse.cli.Constants.CURRENT_DIRECTORY;

public class PwdFunction extends BashFunction {
    @Override
    public Value apply() {
        return new StringValue(List.of(System.getProperty(CURRENT_DIRECTORY)));
    }
}
