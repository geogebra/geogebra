package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.font.FontCreator;
import org.geogebra.common.main.settings.FontSettings;

public class FontCreatorNoGui extends FontCreator {

	private final static int DEFAULT_FONT_SIZE = 12;

	public FontCreatorNoGui(FontSettings fontSettings) {
		super(fontSettings);
	}

	@Override
	public GFont newSerifFont(String testString, int fontStyle, int fontSize) {
		return newSansSerifFont();
	}

	@Override
	public GFont newSansSerifFont(String testString, int fontStyle, int fontSize) {
		return newSansSerifFont();
	}

	@Override
	public GFont newSansSerifFont() {
		return AwtFactory.getPrototype().newFont("sans", GFont.PLAIN, DEFAULT_FONT_SIZE);
	}
}
