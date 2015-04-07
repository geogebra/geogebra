package org.geogebra.desktop.awt;

public class GColorD extends org.geogebra.common.awt.GColor {

	private static final double FACTOR = 0.7;

	private java.awt.Color adaptedColor = new java.awt.Color(0, 0, 0);

	public GColorD(int r, int g, int b, int alpha) {
		adaptedColor = new java.awt.Color(r, g, b, alpha);
	}

	public GColorD(float r, float g, float b, float alpha) {
		adaptedColor = new java.awt.Color(r, g, b, alpha);
	}

	public GColorD(int r, int g, int b) {
		adaptedColor = new java.awt.Color(r, g, b);
	}

	public GColorD(int r, float g, int b) {
		adaptedColor = new java.awt.Color(r, g, b);
	}

	public GColorD(int rgb) {
		adaptedColor = new java.awt.Color(rgb);
	}

	public GColorD(java.awt.Color hsbColor) {
		adaptedColor = hsbColor;
		// TODO Auto-generated constructor stub
	}

	public GColorD(float f, float g, float h) {
		adaptedColor = new java.awt.Color(f, g, h);
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
		return java.awt.Color.HSBtoRGB(redD, greenD, blueD);
	}

	/**
	 * @param color
	 * @return
	 */
	public static java.awt.Color getAwtColor(org.geogebra.common.awt.GColor color) {
		if (color == null)
			return null;
		return ((org.geogebra.desktop.awt.GColorD) color).adaptedColor;
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsb) {
		return java.awt.Color.RGBtoHSB(r, g, b, hsb);

	}

	public static GColorD getHSBColor(float h, float s, float b) {
		// TODO Auto-generated method stub
		return new GColorD(java.awt.Color.getHSBColor(h, s, b));
	}

	@Override
	public GColorD darker() {
		return new GColorD(Math.max((int) (getRed() * FACTOR), 0), Math.max(
				(int) (getGreen() * FACTOR), 0), Math.max(
				(int) (getBlue() * FACTOR), 0));
	}

	@Override
	public org.geogebra.common.awt.GColor brighter() {
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
