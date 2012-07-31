package geogebra.web.main;


import geogebra.common.awt.GFont;
import geogebra.common.main.FontManager;
/**
 * This class takes care of storing and creating fonts.
 * @author Zbynek (based on Desktop FontManager)
 *
 */
public class FontManagerW extends FontManager{
	private int fontSize;
	
	@Override
	public void setFontSize(int size){
		fontSize = size;
	}

	@Override
    public GFont getFontCanDisplay(String testString, boolean serif,
            int fontStyle, int fontSize) {
	    geogebra.web.awt.GFontW ret = new geogebra.web.awt.GFontW("normal");
	    ret.setFontStyle(fontStyle);
	    ret.setFontSize(fontSize);
	    ret.setFontFamily(serif?"serif":"sans-serif");
	    return ret;
    }

}
