package org.geogebra.common.awt;

import java.util.HashMap;
import java.util.Iterator;

import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.util.debug.Log;

public class GColor implements GPaint {

	public static final GColor WHITE = new GColor(255, 255, 255);
	public static final GColor BLACK = new GColor(0, 0, 0);
	public static final GColor RED = new GColor(255, 0, 0);
	public static final GColor ORANGE = new GColor(255, 127, 0);
	public static final GColor YELLOW = new GColor(255, 255, 0);
	public static final GColor GREEN = new GColor(0, 255, 0);
	public static final GColor BLUE = new GColor(0, 0, 255);
	public static final GColor CYAN = new GColor(0, 255, 255);
	public static final GColor DARK_CYAN = new GColor(99, 219, 219);
	public static final GColor DARK_GREEN = new GColor(0, 127, 0);
	public static final GColor MAGENTA = new GColor(255, 0, 255);
	public static final GColor LIGHTEST_GRAY = new GColor(230, 230, 230);
	public static final GColor LIGHT_GRAY = new GColor(192, 192, 192);
	public static final GColor GRAY = new GColor(128, 128, 128);
	public static final GColor DARK_GRAY = new GColor(68, 68, 68);
	public static final GColor PURPLE = new GColor(102, 102, 255);

	// 0 - 255
	private final int red;
	private final int green;
	private final int blue;
	private final int alpha;

	private static HashMap<GColor, GColorN> map = new HashMap<GColor, GColorN>();

	public GColor(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = 255;
	}

	public GColor(int r, int g, int b, int a) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	/*
	 * Creates an opaque sRGB color with the specified combined RGB value
	 * consisting of the red component in bits 16-23, the green component in
	 * bits 8-15, and the blue component in bits 0-7. Alpha is defaulted to 255.
	 */
	public static GColor newColor(int rgb) {
		int red = rgb >> 16;
		int green = (rgb >> 8) & 255;
		int blue = rgb & 255;

		return newColor(red, green, blue);
	}

	// public GColorOld getColor(GColor col) {
	//
	//
	// GColorOld ret = map.get(col);
	//
	// if (ret != null) {
	// return ret;
	// }
	//
	// ret = AwtFactory.getPrototype().newColor(col.red, col.green, col.blue,
	// col.alpha);
	//
	// map.put(col, ret);
	// log(col);
	//
	// return ret;
	//
	// }

	private void log() {
		Log.debug("storing " + getColorString(this));
		Log.error("map length = " + map.size());

		// Log.printStacktrace("");
	}

	private static GColor createColor(GColor col) {
		return new GColor(col.red, col.green, col.blue);
	}

