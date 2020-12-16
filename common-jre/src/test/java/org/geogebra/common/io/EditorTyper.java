package org.geogebra.common.io;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;

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
		KeyboardInputAdapter.emulateInput(mathField.getInternal(), input);
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
		mathField.getInternal().onKeyPressed(new KeyEvent(key, modifiers, '\0'));
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
			mathField.getInternal().onKeyPressed(new KeyEvent(key, modifiers, '\0'));
		}
	}
}
