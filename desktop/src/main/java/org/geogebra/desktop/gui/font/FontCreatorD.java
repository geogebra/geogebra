package org.geogebra.desktop.gui.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.font.FontCreator;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.desktop.main.FontManagerD;

/**
 * Creates fonts for desktop.
 */
public class FontCreatorD extends FontCreator {

	private FontManagerD fontManager;

	/**
	 * @param fontSettings font settings
	 * @param fontManager font manager
	 */
	public FontCreatorD(FontSettings fontSettings, FontManagerD fontManager) {
		super(fontSettings);
		this.fontManager = fontManager;
	}

	@Override
	public GFont newSerifFont(String testString, int fontStyle, int fontSize) {
		return fontManager.getFontCanDisplay(testString, true, fontStyle, fontSize);
	}

	@Override
	public GFont newSansSerifFont(String testString, int fontStyle, int fontSize) {
		return fontManager.getFontCanDisplay(testString, false, fontStyle, fontSize);
	}

	@Override
	public GFont newSansSerifFont() {
		return fontManager.getPlainFont();
	}
}
