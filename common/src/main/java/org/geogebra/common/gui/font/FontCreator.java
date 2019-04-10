package org.geogebra.common.gui.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.settings.FontSettings;

public abstract class FontCreator {

	private FontSettings fontSettings;

	public FontCreator(FontSettings fontSettings) {
		this.fontSettings = fontSettings;
	}

	public abstract GFont newSerifFont(String testString, int fontStyle, int fontSize);

	public abstract GFont newSansSerifFont(String testString, int fontStyle, int fontSize);

	public abstract GFont newSansSerifFont();

	public GFont newSansSerifFont(String testString) {
		return newSansSerifFont(testString, GFont.PLAIN, fontSettings.getAppFontSize());
	}

	protected FontSettings getFontSettings() {
		return fontSettings;
	}
}
