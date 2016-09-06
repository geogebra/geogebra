package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class FunctionAdapter extends StringInput {

    private final String input;

    public FunctionAdapter(String input) {
        this(input, input);
    }

    public FunctionAdapter(String keyboard, String input) {
        super(keyboard);
        this.input = input;
    }

    @Override
    public void commit(MathFieldInternal mfi, String input) {
        commitFunction(mfi, this.input);
    }
}
