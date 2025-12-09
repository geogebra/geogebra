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

package org.geogebra.common.main;

import org.geogebra.common.gui.inputfield.AutoCompleteTextField;

/**
 * Input keyboard button.
 */
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

	/**
	 * Enable or disable.
	 * @param enabled whether to enable
	 */
	void setEnabled(boolean enabled);
}
