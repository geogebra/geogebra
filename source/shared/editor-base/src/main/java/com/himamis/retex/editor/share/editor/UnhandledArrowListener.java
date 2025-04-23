package com.himamis.retex.editor.share.editor;

/**
 * Listener for arrow keys that were not handled by editor.
 */
public interface UnhandledArrowListener {
	/**
	 * Runs when arrow key is pressed and not handled by the editor.
	 * @param keyCode key code from {@link com.himamis.retex.editor.share.util.JavaKeyCodes}
	 */
	void onArrow(int keyCode);
}
