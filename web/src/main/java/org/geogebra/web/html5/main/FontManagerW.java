package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;

/**
 * This class takes care of storing and creating fonts.
 * 
 * @author Zbynek (based on Desktop FontManager)
 *
 */
public class FontManagerW extends FontManager {
	private int fontSize;

	@Override
	public void setFontSize(int size) {
		fontSize = size;
	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif,
	        int fontStyle, int fontSize1) {
		org.geogebra.web.html5.awt.GFontW ret = new org.geogebra.web.html5.awt.GFontW("normal");
		ret.setFontStyle(fontStyle);
		ret.setFontSize(fontSize1);
		ret.setFontFamily(serif ? "geogebra-serif, serif"
		        : "geogebra-sans-serif, sans-serif");
		return ret;
	}

}
