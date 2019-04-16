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
