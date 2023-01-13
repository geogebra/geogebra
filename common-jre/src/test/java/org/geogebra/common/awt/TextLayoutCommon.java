package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.util.StringUtil;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class TextLayoutCommon implements GTextLayout {

	private final GFont font;
	private final String string;

	/**
	 * @param font font
	 * @param string string
	 */
	public TextLayoutCommon(GFont font, String string) {
		this.font = font;
		this.string = string;
	}

	@Override
	public double getAdvance() {
		return new StringUtil().estimateLength(string, font);
	}

	@Override
	public GRectangle2D getBounds() {
		return new Rectangle(0, 0, 1, 1);
	}

	@Override
	public double getAscent() {
		return 0.8 * font.getSize();
	}

	@Override
	public void draw(GGraphics2D g2, int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getDescent() {
		return 0.2 * font.getSize();
	}

}
