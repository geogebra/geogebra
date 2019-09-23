package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class TextLayoutCommon implements GTextLayout {

	public double getAdvance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public GRectangle2D getBounds() {
		return new Rectangle(0, 0, 1, 1);
	}

	public double getAscent() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void draw(GGraphics2D g2, int x, int y) {
		// TODO Auto-generated method stub

	}

	public double getDescent() {
		// TODO Auto-generated method stub
		return 0;
	}

}
