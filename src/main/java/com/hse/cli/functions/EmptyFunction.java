package com.hse.cli.functions;

import com.hse.cli.interpretator.Value;

public class EmptyFunction extends BashFunction {
    @Override
    public Value apply() {
        return null;
    }
}
