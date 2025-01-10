package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;

public class PlainStringInput extends StringInput {

	private String input;

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
	public PlainStringInput(String keyboard, String input) {
		super(keyboard);
		this.input = input;
	}

	@Override
	public void commit(MathFieldInternal mfi, String unused) {
		boolean old = mfi.getInputController().getPlainTextMode();
		mfi.setPlainTextMode(true);
		KeyboardInputAdapter.type(mfi, this.input);
		mfi.setPlainTextMode(old);
	}
}
