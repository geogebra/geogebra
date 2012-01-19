package geogebra.web.main;


import geogebra.common.awt.Font;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractFontManager;
/**
 * This class takes care of storing and creating fonts.
 * @author Zbynek (based on Desktop FontManager)
 *
 */
public class FontManager extends AbstractFontManager{
	private int fontSize;
	
	@Override
	public void setFontSize(int size){
		fontSize = size;
	}

	@Override
    public Font getFontCanDisplay(String testString, boolean serif,
            int fontStyle, int fontSize) {
	    geogebra.web.awt.Font ret = new geogebra.web.awt.Font("normal");
	    ret.setFontStyle(fontStyle);
	    ret.setFontSize(fontSize);
	    AbstractApplication.debug(fontSize);
	    ret.setFontFamily(serif?"serif":"sans-serif");
	    return ret;
    }

}
