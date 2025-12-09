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

package org.geogebra.common.gui.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.settings.FontSettings;

/**
 * Creates fonts.
 */
public class FontCreator {

	private FontManager fontManager;
	private FontSettings fontSettings;

	/**
	 * @param fontManager font manager
	 * @param fontSettings font settings
	 */
	public FontCreator(FontManager fontManager, FontSettings fontSettings) {
		this.fontManager = fontManager;
		this.fontSettings = fontSettings;
	}

	/**
	 * Creates a serif font.
	 * @param testString test string
	 * @param fontStyle font style
	 * @param fontSize font size
	 * @return A new serif font instance.
	 */
	public GFont newSerifFont(String testString, int fontStyle, int fontSize) {
		return fontManager.getFontCanDisplay(testString, true, fontStyle, fontSize);
	}

	/**
	 * Creates a sans-serif font.
	 * @param testString test string
	 * @param fontStyle font style
	 * @param fontSize font size
	 * @return A new sans-serif font instance.
	 */
	public GFont newSansSerifFont(String testString, int fontStyle, int fontSize) {
		return fontManager.getFontCanDisplay(testString, false, fontStyle, fontSize);
	}

	/**
	 * Creates a sans-serif font.
	 * @return A new sans-serif font instance.
	 */
	public GFont newSansSerifFont() {
		return newSansSerifFont("");
	}

	/**
	 * Creates a sans-serif font.
	 * @param testString test string
	 * @return A new sans-serif font instance.
	 */
	public GFont newSansSerifFont(String testString) {
		return newSansSerifFont(testString, GFont.PLAIN, fontSettings.getAppFontSize());
	}

	/**
	 * @return font settings
	 */
	protected FontSettings getFontSettings() {
		return fontSettings;
	}
}
