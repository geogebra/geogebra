package org.geogebra.web.html5.gui.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.font.FontCreator;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.web.html5.awt.GFontW;

public class FontCreatorW extends FontCreator {

	public FontCreatorW(FontSettings fontSettings) {
		super(fontSettings);
	}

	@Override
	public GFont newSerifFont(String testString, int fontStyle, int fontSize) {
		return new GFontW(GFontW.GEOGEBRA_FONT_SERIF, fontStyle, fontSize);
	}

	@Override
	public GFont newSansSerifFont(String testString, int fontStyle, int fontSize) {
		return new GFontW(GFontW.GEOGEBRA_FONT_SANSERIF, fontStyle, fontSize);
	}

	@Override
	public GFont newSansSerifFont() {
		return new GFontW(GFontW.GEOGEBRA_FONT_SANSERIF, 0, getFontSettings().getAppFontSize());
	}
}
