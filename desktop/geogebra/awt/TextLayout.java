package geogebra.awt;

import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;

public class TextLayout implements geogebra.common.awt.font.TextLayout {
	private java.awt.font.TextLayout impl;
	public TextLayout(String string, Font fontLine,
			geogebra.common.awt.FontRenderContext frc) {
		impl = new java.awt.font.TextLayout(string,geogebra.awt.Font.getAwtFont(fontLine),
				geogebra.awt.FontRenderContext.getAwtFrc(frc));
	}

	public float getAdvance() {
		return impl.getAdvance();
	}

	public Rectangle2D getBounds() {
		return new geogebra.awt.GenericRectangle2D(impl.getBounds());
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
