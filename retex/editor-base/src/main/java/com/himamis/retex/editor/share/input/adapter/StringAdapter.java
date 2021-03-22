package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;

public class StringAdapter extends StringInput {

    private final String input;

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
    public StringAdapter(char keyboard, String input) {
        this(keyboard + "", input);
    }

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
    public StringAdapter(String keyboard, String input) {
        super(keyboard);
        this.input = input;
    }

    @Override
	public void commit(MathFieldInternal mfi, String unused) {
        KeyboardInputAdapter.type(mfi, this.input);
    }
}
