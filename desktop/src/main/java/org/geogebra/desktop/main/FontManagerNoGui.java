package org.geogebra.desktop.main;

import java.awt.Font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;
import org.geogebra.desktop.awt.GFontD;

public class FontManagerNoGui extends FontManager {

	@Override
	public void setFontSize(int guiFontSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		return new GFontD(new Font("sans", fontStyle, fontSize));
	}

}
