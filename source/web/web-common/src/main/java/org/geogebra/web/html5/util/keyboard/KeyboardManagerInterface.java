/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	 * @return keyboard height
	 */
	int estimateHiddenKeyboardHeight();

	/**
	 * Update keyboard layout
	 */
	void clearAndUpdateKeyboard();

	/**
	 * Remove the keyboard from DOM.
	 */
	void removeFromDom();
}
