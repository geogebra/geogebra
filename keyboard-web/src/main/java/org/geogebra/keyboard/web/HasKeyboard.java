package org.geogebra.keyboard.web;

import org.geogebra.common.main.Localization;

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
	Localization getLocalization();

	/**
	 * @return true-if small keyboard needed
	 */
	boolean needsSmallKeyboard();

	/**
	 * update on keyboard close
	 */
	void updateViewSizes();
}
