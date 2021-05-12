package com.himamis.retex.editor.share.input;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class FunctionVariableAdapter
		implements com.himamis.retex.editor.share.input.adapter.KeyboardAdapter {
	private static final String VARIABLE_TAG = "#FunctionVariable#";

	@Override
	public void commit(MathFieldInternal mfi, String input) {
		String raw = input.replaceAll(VARIABLE_TAG, "");
		KeyboardInputAdapter.type(mfi, raw);
		CursorController.nextCharacter(mfi.getEditorState());
	}

	@Override
	public boolean test(String input) {
		return input.length() > 2
				&& input.startsWith(VARIABLE_TAG)
				&& input.endsWith(VARIABLE_TAG);
	}

	public static String wrap(String varName) {
		return VARIABLE_TAG + varName + VARIABLE_TAG;
	}
}
