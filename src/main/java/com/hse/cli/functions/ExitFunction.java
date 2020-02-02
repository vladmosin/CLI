package com.hse.cli.functions;

import com.hse.cli.interpretator.Value;

import static java.lang.System.exit;

public class ExitFunction extends BashFunction {
    @Override
    public Value apply() {
        exit(0);
        return null;
    }
}
