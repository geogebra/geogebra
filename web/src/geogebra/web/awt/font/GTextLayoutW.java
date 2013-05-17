package geogebra.web.awt.font;

import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;
import geogebra.html5.awt.GFontRenderContextW;

public class GTextLayoutW implements geogebra.common.awt.font.GTextLayout {
	
	GFont font;
	String str;
	GFontRenderContextW frc;
	boolean containsLowerCase = false;
	int advance = -1;

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
		if(advance<0)
			advance = frc.measureText(str, ((geogebra.html5.awt.GFontW) font).getFullFontString());
		return advance;
	}

	public GRectangle2D getBounds() {
	    return new geogebra.html5.awt.GRectangleW((int)getAdvance(),(int)getAscent());
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

	public void draw(GGraphics2D g2, int x, int y) {
		GFont tempFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(str, x, y);
		g2.setFont(tempFont);
	    App.debug("implementation needed - just finishing"); // TODO
    }

}
