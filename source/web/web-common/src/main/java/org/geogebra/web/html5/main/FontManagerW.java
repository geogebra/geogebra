package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;
import org.geogebra.web.html5.awt.GFontW;

/**
 * This class takes care of storing and creating fonts.
 * 
 * @author Zbynek (based on Desktop FontManager)
 *
 */
public class FontManagerW extends FontManager {

	@Override
	public void setFontSize(int size) {
		// fontSize = size;
	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize1) {
		GFontW ret = new GFontW(
				serif ? GFontW.GEOGEBRA_FONT_SERIF
						: GFontW.GEOGEBRA_FONT_SANSERIF,
				fontStyle,
				fontSize1);
		return ret;
	}

}
