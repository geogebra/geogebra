package org.geogebra.common.awt;

import java.util.HashMap;

import org.geogebra.common.util.debug.Log;

/**
 * @author michael
 * 
 *         Thin class to just contain the color (as a single int)
 * 
 *         a HashMap is used to recycle colors (to avoid extra "new GColor()"s)
 *
 */
public final class GColor implements GPaint {

	// MUST be first in class
	private static HashMap<Integer, GColor> map = new HashMap<>();

	/** WHITE */
	public static final GColor WHITE = newColor(255, 255, 255);
	/** BLACK */
	public static final GColor BLACK = newColor(0, 0, 0);
	/** RED */
	public static final GColor RED = newColor(255, 0, 0);
	/** ORANGE */
	public static final GColor ORANGE = newColor(255, 127, 0);
	/** YELLOW */
	public static final GColor YELLOW = newColor(255, 255, 0);
	/** GREEN */
	public static final GColor GREEN = newColor(0, 255, 0);
	/** BLUE */
	public static final GColor BLUE = newColor(0, 0, 255);
	/** CYAN */
	public static final GColor CYAN = newColor(0, 255, 255);
	/** DARK_CYAN */
	public static final GColor DARK_CYAN = newColor(99, 219, 219);
	/** DARK_GREEN */
	public static final GColor DARK_GREEN = newColor(0, 127, 0);
	/** MAGENTA */
	public static final GColor MAGENTA = newColor(255, 0, 255);
	/** LIGHTEST_GRAY */
	public static final GColor LIGHTEST_GRAY = newColor(230, 230, 230);
	/** LIGHT_GRAY */
	public static final GColor LIGHT_GRAY = newColor(192, 192, 192);
	/** GEOGEBRA_GRAY */
	public static final GColor GEOGEBRA_GRAY = newColor(102, 102, 102);
	/** GRAY */
	public static final GColor GRAY = newColor(128, 128, 128);
	/** DARK_GRAY */
	public static final GColor DARK_GRAY = newColor(68, 68, 68);
	/** PURPLE */
	public static final GColor PURPLE = newColor(102, 102, 255);
	/** PURPLE A400 */
	public static final GColor PURPLE_A400 = newColor(213, 0, 249);
	/** GEOGEBRA_BLUE */
	public static final GColor GEOGEBRA_BLUE = newColor(153, 153, 255);
	/** MOW PURPLE */
	public static final GColor MOW_PURPLE = newColor(163, 136, 212);
	/** MOW MEBIS TEAL */
	public static final GColor MOW_MEBIS_TEAL = newColor(0, 168, 213);
	/** MOW MEBIS TEAL with alpha */
	public static final GColor MOW_MEBIS_TEAL_50 = newColor(0, 168, 213, 128);
	/** MOW TEXT PRIMARY */
	public static final GColor TEXT_PRIMARY = newColor(0, 0, 0, 138);
	/** MOW WIDGET BACKGROUND */
	public static final GColor MOW_WIDGET_BACKGROUND = newColor(245, 245, 245);
	/** MOW GREEN */
	public static final GColor MOW_GREEN = newColor(46, 125, 50);
	/** ERROR RED */
	public static final GColor ERROR_RED = newColor(176, 0, 32);

	/** MOW RULER */
	public static final GColor MOW_RULER = newColor(192, 192, 192);
	/** MOW SUBGRID */
	public static final GColor MOW_SUBGRID = newColor(229, 229, 229);

	// Selection color for inline text and table in Tafel
	public static final GColor MOW_SELECTION_COLOR = newColor(0, 168, 213, 31);

	public static final GColor TABLE_HEADING_COLOR = newColor(110, 101, 179, 122);

	public static final GColor MOW_TABLE_HEADING_COLOR = newColor(65, 121, 140, 102);

	/**
	 * color stored as ARGB order chosen so that it can be sent as an integer
	 * directly to
	 * https://developer.android.com/reference/android/graphics/Color.html
	 */
	private final int valueARGB;

