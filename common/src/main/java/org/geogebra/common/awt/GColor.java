package org.geogebra.common.awt;

import org.geogebra.common.factories.AwtFactory;

public abstract class GColor implements GPaint {

	public static GColor WHITE;
	public static GColor BLACK;
	public static GColor RED;
	public static GColor ORANGE;
	public static GColor YELLOW;
	public static GColor GREEN;
	public static GColor BLUE;
	public static GColor CYAN;
	public static GColor DARK_CYAN;
	public static GColor DARK_GREEN;
	public static GColor MAGENTA;
	public static GColor LIGHT_GRAY;
	public static GColor GRAY;
	public static GColor DARK_GRAY;

	public static void initColors(AwtFactory f) {
		WHITE = f.newColor(255, 255, 255);
		BLACK = f.newColor(0, 0, 0);
		RED = f.newColor(255, 0, 0);
		ORANGE = f.newColor(255, 127, 0);
		YELLOW = f.newColor(255, 255, 0);
		GREEN = f.newColor(0, 255, 0);
		BLUE = f.newColor(0, 0, 255);
		CYAN = f.newColor(0, 255, 255);
		DARK_CYAN = f.newColor(99, 219, 219);
		DARK_GREEN = f.newColor(0, 127, 0);
		MAGENTA = f.newColor(255, 0, 255);
		LIGHT_GRAY = f.newColor(192, 192, 192);
		GRAY = f.newColor(128, 128, 128);
		DARK_GRAY = f.newColor(68, 68, 68);
	}

	public abstract int getRed();

	public abstract int getBlue();

	public abstract int getGreen();

	public abstract int getAlpha();

	/* float[] */
	public abstract void getRGBColorComponents(float[] rgb);

	// public Color(float r, float g, float b, float alpha);
	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}

	public abstract org.geogebra.common.awt.GColor darker();

	public abstract org.geogebra.common.awt.GColor brighter();

	public static String getColorString(GColor fillColor) {
		return "rgba(" + fillColor.getRed() + "," + fillColor.getGreen() + ","
				+ fillColor.getBlue() + "," + (fillColor.getAlpha() / 255d)
				+ ")";
	}

	/**
	 * This method could return Long, but it returns Integer for
	 * backwards-compatibility, even if it's negative
	 * 
	 * @return int
	 */
	public int getRGB() {
		// must use longs to avoid negative overflow
		int red = getRed();
		if (red > 255)
			red = 255;
		if (red < 0)
			red = 0;
		int green = getGreen();
		if (green > 255)
			green = 255;
		if (green < 0)
			green = 0;
		int blue = getBlue();
		if (blue > 255)
			blue = 255;
		if (blue < 0)
			blue = 0;
		int alpha = getAlpha();
		if (alpha > 255)
			alpha = 255;
		if (alpha < 0)
			alpha = 0;
		return ((alpha * 256 + red) * 256 + green) * 256 + blue;
	}

	/**
	 * 
	 * @return gray scale value corresponding to this color
	 */
	public double getGrayScale() {
		return 0.2989 * getRed() + 0.5870 * getGreen() + 0.1140 * getBlue();
	}
}
