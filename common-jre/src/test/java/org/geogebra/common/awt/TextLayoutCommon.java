package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class TextLayoutCommon implements GTextLayout {

	@Override
	public double getAdvance() {
		return 0;
	}

	@Override
	public GRectangle2D getBounds() {
		return new Rectangle(0, 0, 1, 1);
	}

	@Override
	public double getAscent() {
		return 0;
	}

	@Override
	public void draw(GGraphics2D g2, int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getDescent() {
		return 0;
	}

}
