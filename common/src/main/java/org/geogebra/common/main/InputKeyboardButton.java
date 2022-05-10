package org.geogebra.common.main;

import org.geogebra.common.gui.inputfield.AutoCompleteTextField;

public interface InputKeyboardButton {

	/**
	 * Show keyboard button
	 */
	void show();

	/**
	 * Hide keyboard button
	 */
	void hide();

	void attach(AutoCompleteTextField textField);
}