	private static GColor createColor(int r, int g, int b, int a) {
		return new GColor(r, g, b, a);
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public GColorN getColor() {

		GColorN ret = map.get(this);

		if (ret == null) {
			// color hasn't been used yet, need to create it
			ret = AwtFactory.getPrototype().newColor(red, green, blue, alpha);
			map.put(this, ret);
			log();

		}

		return ret;

	}

	public static GColor newColor(int r, int g, int b) {
		return newColor(r, g, b, 255);
	}

	public static GColor newColor(int r, int g, int b, int a) {
		Iterator<GColor> it = map.keySet().iterator();

		while (it.hasNext()) {
			GColor col = it.next();

			if (col.red == r && col.green == g && col.blue == b
					&& col.alpha == a) {
				return col;
			}
		}

		// don't add this to the map
		// only create the native color object when necessary

		return new GColor(r, g, b, a);

	}

	/**
	 * Create a more readable (=darker) version of a color, to make it readable
	 * on white background. Does not change the color, if it already fulfills
	 * the requirements.
	 *
	 * Uses the W3C standard for contrast and brightness.
	 *
	 * @param color
	 *            the base color
	 * @param factory
	 *            used to create the new color
	 * @return a darker version of the input color that can be read on white
	 *         background
	 */
	public static GColor updateForWhiteBackground(GColor color) {

		int fgRed = color.getRed();
		int fgGreen = color.getGreen();
		int fgBlue = color.getBlue();
		// prevent endless loop
		int loopCounter = 0;
		int difference = 5;
		while (!checkColorRatioWhite(fgRed, fgGreen, fgBlue, GColor.WHITE)
				&& loopCounter < 50) {
			// create a slightly darker version of the color
			fgRed = Math.max(fgRed - difference, 0);
			fgGreen = Math.max(fgGreen - difference, 0);
			fgBlue = Math.max(fgBlue - difference, 0);
			loopCounter++;
		}

		if (!checkColorRatioWhite(fgRed, fgGreen, fgBlue, GColor.WHITE)) {
			// If the color could not be set correctly, the font color is set to
			// black.
			return GColor.BLACK;
		}

		return GColor.newColor(fgRed, fgGreen, fgBlue);
	}

	/**
	 * uses the color contrast ratio of the W3C, which can be found at:
	 * http://www.w3.org/TR/WCAG20-TECHS/G18.html
	 * http://web.mst.edu/~rhall/web_design/color_readability.html
	 *
	 * @param foreground
	 *            the text color
	 * @param background
	 *            the background color
	 * @return if the contrast ration sufficient (true) or not (false)
	 */
	private static boolean checkColorRatioWhite(int fgRed, int fgGreen,
			int fgBlue, GColor background) {
		int diff_hue = 3 * 255 - fgRed - fgBlue - fgGreen;

		double diff_brightness = 255
				- GColor.getGrayScale(fgGreen, fgRed, fgBlue);

		return diff_brightness > 125 && diff_hue > 500;
	}

	public int compareTo(GColor c) {
		if (getRed() < c.getRed()) {
			return -1;
		}
		if (getRed() > c.getRed()) {
			return 1;
		}
		if (getGreen() < c.getGreen()) {
			return -1;
		}
		if (getGreen() > c.getGreen()) {
			return 1;
		}
		if (getBlue() < c.getBlue()) {
			return -1;
		}
		if (getBlue() > c.getBlue()) {
			return 1;
		}
		if (getAlpha() < c.getAlpha()) {
			return -1;
		}
		if (getAlpha() > c.getAlpha()) {
			return 1;
		}

		return 0;
	}

	/**
	 * 
	 * @return gray scale value corresponding to this color (0 to 255)
	 */
	public double getGrayScale() {
		return getGrayScale(getRed(), getGreen(), getBlue());
	}

	private static double getGrayScale(int red2, int green2, int blue2) {
		return 0.2989 * red2 + 0.5870 * green2 + 0.1140 * blue2;
	}

	/**
	 * This method could return Long, but it returns Integer for
	 * backwards-compatibility, even if it's negative
	 * 
	 * @return int
	 */
	public int getRGB() {
		// must use longs to avoid negative overflow
		int red = MyDouble.truncate0to255(getRed());
		int green = MyDouble.truncate0to255(getGreen());
		int blue = MyDouble.truncate0to255(getBlue());
		int alpha = MyDouble.truncate0to255(getAlpha());
		return ((alpha * 256 + red) * 256 + green) * 256 + blue;
	}

	public static GColor newColor(float r, float g, float b, float a) {
		return newColor((int) (r * 255), (int) (g * 255), (int) (b * 255),
				(int) (a * 255));
	}

	public static GColor newColor(float r, float g, float b) {
		return newColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
	}

	public void getRGBColorComponents(float[] rgb) {
		rgb[0] = (float) (getRed() / 255.0);
		rgb[1] = (float) (getGreen() / 255.0);
		rgb[2] = (float) (getBlue() / 255.0);

	}

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

	public static String getColorString(GColor fillColor) {
		String ret = "rgba(" + fillColor.getRed() + "," + fillColor.getGreen()
				+ ","
				+ fillColor.getBlue() + "," + (fillColor.getAlpha() / 255d)
				+ ")";

		Log.debug("ret = " + ret);
		return ret;
	}

	private static final double FACTOR = 0.7;

	public GColor darker() {
		return GColor.newColor(Math.max((int) (getRed() * FACTOR), 0),
				Math.max((int) (getGreen() * FACTOR), 0),
				Math.max((int) (getBlue() * FACTOR), 0));
	}

	public GColor brighter() {
		return GColor.newColor(Math.min((int) (getRed() / FACTOR), 255),
				Math.min((int) (getGreen() / FACTOR), 255),
				Math.min((int) (getBlue() / FACTOR), 255));
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof GColor)) {
			return false;
		}
		GColor other = (GColor) object;
		return other.red == this.red && other.green == this.green
				&& other.blue == this.blue && other.alpha == this.alpha;
	}

	@Override
	public int hashCode() {
		return hash(red, green, blue, alpha);
	}

	private static int hash(int red2, int green2, int blue2, int alpha2) {
		return ((alpha2 * 256 + red2) * 256 + green2) * 256 + blue2;
	}

	@Override
	public String toString() {
		return getColorString(this);
	}

}