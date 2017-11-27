package org.geogebra.keyboard.web;

import org.geogebra.web.html5.gui.util.KeyboardLocale;

/**
 *
 */
public interface HasKeyboard {
	/**
	 * update keyboard height
	 */
	void updateKeyboardHeight();

	/**
	 * @return inner width
	 */
	double getInnerWidth();

	/**
	 * @return localization
	 */
	KeyboardLocale getLocalization();

	/**
	 * @return true-if small keyboard needed
	 */
	boolean needsSmallKeyboard();

	/**
	 * update on keyboard close
	 */
	void updateCenterPanelAndViews();
}
