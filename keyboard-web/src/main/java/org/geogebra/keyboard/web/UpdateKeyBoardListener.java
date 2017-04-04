package org.geogebra.keyboard.web;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;

public interface UpdateKeyBoardListener {

	// public void showInputField();

	public void keyBoardNeeded(boolean show, MathKeyboardListener textField);

	public void doShowKeyBoard(boolean b, MathKeyboardListener textField);

}