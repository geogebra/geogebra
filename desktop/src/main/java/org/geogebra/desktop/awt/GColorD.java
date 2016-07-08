package org.geogebra.desktop.awt;

import java.awt.Color;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.arithmetic.MyDouble;

public class GColorD extends GColor {

	private static final double FACTOR = 0.7;

	private Color adaptedColor = new Color(0, 0, 0);

	public GColorD(int r, int g, int b, int alpha) {

		adaptedColor = new Color(MyDouble.truncate0to255(r),
				MyDouble.truncate0to255(g), MyDouble.truncate0to255(b),
				MyDouble.truncate0to255(alpha));
	}

	public GColorD(float r, float g, float b, float alpha) {
		if (r > 1) {
			r = 1;
		} else if (r < 0) {
			r = 0;
		}

		if (g > 1) {
			g = 1;
		} else if (g < 0) {
			g = 0;
		}

		if (b > 1) {
			b = 1;
		} else if (b < 0) {
			b = 0;
		}

		if (alpha < 0 || alpha > 1) {
			alpha = 1;
		}

		adaptedColor = new Color(r, g, b, alpha);
	}

	public GColorD(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public GColorD(int rgb) {
		adaptedColor = new Color(rgb);
	}

	public GColorD(Color hsbColor) {
		adaptedColor = hsbColor;
	}

	public GColorD(float f, float g, float h) {
		this(f, g, h, 1);
	}

	@Override
	public void getRGBColorComponents(float[] rgb) {
		adaptedColor.getRGBColorComponents(rgb);
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

	public static int HSBtoRGB(float redD, float greenD, float blueD) {
		return Color.HSBtoRGB(redD, greenD, blueD);
	}

	/**
	 * @param color
	 * @return
	 */
	public static Color getAwtColor(GColor color) {
		if (color == null)
			return null;
		return ((GColorD) color).adaptedColor;
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsb) {
		return Color.RGBtoHSB(r, g, b, hsb);

	}

	public static GColorD getHSBColor(float h, float s, float b) {
		return new GColorD(Color.getHSBColor(h, s, b));
	}

	@Override
	public GColorD darker() {
		return new GColorD(Math.max((int) (getRed() * FACTOR), 0), Math.max(
				(int) (getGreen() * FACTOR), 0), Math.max(
				(int) (getBlue() * FACTOR), 0));
	}

	@Override
	public GColor brighter() {
		return new GColorD(Math.min((int) (getRed() / FACTOR), 255), Math.min(
				(int) (getGreen() / FACTOR), 255), Math.min(
				(int) (getBlue() / FACTOR), 255));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GColorD))
			return false;
		return adaptedColor.equals(((GColorD) o).adaptedColor);
	}

	@Override
	public int hashCode() {
		return adaptedColor.hashCode();
	}

}
