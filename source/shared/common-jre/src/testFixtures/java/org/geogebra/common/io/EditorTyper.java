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
 
package org.geogebra.common.io;

import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.input.KeyboardInputAdapter;

/**
 * Helps with typing input into an editor.
 */
public class EditorTyper {

	private MathFieldCommon mathField;
	private int modifiers = 0;

	/**
	 * Create a new Editor Typer.
	 * @param mathField math filed to type into
	 */
	public EditorTyper(MathFieldCommon mathField) {
		this.mathField = mathField;
	}

	/**
	 * Type every character from the input string.
	 *
	 * @param input to type
	 */
	public void type(String input) {
		KeyboardInputAdapter.type(mathField.getInternal(), input);
	}

	/**
	 * Inserts string as is into editor.
	 *
	 * @param input to insert
	 */
	public void insert(String input) {
		mathField.insertString(input);
	}

	/**
	 * Types a key.
	 *
	 * @param key keyCode to type
	 */
	public void typeKey(int key) {
		mathField.getInternal().onKeyPressed(new KeyEvent(key, modifiers, '\0',
				KeyEvent.KeyboardType.EXTERNAL));
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Repeats a key.
	 *
	 * @param key keyCode to type
	 * @param count to repeat.
	 */
	public void repeatKey(int key, int count) {
		for (int i = 0; i < count; i++) {
			mathField.getInternal().onKeyPressed(new KeyEvent(key, modifiers, '\0',
					KeyEvent.KeyboardType.EXTERNAL));
		}
	}
}
