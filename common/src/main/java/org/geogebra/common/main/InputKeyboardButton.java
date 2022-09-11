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

	/**
	 * Specify the text field that keyboard button should appear in.
	 *
	 * @param textField which is about to use the keyboard button.
	 */
	void setTextField(AutoCompleteTextField textField);

	/**
	 * Removes keyboard button from the current input field.
	 */
	void detach();

	void setEnabled(boolean enabled);
}
