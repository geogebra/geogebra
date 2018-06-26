package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class FunctionAdapter extends StringInput {

    private final String input;

	/**
	 * @param input
	 *            to be added to editor
	 */
    public FunctionAdapter(String input) {
        this(input, input);
    }

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
    public FunctionAdapter(String keyboard, String input) {
        super(keyboard);
        this.input = input;
    }

    @Override
	public void commit(MathFieldInternal mfi, String unused) {
        commitFunction(mfi, this.input);
    }
}
