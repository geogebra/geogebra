package org.geogebra.web.html5.util.keyboard;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

/**
 * WebSimple-compliant interface for keyboard manager
 */
public interface KeyboardManagerInterface {

	/**
	 * @return whether keyboard was closed by clicking the X button
	 */
	boolean isKeyboardClosedByUser();

	/**
	 * @param tablePopup
	 *            popup that should *not* be closed by clicking keyboard buttons
	 */
	void addKeyboardAutoHidePartner(GPopupPanel tablePopup);

	/**
	 * @param textField
	 *            keyboard listener
	 */
	void setOnScreenKeyboardTextField(MathKeyboardListener textField);

	/**
	 * Update keyboard localization
	 */
	void updateKeyboardLanguage();

	/**
	 * Update keyboard layout
	 */
	void clearAndUpdateKeyboard();
}
