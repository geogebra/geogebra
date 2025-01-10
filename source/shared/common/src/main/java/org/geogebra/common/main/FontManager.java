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
