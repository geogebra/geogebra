package org.geogebra.keyboard.web;

public interface HasKeyboard {
	
	void updateKeyboardHeight();

	double getWidth();

	KeyboardLocale getLocalization();

	boolean needsSmallKeyboard();

}
