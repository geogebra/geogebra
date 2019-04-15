package org.geogebra.common.gui.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.settings.FontSettings;

/**
 * Creates fonts.
 */
public abstract class FontCreator {

	private FontSettings fontSettings;

	/**
	 * @param fontSettings font settings
	 */
	public FontCreator(FontSettings fontSettings) {
		this.fontSettings = fontSettings;
	}

	/**
	 * Creates a serif font.
	 * @param testString test string
	 * @param fontStyle font style
	 * @param fontSize font size
	 * @return A new serif font instance.
	 */
	public abstract GFont newSerifFont(String testString, int fontStyle, int fontSize);

	/**
	 * Creates a sans-serif font.
	 * @param testString test string
	 * @param fontStyle font style
	 * @param fontSize font size
	 * @return A new sans-serif font instance.
	 */
	public abstract GFont newSansSerifFont(String testString, int fontStyle, int fontSize);

	/**
	 * Creates a sans-serif font.
	 * @return A new sans-serif font instance.
	 */
	public abstract GFont newSansSerifFont();

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
