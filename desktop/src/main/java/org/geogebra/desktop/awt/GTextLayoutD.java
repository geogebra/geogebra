package org.geogebra.desktop.awt;

import java.awt.font.TextLayout;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;

public class GTextLayoutD implements org.geogebra.common.awt.font.GTextLayout {
	private java.awt.font.TextLayout impl;
	private String str;
	private GFont font;

	public GTextLayoutD(String string, GFont font,
			org.geogebra.common.awt.GFontRenderContext frc) {
		this.font = font;
		this.str = string;
		impl = new java.awt.font.TextLayout(string,
				org.geogebra.desktop.awt.GFontD.getAwtFont(font),
				org.geogebra.desktop.awt.GFontRenderContextD.getAwtFrc(frc));
	}

	public float getAdvance() {
		return impl.getAdvance();
	}

	public GRectangle2DD getBounds() {
		return new org.geogebra.desktop.awt.GGenericRectangle2DD(impl.getBounds());
	}

	public float getAscent() {
		return impl.getAscent();
	}

	public float getDescent() {
		return impl.getDescent();
	}

	public void draw(GGraphics2D g2, int x, int y) {
		if (g2 instanceof GGraphics2DD) {
			impl.draw(GGraphics2DD.getAwtGraphics(g2), x, y);
		} else {
			GFont tempFont = g2.getFont();
			g2.setFont(font);
			g2.drawString(str, x, y);
			g2.setFont(tempFont);
		}
	}

	public TextLayout getImpl() {
		return impl;
	}

}
