package geogebra.web.awt.font;

import geogebra.common.awt.Font;
import geogebra.web.awt.FontRenderContext;

public class TextLayout implements geogebra.common.awt.font.TextLayout {
	
	Font font;
	String str;
	FontRenderContext frc;

	public TextLayout(String tempStr, Font font, FontRenderContext frc) {
	   this.font = font;
	   this.str = str;
	   this.frc = frc;
    }

	public float getAdvance() {
		return frc.measureText(str, ((geogebra.web.awt.Font) font).getFullFontString());
	}

}
