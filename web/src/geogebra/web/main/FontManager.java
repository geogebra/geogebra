package geogebra.web.main;

import geogebra.common.awt.Font;
import geogebra.common.main.AbstractFontManager;

public class FontManager extends AbstractFontManager{
	private int fontSize;
	
	public void setFontSize(int size){
		fontSize = size;
	}

	@Override
    public Font getFontCanDisplay(String testString, boolean serif,
            int fontStyle, int fontSize) {
	    return new geogebra.web.awt.Font("normal");
    }

}
