package org.geogebra.web.android;

import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;

public class UpdateKeyboardListenerStub implements UpdateKeyBoardListener {

	public void doShowKeyBoard(boolean b, MathKeyboardListener textField) {
		// stub
	}

	public void keyBoardNeeded(boolean show, MathKeyboardListener textField) {
		// stub
	}

	public void showInputField() {
		// stub
	}

	public AppW getAppW() {
		return null;
	}
}
