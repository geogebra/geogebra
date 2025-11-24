package org.geogebra.desktop.awt;

import java.awt.font.TextLayout;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.font.GTextLayout;

public class GTextLayoutD implements GTextLayout {
	private final TextLayout impl;
	private final String str;
	private final GFont font;

	/**
	 * @param string measured text
	 * @param font font
	 * @param frc rendering context
	 */
	public GTextLayoutD(String string, GFont font, GFontRenderContext frc) {
		this.font = font;
		this.str = string;
		impl = new TextLayout(string, GFontD.getAwtFont(font),
				GFontRenderContextD.getAwtFrc(frc));
	}

	@Override
	public double getAdvance() {
		return impl.getAdvance();
	}

	@Override
	public GRectangle2DD getBounds() {
		return new GGenericRectangle2DD(impl.getBounds());
	}

	@Override
	public double getAscent() {
		return impl.getAscent();
	}

	@Override
	public double getDescent() {
		return impl.getDescent();
	}

	@Override
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
