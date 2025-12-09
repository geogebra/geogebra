/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
