package org.geogebra.common.awt;

import java.util.HashMap;

import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
	/** Highlight */
	public static final GColor HIGHLIGHT_GRAY = newColor(114, 114, 114);
	/** PURPLE */
	public static final GColor PURPLE = newColor(102, 102, 255);
	/** PURPLE A400 */
	public static final GColor PURPLE_A400 = newColor(213, 0, 249);
	/** MOW TEXT PRIMARY */
	public static final GColor TEXT_PRIMARY = newColor(0, 0, 0, 138);
	/** MOW WIDGET BACKGROUND */
	public static final GColor MOW_WIDGET_BACKGROUND = newColor(245, 245, 245);
	/** MOW GREEN */
	public static final GColor MOW_GREEN = newColor(46, 125, 50);
	/** ERROR RED */
	public static final GColor ERROR_RED_BACKGROUND = newColorRGB(0xF1D1D7);
	public static final GColor ERROR_RED_BORDER = newColorRGB(0xce5c71);
	/** Table values points color for y values */
	public static final GColor Y_POINT_COLOR = newColor(76, 66, 161);

	/** MOW RULER */
	public static final GColor MOW_RULER = LIGHT_GRAY;
	/** MOW SUBGRID */
	public static final GColor MOW_SUBGRID = newColor(229, 229, 229);

	// Selection color for inline text and table in Tafel
	public static final GColor MOW_SELECTION_COLOR = newColor(151, 95, 168, 51);

	public static final GColor TABLE_HEADING_COLOR = newColor(110, 101, 179, 122);

	public static final GColor MOW_TABLE_HEADING_COLOR = newColorRGB(0xD7D0DB);

	public static final GColor DARK_RED = newColorRGB(0xD32F2F);

	public static final GColor MIND_MAP_PARENT_BG_COLOR = newColorRGB(0xE2E1F0);

	public static final GColor MIND_MAP_PARENT_BORDER_COLOR = newColor(76, 66, 161);

	public static final GColor MIND_MAP_CHILD_BG_COLOR = newColorRGB(0xF7F6FF);

	public static final GColor MIND_MAP_CHILD_BORDER_COLOR = newColor(153, 132, 255);

	public static final GColor MOW_MIND_MAP_PARENT_BG_COLOR = newColorRGB(0xD7D0DB);

	public static final GColor MOW_MIND_MAP_PARENT_BORDER_COLOR = newColorRGB(0x350D49);

	public static final GColor MOW_MIND_MAP_CHILD_BG_COLOR = newColorRGB(0xEADFEE);

	public static final GColor MOW_MIND_MAP_CHILD_BORDER_COLOR = newColorRGB(0x975FA8);

	public static final GColor MIND_MAP_PLUS_INACTIVE = newColor(189, 189, 189);

	public static final GColor MIND_MAP_PLUS_ACTIVE = newColor(0, 132, 117);

	public static final GColor MIND_MAP_CONNECTION = newColorRGB(0xDEDEDE);

	public static final GColor MOW_MIND_MAP_PLUS_ACTIVE = newColorRGB(0x975FA8);
	public static final GColor DEFAULT_AXES_COLOR = newColorRGB(0x252525);

	public static final GColor DEFAULT_PURPLE = newColorRGB(0X6557D2);

	/**
	 * color stored as ARGB order chosen so that it can be sent as an integer
	 * directly to
	 * https://developer.android.com/reference/android/graphics/Color.html
	 */
	private final int valueARGB;
	private double luminance = -1;

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
		int arbg = rgb | 0xff000000;
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
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha [0,1]
	 * @return gColor
	 */
	public static GColor newColor(String r, String g, String b, String a) {
		int red = Integer.parseInt(r);
		int green = Integer.parseInt(g);
		int blue = Integer.parseInt(b);
		double alpha = Double.parseDouble(a);
		return newColor(red, green, blue, (int) (alpha * 255));
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
		GColor ret = color;
		int loopCounter = 0;
		while (WHITE.getContrast(ret) < 4.5
				&& loopCounter < 50) {
			// create a slightly darker version of the color
			int fgRed = Math.max(color.getRed() - 5 * loopCounter, 0);
			int fgGreen = Math.max(color.getGreen() - 5 * loopCounter, 0);
			int fgBlue = Math.max(color.getBlue() - 5 * loopCounter, 0);
			ret = newColor(fgRed, fgGreen, fgBlue);
			loopCounter++;
		}

		if (WHITE.getContrast(ret) < 4.5) {
			// If the color could not be set correctly, the font color is set to
			// black.
			return GColor.BLACK;
		}

		return ret;
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
	 * Returns true if this color is darker than the color in the parameter.
	 * Darkerk means that all RGB components have a lower value.
	 * @param color color
	 * @return true if this color is darker
	 */
	public boolean isDarkerThan(GColor color) {
		return getRed() < color.getRed()
				&& getBlue() < color.getBlue()
				&& getGreen() < color.getGreen();
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
	public static GColor newColorHSB(double hue, double saturation,
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
		return newColor(r, g, b);
	}

	/**
	 * @param color color
	 * @return HTML5 color string eg rgba(255,0,0,0.5)
	 */
	public static String getColorString(GColor color) {
		String ret = "rgba(" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + "," + (color.getAlpha() / 255d) + ")";

		return ret;
	}

	/**
	 * @param colorStr HTML5 color string eg rgba(255,0,0,0.5)
	 * @return GColor
	 */
	public static GColor getGColor(String colorStr) {
		if (colorStr != null && colorStr.startsWith("rgba(") && colorStr.endsWith(")")) {
			String colorSubStr = colorStr.substring(5, colorStr.length() - 1);
			String[] colorValues = colorSubStr.split(",");
			return colorValues.length == 4 ? newColor(colorValues[0],
					colorValues[1], colorValues[2], colorValues[3]) : null;
		}
		return null;
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

	/**
	 * Calculate darker color, adjustment based on lightness
	 * @param bgColor - background color
	 * @return adjusted color
	 */
	public static GColor getBorderColorFrom(GColor bgColor) {
		float[] hslValues = rgbToHsl(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
		if (hslValues[2] > 0.4) {
			hslValues[2] -= 0.3;
		} else {
			hslValues[2] -= 0.2;
		}
		hslValues[2] = Math.max(hslValues[2], 0);
		return newColorHSL(hslValues[0], hslValues[1], hslValues[2]);
	}

	/**
	 * Calculate brighter color, adjustment based on lightness
	 * @param bgColor - background color
	 * @return adjusted color
	 */
	public static GColor getBrightBorderColorFrom(GColor bgColor) {
		float[] hslValues = rgbToHsl(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
		if (hslValues[2] < 0.6) {
			hslValues[2] += 0.3;
		} else {
			hslValues[2] += 0.2;
		}
		hslValues[2] = Math.min(hslValues[2], 1);
		return newColorHSL(hslValues[0], hslValues[1], hslValues[2]);
	}

	/**
	 * Converts an HSL color value to RGB. Conversion formula
	 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
	 * Assumes h, s, and l are contained in the set [0, 1] and
	 * returns r, g, and b in the set [0, 255].
	 *
	 * @param h - hue
	 * @param s - saturation
	 * @param l - lightness
	 * @return int array, the RGB representation
	 */
	public static GColor newColorHSL(double h, double s, double l) {
		double r, g, b;

		if (s == 0f) {
			r = g = b = l; // achromatic
		} else {
			double q = l < 0.5f ? l * (1 + s) : l + s - l * s;
			double p = 2 * l - q;
			r = hueToRgb(p, q, h + 1f / 3f);
			g = hueToRgb(p, q, h);
			b = hueToRgb(p, q, h - 1f / 3f);
		}
		return newColor(to255(r), to255(g), to255(b));
	}

	private static int to255(double v) {
		return (int) Math.min(255, 256 * v);
	}

	/**
	 * Helper method that converts hue to rgb
	 * @return color
	 */
	private static double hueToRgb(double p, double q, double t) {
		double tt = t;
		if (tt < 0f) {
			tt += 1f;
		}
		if (tt > 1f) {
			tt -= 1f;
		}
		if (tt < 1f / 6f) {
			return p + (q - p) * 6f * tt;
		}
		if (tt < 1f / 2f) {
			return q;
		}
		if (tt < 2f / 3f) {
			return p + (q - p) * (2f / 3f - tt) * 6f;
		}
		return p;
	}

	/**
	 * Converts an RGB color value to HSL.
	 * Assumes r, g, and b in the set [0, 255] and
	 * returns h, s, and l contained in the set [0, 1]
	 *
	 * @param pR - red
	 * @param pG - green
	 * @param pB - blue
	 * @return float array, the HSL representation
	 */
	@SuppressFBWarnings("FE_FLOATING_POINT_EQUALITY")
	public static float[] rgbToHsl(int pR, int pG, int pB) {
		float r = pR / 255f;
		float g = pG / 255f;
		float b = pB / 255f;

		float max = (r > g && r > b) ? r : (g > b) ? g : b;
		float min = (r < g && r < b) ? r : (g < b) ? g : b;

		float h, s, l;
		l = (max + min) / 2.0f;

		if (max == min) {
			h = s = 0.0f;
		} else {
			float d = max - min;
			s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

			if (r > g && r > b) {
				h = (g - b) / d + (g < b ? 6.0f : 0.0f);
			} else if (g > b) {
				h = (b - r) / d + 2.0f;
			} else {
				h = (r - g) / d + 4.0f;
			}
			h /= 6.0f;
		}

		float[] hsl = {h, s, l};
		return hsl;
	}

	/**
	 * @param other other color
	 * @return contrast with other color
	 */
	public double getContrast(GColor other) {
		double ratio = (getLuminance() + 0.05) / (other.getLuminance() + 0.05);
		return ratio < 1.0 ? 1 / ratio : ratio;
	}

	/**
	 * See <a href="https://www.w3.org/TR/2008/REC-WCAG20-20081211/#sRGB">WCAG definition</a>
	 * @return relative luminance
	 */
	public double getLuminance() {
		if (luminance < 0) {
			double lumR = lumComponent(getRed()), lumG = lumComponent(getGreen()),
					lumB = lumComponent(getBlue());
			luminance = 0.2126 * lumR + 0.7152 * lumG + 0.0722 * lumB;
		}
		return luminance;
	}

	private double lumComponent(int val) {
		double valD = val / 255.0;
		return valD <= 0.03928 ? valD / 12.92 : Math.pow((valD + 0.055) / 1.055, 2.4);
	}
}

// If(x <= 0.03928 , x / 12.92, ((x + 0.055) / 1.055)^2.4)