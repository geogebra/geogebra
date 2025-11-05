/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.input;

import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.editor.MathFieldInternal;

public class FunctionVariableAdapter
		implements org.geogebra.editor.share.input.adapter.KeyboardAdapter {
	private static final String VARIABLE_TAG = "#FunctionVariable#";

	@Override
	public void commit(MathFieldInternal mfi, String input) {
		String raw = input.replaceAll(VARIABLE_TAG, "");
		KeyboardInputAdapter.type(mfi, raw);
		if (raw.contains("_")) {
			CursorController.nextCharacter(mfi.getEditorState());
		}
	}

	@Override
	public boolean test(String input) {
		return input.length() > 2
				&& input.startsWith(VARIABLE_TAG)
				&& input.endsWith(VARIABLE_TAG);
	}

	/**
	 * To allow special handling when typed, we wrap variable names in tags
	 * that are removed when processing kyeboard input.
	 * @param varName variable name
	 * @return keyboard input string
	 */
	public static String wrap(String varName) {
		return VARIABLE_TAG + varName + VARIABLE_TAG;
	}
}
