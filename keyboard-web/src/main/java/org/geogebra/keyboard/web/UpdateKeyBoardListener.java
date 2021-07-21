package org.geogebra.keyboard.web;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;

/**
 * handle add keyboard btn and show keyboard interface
 */
public interface UpdateKeyBoardListener {
	// public void showInputField();
	/**
	 * @param show
	 *            true if show
	 * @param textField
	 *            {@link MathKeyboardListener}
	 * @return true animating in
	 */
	boolean keyBoardNeeded(boolean show, MathKeyboardListener textField);

	/**
	 * @param b
	 *            true if show
	 * @param textField
	 *            {@link MathKeyboardListener}
	 */
	void doShowKeyBoard(boolean b, MathKeyboardListener textField);
}