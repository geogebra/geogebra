package geogebra.web.awt.font;

import geogebra.common.awt.GFont;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Unicode;
import geogebra.web.awt.GFontRenderContextW;

public class GTextLayoutW implements geogebra.common.awt.font.GTextLayout {
	
	GFont font;
	String str;
	GFontRenderContextW frc;
	boolean containsLowerCase = false;

	public GTextLayoutW(String str, geogebra.common.awt.GFont font, GFontRenderContextW frc) {
	   this.font = font;
	   this.str = str;
	   this.frc = frc;
	   
	   if (str.length() > 0) {
		   for (int i = 0 ; i < str.length() ; i++) {
			   if (Unicode.charactersWithDescenders.indexOf(str.charAt(i)) > -1) {
				   containsLowerCase = true;
				   break;
			   }
		   }	
	   }
	   //containsLowerCase = str.indexOf('g') > -1 || str.indexOf('y') > -1 || str.indexOf('j') > -1 || str.indexOf('f') > -1;
    }

	public float getAdvance() {
		return frc.measureText(str, ((geogebra.web.awt.GFontW) font).getFullFontString());
	}

	public GRectangle2D getBounds() {
	    return new geogebra.web.awt.Rectangle((int)getAdvance(),(int)getAscent());
    }

	public float getAscent() {
		if (containsLowerCase) {
			return font.getSize() * 0.75f;
		}
		return font.getSize() * 0.80f;
    }
	
	public float getDescent() {
		if (containsLowerCase) {
			return font.getSize() * 0.25f;
		}
		return font.getSize() * 0.20f;
	}

	public void draw(Graphics2D g2, int x, int y) {
		GFont tempFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(str, x, y);
		g2.setFont(tempFont);
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO
    }

}
