package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public interface KeyboardAdapter {

    void commit(MathFieldInternal mfi, String input);

    boolean test(String keyboard);
}
