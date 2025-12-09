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

import org.geogebra.common.awt.GFont;

/**
 * Handles different fonts used by application
 */
public abstract class FontManager {

	/**
	 * Change size of all fonts
	 * 
	 * @param guiFontSize
	 *            new font size
	 */
	public abstract void setFontSize(int guiFontSize);

	/**
	 * Get a font which can display given string
	 * 
	 * @param testString
	 *            test string
	 * @param serif
	 *            serif /sans serif flag
	 * @param fontStyle
	 *            style
	 * @param fontSize
	 *            size
	 * @return usable font
	 */
	public abstract GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize);

}
