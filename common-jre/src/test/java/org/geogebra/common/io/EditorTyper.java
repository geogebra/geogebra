package org.geogebra.common.io;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;

/**
 * Helps with typing input into an editor.
 */
public class EditorTyper {

    private MathFieldCommon mathField;

    /**
     * Create a new Editor Typer.
     *
     * @param mathField math filed to type into
     */
    public EditorTyper(MathFieldCommon mathField) {
        this.mathField = mathField;
    }

    /**
     * Type every character from the input string.
     *
     * @param input to type
     * @return this
     */
    public EditorTyper type(String input) {
        KeyboardInputAdapter.emulateInput(mathField.getInternal(), input);
        return this;
    }

    /**
     * Inserts string as is into editor.
     *
     * @param input to insert
     * @return this
     */
    public EditorTyper insert(String input) {
        mathField.insertString(input);
        return this;
    }

	/**
	 * Types a key.
	 *
	 * @param key keyCode to type
	 * @return this
	 */
	public EditorTyper typeKey(int key) {
		mathField.getInternal().onKeyPressed(new KeyEvent(key, 0, '\0'));
		return this;
	}

	/**
	 * Repeats a key.
	 *
	 * @param key keyCode to type
	 * @param count to repeat.
	 * @return this
	 */
	public EditorTyper repeatKey(int key, int count) {
		for (int i = 0; i < count; i++) {
			mathField.getInternal().onKeyPressed(new KeyEvent(key, 0, '\0'));
		}
		return this;
	}
}
