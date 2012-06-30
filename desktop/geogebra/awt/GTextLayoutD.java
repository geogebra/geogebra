package geogebra.awt;

import geogebra.common.awt.GFont;
import geogebra.common.awt.Graphics2D;

public class GTextLayoutD implements geogebra.common.awt.font.GTextLayout {
	private java.awt.font.TextLayout impl;
	public GTextLayoutD(String string, GFont fontLine,
			geogebra.common.awt.GFontRenderContext frc) {
		impl = new java.awt.font.TextLayout(string,geogebra.awt.GFontD.getAwtFont(fontLine),
				geogebra.awt.GFontRenderContextD.getAwtFrc(frc));
	}

	public float getAdvance() {
		return impl.getAdvance();
	}

	public GRectangle2DD getBounds() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds());
	}

	public float getAscent() {
		return impl.getAscent();
	}

	public float getDescent() {
		return impl.getDescent();
	}

	public void draw(Graphics2D g2, int x, int y) {
		impl.draw(geogebra.awt.Graphics2D.getAwtGraphics(g2), x, y);

	}

}
