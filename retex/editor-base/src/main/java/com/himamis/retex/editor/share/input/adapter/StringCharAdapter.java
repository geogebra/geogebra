package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class StringCharAdapter extends StringInput {

    private final char input;

    public StringCharAdapter(char keyboard, char input) {
        this(keyboard + "", input);
    }

    public StringCharAdapter(String keyboard, char input) {
        super(keyboard);
        this.input = input;
    }

    @Override
    public void commit(MathFieldInternal mfi, String input) {
        typeCharacter(mfi, this.input);
    }
}
