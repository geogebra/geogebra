package org.geogebra.desktop.awt;

import java.awt.Color;

import org.geogebra.common.awt.GColor;

public class GColorD extends GColor {

	private static final double FACTOR = 0.7;

	private Color adaptedColor = new Color(0, 0, 0);

	public GColorD(int r, int g, int b, int alpha) {
		adaptedColor = new Color(r, g, b, alpha);
	}

	public GColorD(float r, float g, float b, float alpha) {
		adaptedColor = new Color(r, g, b, alpha);
	}

	public GColorD(int r, int g, int b) {
		adaptedColor = new Color(r, g, b);
	}

	public GColorD(int r, float g, int b) {
		adaptedColor = new Color(r, g, b);
	}

	public GColorD(int rgb) {
		adaptedColor = new Color(rgb);
	}

	public GColorD(Color hsbColor) {
		adaptedColor = hsbColor;
		// TODO Auto-generated constructor stub
	}

	public GColorD(float f, float g, float h) {
		adaptedColor = new Color(f, g, h);
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
