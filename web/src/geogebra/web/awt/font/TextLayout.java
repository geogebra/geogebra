package geogebra.web.awt.font;

import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.user.client.ui.HTML;

import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.main.AbstractApplication;
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
	    //AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return new geogebra.web.awt.Rectangle((int)getAdvance(),(int)getAscent());
    }

	public float getAscent() {
		HTML m_textmetrics = new HTML();
		m_textmetrics.setHTML("<div style='font-size: " + font.getSize() + "px;	font-family: " + font.getFontName() +";'>"+str+"</div>");
		m_textmetrics.setVisible(true);
		float h = m_textmetrics.getOffsetHeight();
		m_textmetrics.setVisible(false);
		return h;
    }

	public void draw(Graphics2D g2, int x, int y) {
		Font tempFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(str, x, y);
		g2.setFont(tempFont);
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

}
