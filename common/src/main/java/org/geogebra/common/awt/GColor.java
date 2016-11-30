package org.geogebra.common.awt;

import java.util.HashMap;
import java.util.Iterator;

import org.geogebra.common.factories.AwtFactory;

/**
 * @author michael
 * 
 *         Thin class to just contain the color (as a single int)
 * 
 *         GColorN is then a wrapper for the native color object (eg GColorD
 *         wraps java.wat.Color)
 * 
 *         The corresponding GColorN's are stored in a HashMap so they can be
 *         recycled
 *
 */
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


	private final int value;

	private static HashMap<GColor, GColorN> map = new HashMap<GColor, GColorN>();

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 */
	public GColor(int r, int g, int b) {
		this(r, g, b, 255);
	}

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 * @param a
	 *            alpha (0-255)
	 */
	public GColor(int r, int g, int b, int a) {
		this.value = hashRGBA(r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
	}

	/**
	 * @param argb
	 *            ARGB
	 */
	public GColor(int argb) {
		this.value = argb;
	}

	/**
	 * Creates an opaque sRGB color with the specified combined RGB value
	 * consisting of the red component in bits 16-23, the green component in
	 * bits 8-15, and the blue component in bits 0-7. Alpha is defaulted to 255.
	 * 
	 * @param rgb
	 *            RGB
	 * @return new color
	 */
	public static GColor newColorRGB(int rgb) {

		return new GColor((rgb & 0x00ffffff) | 0xff000000);
	}

	// private void log() {
	// Log.debug("storing " + getColorString(this));
	// Log.error("map length = " + map.size());
	//
	// // Log.printStacktrace("");
	// }

	/**
	 * @return red (0 - 255)
	 */
	public int getRed() {
		return (value >> 16) & 0xFF;
	}

	/**
	 * @return green (0 - 255)
	 */
	public int getGreen() {
		return (value >> 8) & 0xFF;
	}

	/**
	 * @return blue (0 - 255)
	 */
	public int getBlue() {
		return (value >> 0) & 0xFF;
	}

	/**
	 * @return alpha (0 - 255)
	 */
	public int getAlpha() {
		return (value >> 24) & 0xff;
	}

	/**
	 * @return native color object (wrapped)
	 */
	public GColorN getColor() {

		GColorN ret = map.get(this);

		if (ret == null) {
			// color hasn't been used yet, need to create it
			ret = AwtFactory.getPrototype().newColor(getRed(), getGreen(),
					getBlue(), getAlpha());
			synchronized (map) {
				map.put(this, ret);
			}

		}

		return ret;

	}

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 * @return new color
	 */
	public static GColor newColor(int r, int g, int b) {
		return newColor(r, g, b, 255);
	}

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 * @param a
	 *            alpha (0-255)
	 * @return new color
	 */
	public static GColor newColor(int r, int g, int b, int a) {

		synchronized (map) {

			Iterator<GColor> it = map.keySet().iterator();

			while (it.hasNext()) {
				GColor col = it.next();

				if (col.value == hashRGBA(r, g, b, a)) {
					return col;
				}
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

	// public int compareTo(GColor c) {
	// if (getRed() < c.getRed()) {
	// return -1;
	// }
	// if (getRed() > c.getRed()) {
	// return 1;
	// }
	// if (getGreen() < c.getGreen()) {
	// return -1;
	// }
	// if (getGreen() > c.getGreen()) {
	// return 1;
	// }
	// if (getBlue() < c.getBlue()) {
	// return -1;
	// }
	// if (getBlue() > c.getBlue()) {
	// return 1;
	// }
	// if (getAlpha() < c.getAlpha()) {
	// return -1;
	// }
	// if (getAlpha() > c.getAlpha()) {
	// return 1;
	// }
	//
	// return 0;
	// }

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
	 * @return int ARBG
	 */
	public int getRGB() {
		return value;
	}

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 * @param a
	 *            alpha (0-255)
	 * @return new color
	 */
	public static GColor newColor(float r, float g, float b, float a) {
		return newColor((int) (r * 255), (int) (g * 255), (int) (b * 255),
				(int) (a * 255));
	}

	/**
	 * @param r
	 *            red (0-1)
	 * @param g
	 *            green (0-1)
	 * @param b
	 *            blue (0-1)
	 * @return new color
	 */
	public static GColor newColor(float r, float g, float b) {
		return newColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
	}

	/**
	 * @param rgb
	 *            puts rgb values in array
	 */
	public void getRGBColorComponents(float[] rgb) {
		rgb[0] = (float) (getRed() / 255.0);
		rgb[1] = (float) (getGreen() / 255.0);
		rgb[2] = (float) (getBlue() / 255.0);

	}

	/**
	 * @param hue
	 *            (0-1)
	 * @param saturation
	 *            (0-1)
	 * @param brightness
	 *            (0-1)
	 * @return new color as ARGB
	 */
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

	/**
	 * @param color
	 *            color
	 * @return HTML5 color string eg rgba(255,0,0,0.5)
	 */
	public static String getColorString(GColor color) {
		String ret = "rgba(" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + "," + (color.getAlpha() / 255d) + ")";

		return ret;
	}

	private static final double FACTOR = 0.7;

	/**
	 * @return darker color
	 */
	public GColor darker() {
		return GColor.newColor(Math.max((int) (getRed() * FACTOR), 0),
				Math.max((int) (getGreen() * FACTOR), 0),
				Math.max((int) (getBlue() * FACTOR), 0));
	}

	/**
	 * @return brighter color
	 */
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
		return other.value == this.value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	/**
	 * @param r
	 *            red (0-255)
	 * @param g
	 *            green (0-255)
	 * @param b
	 *            blue (0-255)
	 * @param a
	 *            alpha (0-255)
	 * @return ARGB as an int
	 */
	public static int hashRGBA(int r, int g, int b, int a) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0);
	}

	@Override
	public String toString() {
		return getColorString(this);
	}

	/**
	 * @param alpha
	 *            0 - 255
	 * @return new drived color with alpha set to new value
	 */
	public GColor deriveWithAlpha(int alpha) {
		int newARGB = value & 0x00ffffff;
		
		newARGB = newARGB | ((alpha & 0xff) << 24);

		return new GColor(newARGB);
	}

}