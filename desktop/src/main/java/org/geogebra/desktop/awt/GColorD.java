package org.geogebra.desktop.awt;

import java.awt.Color;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GColorN;

public class GColorD extends GColorN {

	private final Color adaptedColor;

	public GColorD(int r, int g, int b, int alpha) {

		adaptedColor = new Color(r & 0xff, g & 0xff, b & 0xff, alpha & 0xff);
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
		return c == null ? null : GColor.newColor(c.getRed(), c.getGreen(),
				c.getBlue());
	}

}
