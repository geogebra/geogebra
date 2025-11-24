package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;

public class FontManagerNoGui extends FontManager {

	@Override
	public void setFontSize(int guiFontSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		return AwtFactory.getPrototype().newFont("sans", GFont.PLAIN, 12);
	}

}
