package org.geogebra.desktop.awt;

import java.awt.Color;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GColorN;
import org.geogebra.common.kernel.arithmetic.MyDouble;

public class GColorD extends GColorN {

	private Color adaptedColor = new Color(0, 0, 0);

	public GColorD(int r, int g, int b, int alpha) {

		adaptedColor = new Color(MyDouble.truncate0to255(r),
				MyDouble.truncate0to255(g), MyDouble.truncate0to255(b),
				MyDouble.truncate0to255(alpha));
	}

	@Override
	public int getBlue() {
		return adaptedColor.getBlue();
	}

	@Override
	public int getAlpha() {
		return adaptedColor.getAlpha();
	}

	@Override
	public int getGreen() {
		return adaptedColor.getGreen();
	}

	@Override
	public int getRed() {
		return adaptedColor.getRed();
	}

	/**
	 * @param color
	 * @return
	 */
	public static Color getAwtColor(GColor color) {
		if (color == null)
			return null;
		return getAwtColor((GColorD) color.getColor());
	}

	public static Color getAwtColor(GColorD color) {
		if (color == null)
			return null;
		return color.adaptedColor;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof GColorD)) {
			return false;
		}
		return adaptedColor.equals(((GColorD) o).adaptedColor);
	}

	@Override
	public int hashCode() {
		return adaptedColor.hashCode();
	}

	public static GColor newColor(Color c) {
		return GColor.newColor(c.getRed(), c.getGreen(), c.getBlue());
	}

}
