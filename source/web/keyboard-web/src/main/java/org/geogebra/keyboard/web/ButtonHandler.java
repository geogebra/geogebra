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

package org.geogebra.keyboard.web;

import org.geogebra.common.euclidian.event.PointerEventType;

/**
 * keyboard button handler interface
 *
 */
public interface ButtonHandler {
	/**
	 * processes the click on one of the keyboard buttons
	 * 
	 * @param btn
	 *            the button that was clicked
	 * @param type
	 *            the type of click (mouse vs. touch)
	 */
	void onClick(BaseKeyboardButton btn, PointerEventType type);

	/**
	 * Stop keyboard repeating command
	 */
	void buttonPressEnded();

}