	private static final double FACTOR = 0.7;

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
	private GColor(int r, int g, int b, int a) {
		this.valueARGB = hashRGBA(r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
	}

	private GColor(int argb) {
		this.valueARGB = argb;
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
		GColor ret;
		int arbg = rgb | (0xff000000);
		synchronized (map) {
			ret = map.get(arbg);
			if (ret == null) {
				ret = new GColor(arbg);
				map.put(arbg, ret);
			}
		}

		return ret;

	}

	private static int getRed(int rgba) {
		return (rgba >> 16) & 0xFF;
	}

	private static int getGreen(int rgba) {
		return (rgba >> 8) & 0xFF;
	}

	private static int getBlue(int rgba) {
		return (rgba >> 0) & 0xFF;
	}

	private static int getAlpha(int rgba) {
		return (rgba >> 24) & 0xff;
	}

	/**
	 * @return red (0 - 255)
	 */
	public int getRed() {
		return getRed(valueARGB);
	}

	/**
	 * @return green (0 - 255)
	 */
	public int getGreen() {
		return getGreen(valueARGB);
	}

	/**
	 * @return blue (0 - 255)
	 */
	public int getBlue() {
		return getBlue(valueARGB);
	}

	/**
	 * @return alpha (0 - 255)
	 */
	public int getAlpha() {
		return getAlpha(valueARGB);
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
		GColor ret;
		int argb = hashRGBA(r, g, b, a);
		synchronized (map) {
			ret = map.get(argb);
			if (ret == null) {
				ret = new GColor(r, g, b, a);
				map.put(argb, ret);
			}
		}

		return ret;
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
		while (!checkColorRatioWhite(fgRed, fgGreen, fgBlue)
				&& loopCounter < 50) {
			// create a slightly darker version of the color
			fgRed = Math.max(fgRed - difference, 0);
			fgGreen = Math.max(fgGreen - difference, 0);
			fgBlue = Math.max(fgBlue - difference, 0);
			loopCounter++;
		}

		if (!checkColorRatioWhite(fgRed, fgGreen, fgBlue)) {
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
			int fgBlue) {
		int diff_hue = 3 * 255 - fgRed - fgBlue - fgGreen;

		double diff_brightness = 255
				- GColor.getGrayScale(fgGreen, fgRed, fgBlue);

		return diff_brightness > 125 && diff_hue > 500;
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
	 * 
	 * @return gray scale GColor corresponding to this color
	 */
	public GColor createGrayScale() {
		int gray = (int) getGrayScale();
		return GColor.newColor(gray, gray, gray, getAlpha());
	}

	/**
	 * This method could return Long, but it returns Integer for
	 * backwards-compatibility, even if it's negative
	 * 
	 * @return int ARBG
	 */
	public int getARGB() {
		return valueARGB;
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
	public static GColor newColor(double r, double g, double b, double a) {
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
	public static GColor newColor(double r, double g, double b) {
		return newColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
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
	public static int fromHSBtoRGB(double hue, double saturation,
			double brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			double h = (hue - Math.floor(hue)) * 6.0f;
			double f = h - Math.floor(h);
			double p = brightness * (1.0f - saturation);
			double q = brightness * (1.0f - saturation * f);
			double t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			default:
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
		if (!(object instanceof GColor)) {
			return false;
		}
		GColor other = (GColor) object;
		return other.valueARGB == this.valueARGB;
	}

	@Override
	public int hashCode() {
		return valueARGB;
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
	 * @return new derived color with alpha set to new value
	 */
	public GColor deriveWithAlpha(int alpha) {

		return newColor(getRed(), getGreen(), getBlue(), alpha);
	}

	/**
	 * 
	 * @param color1
	 *            first color
	 * @param color2
	 *            second color
	 * @param mix
	 *            ratio of second color, from [0,1]
	 * @param alpha
	 *            output opacity
	 * @return color1 * (1-mix) + color2 * mix and force alpha
	 */
	public static GColor mixColors(GColor color1, GColor color2,
			double mix, int alpha) {

		int r = (int) (color1.getRed() * (1 - mix) + color2.getRed() * mix);
		int g = (int) (color1.getGreen() * (1 - mix) + color2.getGreen() * mix);
		int b = (int) (color1.getBlue() * (1 - mix) + color2.getBlue() * mix);

		return newColor(r, g, b, alpha);

	}

	/**
	 * @return slightly darker color, using quadratic /cubic polynomials for
	 *         each part
	 */
	public GPaint slightlyDarker() {
		return newColor(slightlyDarkerRed(getRed()),
				slightlyDarkerGreen(getGreen()), slightlyDarkerBlue(getBlue()),
				getAlpha());
	}

	private static int slightlyDarkerRed(double x) {
		return (int) Math.round(0.0011372 * x * x + 0.61590 * x);
	}

	private static int slightlyDarkerGreen(double x) {
		return (int) Math.round(0.0019424 * x * x + 0.41057 * x);
	}

	private static int slightlyDarkerBlue(double x) {
		return (int) Math.round(
				0.0000084096 * x * x * x + 0.0012150 * x * x + 0.049231 * x);
	}

	/**
	 * @param orig
	 *            The original color
	 * @return brighter color for grid.
	 */
	public static GColor getSubGridColor(GColor orig) {
		return GColor.newColor(orig.getRed(), orig.getGreen(), orig.getBlue(), 60);
	}

	/**
	 * Convert a html color string to GColor
	 * @param colorString #RGB, #RGBA, #RRGGBB or #RRGGBBAA in hexadecimal
	 * @return GColor represented by the string, or null, if bad parameter
	 */
	public static GColor parseHexColor(String colorString) {
		if (colorString == null || !colorString.startsWith("#")) {
			return null;
		}

		try {
			int length = colorString.length() - 1;

			int red = 0;
			int green = 0;
			int blue = 0;
			int alpha = 255;

			if (length == 3 || length == 4) {
				red = 16 * Integer.parseInt(colorString.substring(1, 2), 16);
				green = 16 * Integer.parseInt(colorString.substring(2, 3), 16);
				blue = 16 * Integer.parseInt(colorString.substring(3, 4), 16);
			}

			if (length == 4) {
				alpha = 16 * Integer.parseInt(colorString.substring(4, 5), 16);
			}

			if (length == 6 || length == 8) {
				red = Integer.parseInt(colorString.substring(1, 3), 16);
				green = Integer.parseInt(colorString.substring(3, 5), 16);
				blue = Integer.parseInt(colorString.substring(5, 7), 16);
			}

			if (length == 8) {
				alpha = Integer.parseInt(colorString.substring(7, 9), 16);
			}

			return newColor(red, green, blue, alpha);
		} catch (NumberFormatException e) {
			Log.error("Invalid color code");
			return null;
		}
	}
}