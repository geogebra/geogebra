package com.himamis.retex.editor.share.event;

/** TODO Try to document this collection of random-ish methods (what is their meaning?
 * when are they called?; what does the return value mean?)
 * Also,
 * - can we align the return types?
 * - try to find a better (more descriptive) name for onInsertString()
 */
public interface MathFieldListener {

	/**
	 * Called when Enter is pressed.
	 */
	void onEnter();

	/**
	 * Called when a key is typed
	 * @param key key name (may be null)
	 */
	void onKeyTyped(String key);

	/**
	 * Called when arrow key is pressed
	 * @param keyCode key code
	 * @return whether key was handled
	 */
	boolean onArrowKeyPressed(int keyCode);

	/**
	 * Celled when string inserted.
	 */
	default void onInsertString() {
		// rarely needed
	}

	boolean onEscape();

	/**
	 * Called when tab pressed
	 * @param shiftDown whether Shift key is pressed
	 * @return whether the key was handled
	 */
	boolean onTab(boolean shiftDown);
}
