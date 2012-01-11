package geogebra.web.awt.font;

import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.web.awt.FontRenderContext;

public class TextLayout implements geogebra.common.awt.font.TextLayout {
	
	Font font;
	String str;
	FontRenderContext frc;

	public TextLayout(String str, geogebra.common.awt.Font font, FontRenderContext frc) {
	   this.font = font;
	   this.str = str;
	   this.frc = frc;
    }

	public float getAdvance() {
		return frc.measureText(str, ((geogebra.web.awt.Font) font).getFullFontString());
	}

	public Rectangle2D getBounds() {
	    // TODO Auto-generated method stub
	    return new geogebra.web.awt.Rectangle((int)getAdvance(),(int)getAscent());
    }

	public float getAscent() {
	    // TODO Auto-generated method stub
	    return font.getSize()*2;
    }

	public void draw(Graphics2D g2, int i, int j) {
	    // TODO Auto-generated method stub
	    
    }

}
