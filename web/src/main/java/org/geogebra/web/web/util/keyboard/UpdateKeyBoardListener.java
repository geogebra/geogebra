package org.geogebra.web.web.util.keyboard;

import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;

public interface UpdateKeyBoardListener {

	public abstract void showInputField();

	public abstract void keyBoardNeeded(boolean show,
	        MathKeyboardListener textField);

	public abstract void doShowKeyBoard(boolean b,
	        MathKeyboardListener textField);

	public abstract void closePopups();
}
