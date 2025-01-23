package org.geogebra.common.main;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.StringUtil;

/**
 * Color constants for swatches, spreadsheet etc.
 */
public class GeoGebraColorConstants {

	// table colors
	/** background of selected cell */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR = GColor
			.newColor(214, 224, 245);
	/** background of selected row / column header */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR_HEADER = GColor.LIGHT_GRAY;

	/** background of row / column header */
	public static final GColor TABLE_BACKGROUND_COLOR_HEADER = GColor
			.newColor(232, 238, 247);
	/** grid color for spreadsheet */
	public static final GColor TABLE_GRID_COLOR = GColor.GRAY;

	// ggb color constants

	/** violet */
	public static final GColor VIOLET = rgb(127, 0, 255);

	/** lime */
	public static final GColor LIME = rgb(191, 255, 0);
	/** pink */
	public static final GColor PINK = rgb(255, 192, 203);
	/** light orange (wikipedia papaya whip) */
	public static final GColor LIGHTORANGE = rgb(255, 239, 213);
	/** light yellow (wikipedia lemon chiffon) */
	public static final GColor LIGHTYELLOW = rgb(255, 250, 205);
	/** aqua (wikipedia pale aqua) */
	public static final GColor AQUA = rgb(188, 212, 230);
	/** light purple (wikipedia periwinkle) */
	public static final GColor LIGHTPURPLE = rgb(204, 204, 255);
	/** light violet (wikipedia mauve) */
	public static final GColor LIGHTVIOLET = rgb(224, 176, 255);
	/** turqoise (wikipedia pale turquoise) */
	public static final GColor TURQUOISE = rgb(175, 238, 238);
	/** light green (wikipedia tea green) */
	public static final GColor LIGHTGREEN = rgb(208, 240, 192);
	/** maroon */
	public static final GColor MAROON = rgb(128, 0, 0);

	/**
	 * Dark green: don't change: default Angle color
	 */
	public static final GColor GGB_GREEN = rgb(0, 100, 0);
	/** purple */
	public static final GColor GGB_CLASSIC_PURPLE = rgb(153, 51, 255);
	/** dark red, for functions */
	public static final GColor GGB_RED = rgb(204, 0, 0);
	/** violet */
	public static final GColor GGB_VIOLET = rgb(244, 0, 153);
	/** intersection point gray */
	public static final GColor GGB_GRAY = rgb(102, 102, 102);
	/** polygon brown */
	public static final GColor GGB_BROWN = rgb(153, 51, 0);
	/** intersection line orange */
	public static final GColor GGB_ORANGE = rgb(199, 80, 0);
	/**
	 * Light blue: don't change: default Point on path color
	 */
	public static final GColor LIGHTBLUE = rgb(125, 125, 255);
	/** gold */
	public static final GColor GOLD = GColor.newColor(255, 215, 0);
	/** dark blue (wikipedia persian blue) */
	public static final GColor DARKBLUE = rgb(28, 57, 187);
	/** indigo */
	public static final GColor INDIGO = rgb(75, 0, 130);
	/** purple */
	public static final GColor PURPLE = rgb(128, 0, 128);
	/** crimson */
	public static final GColor CRIMSON = rgb(220, 20, 60);

	/** 87.5% gray */
	public static final GColor GRAY7 = grayN(7);
	/** 75% gray (silver) */
	public static final GColor GRAY6 = grayN(6);
	/** 62.5% gray */
	public static final GColor GRAY5 = grayN(5);
	/** 50% gray */
	public static final GColor GRAY4 = grayN(4);
	/** 37.5% gray */
	public static final GColor GRAY3 = grayN(3);
	/** 25% gray */
	public static final GColor GRAY2 = grayN(2);
	/** 12.5% gray */
	public static final GColor GRAY1 = grayN(1);
	/** 87.5% gray */
	public static final GColor DARK_GRAY = GRAY7;

	/** 37.5% gray */
	public static final GColor LIGHT_GRAY = GRAY3;

	/** 75% gray */
	public static final GColor SILVER = GRAY6;

	// input coloring
	/** defined objects (violet) */
	public static final GColor DEFINED_OBJECT_COLOR = GColor.newColor(102, 102,
			255);
	/** undefined objects */
	public static final GColor UNDEFINED_OBJECT_COLOR = GRAY3;

	/** local objects (local variables, blue) */
	public static final GColor LOCAL_OBJECT_COLOR = GColor.newColor(0, 102,
			255);
	/** unbalanced brackets */
	public static final GColor UNBALANCED_BRACKET_COLOR = GColor.RED;

	/** balanced bracket (green) */
	public static final GColor BALANCED_BRACKET_COLOR = GColor.newColor(0, 127,
			0);
	/** text mode */
	public static final GColor INPUT_TEXT_COLOR = GRAY4;

	/** default color */
	public static final GColor INPUT_DEFAULT_COLOR = GColor.BLACK;

	/** MOW Color: teal */
	public static final GColor MOW_TEAL = GColor.newColor(0, 168, 168);
	/** MOW Color: magenta */
	public static final GColor MOW_MAGENTA = GColor.newColor(204, 0, 153);
	/** MOW Color: yellow */
	public static final GColor MOW_YELLOW = GColor.newColor(255, 204, 0);

	// New colors for graphing/geometry app objects
	/** GEOGEBRA_OBJECT_GREEN */
	public static final GColor GEOGEBRA_OBJECT_GREEN = rgb(0x2E7D32);
	/** GEOGEBRA_OBJECT_BLUE */
	public static final GColor GEOGEBRA_OBJECT_BLUE = rgb(0x1565C0);
	/** GEOGEBRA_OBJECT_RED */
	public static final GColor GEOGEBRA_OBJECT_RED = rgb(0xD32F2F);
	/** GEOGEBRA_OBJECT_GREY */
	public static final GColor GEOGEBRA_OBJECT_GREY = rgb(0x616161);
	/** GEOGEBRA_OBJECT_BLACK */
	public static final GColor GEOGEBRA_OBJECT_BLACK = GColor.BLACK;
	/** GEOGEBRA_OBJECT_PINK */
	public static final GColor GEOGEBRA_OBJECT_PINK = GColor.newColor(216, 27,
			96);

	/** Accent color for GeoGebra */
	public static final GColor GEOGEBRA_ACCENT = GColor.newColor(101, 87, 210);

	/** Accent color for mebis */
	public static final GColor MEBIS_ACCENT = rgb(0x975FA8);

	/** MASK color */
	public static final GColor MEBIS_MASK = GColor.newColor(0, 0x63, 0x7d);
	public static final GColor DISABLED_BACKGROUND = rgb(0xa3a3a3);
	public static final GColor DISABLED_BORDER = rgb(0x949494);
	public static final GColor DEFAULT_BORDER = rgb(0x757575);

	public static final GColor NEUTRAL_200 = rgb(0xF3F2F7);
	public static final GColor NEUTRAL_300 = rgb(0xE6E6EB);
	public static final GColor NEUTRAL_500 = rgb(0xB4B3BA);
	public static final GColor NEUTRAL_700 = rgb(0x6E6D73);
	public static final GColor NEUTRAL_900 = rgb(0x1C1C1F);

	public static final GColor PURPLE_100 = rgb(0xF3F0FF);
	public static final GColor PURPLE_600 = rgb(0x6557D2); // primary purple
	public static final GColor PURPLE_700 = rgb(0x5145A8);

	/**
	 * HashMap recording RGB color values with named colors key = color name
	 * from colors.properties value = RBG color
	 */
	private static volatile HashMap<String, GColor> geogebraColor = null; // must
																			// be
																			// declared
																			// volatile
	private static final Object lock = new Object();

	/** popup color menu type: standard */
	public static final int COLORSET_STANDARD = 0;
	/** popup color menu type: background */
	public static final int COLORSET_BGCOLOR = 1;

	/**
	 * Reverse lookup for GeoGebraColors key = RBG color value = color name from
	 * colors.properties
	 */
	private static volatile HashMap<GColor, String> geogebraColorReverse = null;
	private static volatile GColor[] grayColors = null;
	private static volatile GColor[] darkPrimaryColors = null;
	private static volatile GColor[] lightPrimaryColors = null;
	private static volatile GColor[] primaryColors = null;
	private static volatile HashMap<String, GColor> colors = null;

	private static final Object lock2 = new Object();

	/**
	 * initialize (once) only if needed
	 * 
	 * @return map of internal lowercase strings to GColors
	 */
	public static HashMap<String, GColor> getGeoGebraColors() {
		if (geogebraColor == null) { // avoid sync penalty if we can
			synchronized (lock) { // declare a private static Object to use for
									// mutex
				if (geogebraColor == null) { // have to do this inside the sync
					HashMap<String, GColor> geogebraColor0 = new HashMap<>();

					// primary
					geogebraColor0.put("red", GColor.RED);
					geogebraColor0.put("orange", GColor.ORANGE);
					geogebraColor0.put("yellow", GColor.YELLOW);
					geogebraColor0.put("green", GColor.GREEN);
					geogebraColor0.put("cyan", GColor.CYAN);
					geogebraColor0.put("blue", GColor.BLUE);
					geogebraColor0.put("violet", VIOLET);
					geogebraColor0.put("magenta", GColor.MAGENTA);
					geogebraColor0.put("lime", LIME);

					// light primary
					geogebraColor0.put("pink", PINK);
					geogebraColor0.put("lightorange", LIGHTORANGE);
					geogebraColor0.put("lightyellow", LIGHTYELLOW);
					geogebraColor0.put("aqua", AQUA);
					geogebraColor0.put("lightpurple", LIGHTPURPLE);
					geogebraColor0.put("lightviolet", LIGHTVIOLET);
					geogebraColor0.put("turquoise", TURQUOISE);
					geogebraColor0.put("lightgreen", LIGHTGREEN);
					geogebraColor0.put("darkblue", DARKBLUE);

					// dark primary
					geogebraColor0.put("maroon", MAROON);
					geogebraColor0.put("brown", GGB_BROWN);
					geogebraColor0.put("gold", GOLD);
					geogebraColor0.put("darkgreen", GGB_GREEN);
					geogebraColor0.put("lightblue", LIGHTBLUE);
					geogebraColor0.put("indigo", INDIGO);
					geogebraColor0.put("purple", PURPLE);
					geogebraColor0.put("crimson", CRIMSON);

					// white/gray/black
					geogebraColor0.put("white", GColor.WHITE);
					geogebraColor0.put("black", GColor.BLACK);
					geogebraColor0.put("gray7", GRAY7);
					geogebraColor0.put("gray6", GRAY6);
					geogebraColor0.put("gray5", GRAY5);
					geogebraColor0.put("gray", GRAY4);
					geogebraColor0.put("gray3", GRAY3);
					geogebraColor0.put("gray2", GRAY2);
					geogebraColor0.put("gray1", GRAY1);
					geogebraColor0.put("darkgray", DARK_GRAY);
					geogebraColor0.put("lightgray", LIGHT_GRAY);
					geogebraColor0.put("silver", SILVER);

					geogebraColor = geogebraColor0;
				}
			}
		}

		return geogebraColor;
	}

	private static HashMap<GColor, String> getGeoGebraColorReverse() {
		if (geogebraColorReverse == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (geogebraColorReverse == null) { // have to do this inside
													// the sync

					HashMap<GColor, String> geogebraColorReverse0 = new HashMap<>();

					for (Entry<String, GColor> entry : getGeoGebraColors()
							.entrySet()) {
						geogebraColorReverse0.put(entry.getValue(),
								entry.getKey());
					}

					geogebraColorReverse = geogebraColorReverse0;
				}
			}
		}

		return geogebraColorReverse;
	}

	/**
	 * Returns Color object corresponding with given GeoGebra color name string
	 * 
	 * @param app
	 *            application
	 * @param colorName
	 *            localized color name
	 * @return Color object corresponding with given GeoGebra color name string
	 */
	public static GColor getGeogebraColor(App app, String colorName) {

		getGeoGebraColors();

		GColor ret = geogebraColor.get(StringUtil.toLowerCaseUS(colorName));

		if (ret == null) {
			ret = geogebraColor
					.get(app.getLocalization().reverseGetColor(colorName));
		}
		if (ret == null) {
			// will need only English characters
			ret = htmlColorMap().get(colorName.toUpperCase());
		}
		return ret;
	}

	/**
	 * Returns GeoGebra color name string corresponding with given Color object
	 * 
	 * @param app
	 *            application
	 * @param color
	 *            color
	 * @return GeoGebra color name string corresponding with given Color object
	 */
	public static String getGeogebraColorName(App app, GColor color) {
		return app.getLocalization()
				.getColor(getGeoGebraColorReverse().get(color));
	}

	/**
	 * Returns a gray Color where the gray hue is determined by n, an integer
	 * from 1-7. Gray, in RGB values, is given by red = green = blue = (256 -
	 * 32*n) For example: n = 7 gives RGB[32,32,32], v. dark gray n = 6 gives
	 * RGB[64,64,64], dark gray ... n = 1 gives RGB[224,224,224] v.light gray
	 * 
	 * @param n
	 *            degree of gray (1 -7)
	 * @return gray
	 */
	private static GColor grayN(int n) {
		int grayN = 256 - 32 * n;
		return rgb(grayN, grayN, grayN);
	}

	private static GColor[] getPrimaryColors() {

		if (primaryColors == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (primaryColors == null) { // have to do this inside the
												// sync

					getGeoGebraColors();

					GColor[] primaryColors0 = new GColor[9];
					primaryColors0[0] = geogebraColor.get("red");
					primaryColors0[1] = geogebraColor.get("orange");
					primaryColors0[2] = geogebraColor.get("yellow");
					primaryColors0[3] = geogebraColor.get("lime");
					primaryColors0[4] = geogebraColor.get("green");
					primaryColors0[5] = geogebraColor.get("cyan");
					primaryColors0[6] = geogebraColor.get("blue");
					primaryColors0[7] = geogebraColor.get("violet");
					primaryColors0[8] = geogebraColor.get("magenta");

					primaryColors = primaryColors0;

				}
			}
		}

		return primaryColors;
	}

	private static GColor[] getLightPrimaryColors() {

		if (lightPrimaryColors == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (lightPrimaryColors == null) { // have to do this inside the
													// sync

					getGeoGebraColors();

					GColor[] lightPrimaryColors0 = new GColor[9];
					lightPrimaryColors0[0] = null; // for the null icon symbol
													// (for removing
													// bgcolor)
					lightPrimaryColors0[1] = geogebraColor.get("pink");
					lightPrimaryColors0[2] = geogebraColor.get("lightorange");
					lightPrimaryColors0[3] = geogebraColor.get("lightyellow");
					lightPrimaryColors0[4] = geogebraColor.get("lightgreen");
					lightPrimaryColors0[5] = geogebraColor.get("turquoise");
					lightPrimaryColors0[6] = geogebraColor.get("aqua");
					lightPrimaryColors0[7] = geogebraColor.get("lightpurple");
					lightPrimaryColors0[8] = geogebraColor.get("lightviolet");

					lightPrimaryColors = lightPrimaryColors0;

				}
			}
		}

		return lightPrimaryColors;
	}

	private static GColor[] getDarkPrimaryColors() {

		if (darkPrimaryColors == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (darkPrimaryColors == null) { // have to do this inside the
													// sync

					getGeoGebraColors();

					GColor[] darkPrimaryColors0 = new GColor[9];
					darkPrimaryColors0[0] = geogebraColor.get("maroon");
					darkPrimaryColors0[1] = geogebraColor.get("brown");
					darkPrimaryColors0[2] = geogebraColor.get("gold");
					darkPrimaryColors0[3] = geogebraColor.get("darkgreen");
					darkPrimaryColors0[4] = geogebraColor.get("lightblue");
					darkPrimaryColors0[5] = geogebraColor.get("purple");
					darkPrimaryColors0[6] = geogebraColor.get("indigo");
					darkPrimaryColors0[7] = geogebraColor.get("crimson");
					darkPrimaryColors0[8] = geogebraColor.get("pink");

					darkPrimaryColors = darkPrimaryColors0;
				}
			}
		}

		return darkPrimaryColors;
	}

	private static GColor[] getGrayColors() {

		if (grayColors == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (grayColors == null) { // have to do this inside the sync

					GColor[] grayColors0 = new GColor[9];
					grayColors0[0] = getGeoGebraColors().get("white");
					grayColors0[1] = grayN(1);
					grayColors0[2] = grayN(2);
					grayColors0[3] = grayN(3);
					grayColors0[4] = grayN(4);
					grayColors0[5] = grayN(5);
					grayColors0[6] = grayN(6);
					grayColors0[7] = grayN(7);
					grayColors0[8] = getGeoGebraColors().get("black");

					grayColors = grayColors0;
				}
			}
		}

		return grayColors;
	}

	/**
	 * Returns array of localized color names
	 * 
	 * @param app
	 *            application
	 * @param color
	 *            colors
	 * @return array of localized color names
	 */
	public static String[] getColorNames(App app, GColor[] color) {
		String[] s = new String[color.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = getGeogebraColorName(app, color[i]);
		}
		return s;
	}

	/**
	 * Returns array of colors for color popup menus
	 * 
	 * @param colorSetType
	 *            COLORSET_* (background or primary)
	 * @return array of colors for color popup menus
	 */
	public static GColor[] getPopupArray(int colorSetType) {

		GColor[] popupColors = new GColor[27];

		for (int i = 0; i < 9; i++) {
			if (colorSetType == COLORSET_STANDARD) {
				// first row
				popupColors[i] = getPrimaryColors()[i];
				// second row
				popupColors[i + 9] = getDarkPrimaryColors()[i];
				// third row
				popupColors[i + 18] = getGrayColors()[i];
			}

			if (colorSetType == COLORSET_BGCOLOR) {
				// first row
				popupColors[i] = getLightPrimaryColors()[i];
				// second row
				popupColors[i + 9] = getPrimaryColors()[i];
				// third row
				popupColors[i + 18] = getGrayColors()[i];
			}
		}

		return popupColors;
	}

	/**
	 * Returns array of colors for color popup menus
	 * 
	 * @return array of colors for color popup menus
	 */
	public static GColor[] getSimplePopupArray() {

		GColor[] popupColors = new GColor[10];

		popupColors[0] = GGB_GREEN;
		popupColors[1] = GColor.BLUE;
		popupColors[2] = GGB_CLASSIC_PURPLE;
		popupColors[3] = GGB_VIOLET; // rgb(244, 0, 153);
		popupColors[4] = GGB_RED;

		popupColors[5] = GColor.BLACK;
		popupColors[6] = GGB_GRAY; // rgb(102, 102, 102);
		popupColors[7] = GGB_BROWN; // rgb(153, 51, 0);
		popupColors[8] = GGB_ORANGE; // rgb(255, 85, 0);
		popupColors[9] = null; // placeholder for (...) button
		return popupColors;
	}

	/**
	 * 
	 * @return Colors for graphing/geometry apps.
	 */
	public static GColor[] getUnbundledPopupArray() {

		GColor[] popupColors = new GColor[8];

		popupColors[0] = GEOGEBRA_OBJECT_GREEN;
		popupColors[1] = GEOGEBRA_OBJECT_BLUE;
		popupColors[2] = GEOGEBRA_OBJECT_RED;
		popupColors[3] = GGB_ORANGE;
		popupColors[4] = PURPLE_600;
		popupColors[5] = GEOGEBRA_OBJECT_GREY;
		popupColors[6] = GEOGEBRA_OBJECT_BLACK;
		popupColors[7] = null; // placeholder for (...) button
		return popupColors;
	}

	/**
	 * Returns array of colors for MOW color popup.
	 * 
	 * @return array of colors for MOW color popup.
	 */
	public static GColor[] getMOWPopupArray() {

		GColor[] popupColors = new GColor[10];
		popupColors[0] = GColor.BLACK;
		popupColors[1] = GEOGEBRA_OBJECT_GREEN;
		popupColors[2] = MOW_TEAL;
		popupColors[3] = GEOGEBRA_OBJECT_BLUE;
		popupColors[4] = PURPLE_600;
		popupColors[5] = MOW_MAGENTA;
		popupColors[6] = GEOGEBRA_OBJECT_RED;
		popupColors[7] = GGB_ORANGE;
		popupColors[8] = MOW_YELLOW;
		popupColors[9] = null; // placeholder for (...) button
		return popupColors;
	}

	/**
	 * Array of colors intended for the primary color swatch panel of the
	 * GeoGebraColorChooser class.
	 * 
	 * @return array of colors intended for the primary color swatch panel
	 */
	public static GColor[] getPrimarySwatchColors() {

		GColor[] primColors = new GColor[18];

		for (int i = 0; i < 18; i = i + 2) {
			// first row
			primColors[i] = getGrayColors()[i / 2];
			primColors[i + 1] = getPrimaryColors()[i / 2];

		}

		return primColors;
	}

	/**
	 * Array of colors intended for the main color swatch panel of the
	 * GeoGebraColorChooser class.
	 * 
	 * @return colors for main chooser
	 */
	public static GColor[] getMainColorSwatchColors() {
		return new GColor[] {
			// Row 1 RED related colors

			PINK, rgb(255, 153, 204), rgb(255, 102, 153), rgb(255, 51, 102),
			rgb(255, 0, 51), GGB_RED, MAROON, rgb(51, 0, 0),

			// Row 2 ORANGE related colors

			LIGHTORANGE, rgb(255, 204, 51), rgb(255, 153, 0), rgb(255, 153, 51),
			rgb(255, 102, 0), rgb(204, 102, 0), rgb(153, 102, 0),
			rgb(51, 51, 0),

			// Row 3 YELLOW related colors

			LIGHTYELLOW, rgb(255, 255, 153), rgb(255, 255, 102), GOLD,
			rgb(255, 204, 102), rgb(204, 153, 0), GGB_BROWN, rgb(102, 51, 0),

			// Row 4 YELLOW-GREEN ("LIME") related colors

			rgb(204, 255, 204), rgb(204, 255, 102), rgb(153, 255, 0),
			rgb(153, 204, 0), rgb(102, 204, 0), rgb(102, 153, 0),
			rgb(51, 153, 0), rgb(0, 102, 51),

			// Row 5 GREEN related colors

			LIGHTGREEN, rgb(153, 255, 153), rgb(102, 255, 0), rgb(51, 255, 0),
			rgb(0, 204, 0), rgb(0, 153, 0), GGB_GREEN, rgb(0, 51, 0),

			// Row 6 CYAN related colors

			TURQUOISE, rgb(153, 255, 255), rgb(51, 255, 204), rgb(0, 153, 255),
			rgb(0, 153, 204), rgb(0, 102, 153), rgb(0, 51, 204),
			rgb(0, 51, 153),

			// Row 7 BLUE related colors

			AQUA, rgb(153, 204, 255), rgb(102, 204, 255), rgb(102, 153, 255),
			rgb(125, 125, 255), rgb(51, 51, 255), rgb(0, 0, 204), rgb(0, 0, 51),

			// Row 8 VIOLET related colors

			LIGHTPURPLE, rgb(204, 153, 255), rgb(204, 102, 255),
			rgb(153, 102, 255), rgb(102, 0, 204), PURPLE, INDIGO,
			rgb(51, 0, 51),

			// Row 9 MAGENTA related colors

			LIGHTVIOLET, rgb(255, 153, 255), rgb(255, 153, 153),
			rgb(255, 51, 204), CRIMSON, rgb(204, 0, 102), rgb(153, 0, 51),
				rgb(102, 0, 153) };
	}

	/**
	 * @return swatch colors
	 */
	public static GColor[] getSwatchColors() {
		GColor[] primColor = GeoGebraColorConstants.getPrimarySwatchColors();
		GColor[] scolors = GeoGebraColorConstants.getMainColorSwatchColors();
		return new GColor[] { primColor[0], primColor[2], primColor[4],
				primColor[8], primColor[10], primColor[12], GColor.BLACK,
				GeoGebraColorConstants.GEOGEBRA_OBJECT_RED,
				GeoGebraColorConstants.GGB_ORANGE, scolors[19],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN, scolors[43],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE,
				GeoGebraColorConstants.PURPLE_600, scolors[0],
				scolors[8], scolors[16], scolors[32], scolors[40], scolors[48],
				scolors[56], scolors[1], scolors[9], scolors[17], scolors[24],
				scolors[41], scolors[49], scolors[57], scolors[3], scolors[11],
				primColor[5], scolors[33], primColor[11], scolors[51],
				scolors[59], scolors[4], scolors[12], scolors[20], scolors[36],
				scolors[44], scolors[52], scolors[60], scolors[6], scolors[14],
				scolors[22], scolors[38], scolors[46], scolors[54], scolors[62],
				scolors[7], scolors[15], scolors[23], scolors[39], scolors[47],
				scolors[55], scolors[63] };
	}

	/**
	 * initialize (once) only when needed
	 * 
	 * @return map HTML name =&gt; color
	 */
	public static final HashMap<String, GColor> htmlColorMap() {

		if (colors == null) { // avoid sync penalty if we can
			synchronized (lock) { // declare a private static Object to use for
									// mutex
				if (colors == null) { // have to do this inside the sync

					// findbugs DC_PARTIALLY_CONSTRUCTED
					HashMap<String, GColor> colors0 = new HashMap<>();

					// HTML 3.2
					// colors0.put("AQUA", rgb(0x00FFFF));
					// colors0.put("BLACK", rgb(0x000000));
					// colors0.put("BLUE", rgb(0x0000FF));
					colors0.put("FUCHSIA", rgb(0xFF00FF));
					// colors0.put("GRAY", rgb(0x808080));
					// colors0.put("GREEN", rgb(0x008000));
					// colors0.put("LIME", rgb(0x00FF00));
					// colors0.put("MAROON", rgb(0x800000));
					colors0.put("NAVY", rgb(0x000080));
					colors0.put("OLIVE", rgb(0x808000));
					// colors0.put("PURPLE", rgb(0x800080));
					// colors0.put("RED", rgb(0xFF0000));
					// colors0.put("SILVER", rgb(0xC0C0C0));
					colors0.put("TEAL", rgb(0x008080));
					// colors0.put("WHITE", rgb(0xFFFFFF));
					// colors0.put("YELLOW", rgb(0xFFFF00));

					colors0.put("ALICEBLUE", rgb(0xF0F8FF));
					colors0.put("ANTIQUEWHITE", rgb(0xFAEBD7));
					colors0.put("AQUAMARINE", rgb(0x7FFFD4));
					colors0.put("AZURE", rgb(0xF0FFFF));
					colors0.put("BEIGE", rgb(0xF5F5DC));
					colors0.put("BISQUE", rgb(0xFFE4C4));
					colors0.put("BLANCHEDALMOND", rgb(0xFFEBCD));
					colors0.put("BLUEVIOLET", rgb(0x8A2BE2));
					// colors0.put("BROWN", rgb(0xA52A2A));
					colors0.put("BURLYWOOD", rgb(0xDEB887));
					colors0.put("CADETBLUE", rgb(0x5F9EA0));
					colors0.put("CHARTREUSE", rgb(0x7FFF00));
					colors0.put("CHOCOLATE", rgb(0xD2691E));
					colors0.put("CORAL", rgb(0xFF7F50));
					colors0.put("CORNFLOWERBLUE", rgb(0x6495ED));
					colors0.put("CORNSILK", rgb(0xFFF8DC));
					// colors0.put("CRIMSON", rgb(0xDC143C));
					colors0.put("CYAN", rgb(0x00FFFF));
					// colors0.put("DARKBLUE", rgb(0x00008B));
					colors0.put("DARKCYAN", rgb(0x008B8B));
					colors0.put("DARKGOLDENROD", rgb(0xB8860B));
					// colors0.put("DARKGRAY", rgb(0xA9A9A9));
					// colors0.put("DARKGREEN", rgb(0x006400));
					colors0.put("DARKKHAKI", rgb(0xBDB76B));
					colors0.put("DARKMAGENTA", rgb(0x8B008B));
					colors0.put("DARKOLIVEGREEN", rgb(0x556B2F));
					colors0.put("DARKORANGE", rgb(0xFF8C00));
					colors0.put("DARKORCHID", rgb(0x9932CC));
					colors0.put("DARKRED", rgb(0x8B0000));
					colors0.put("DARKSALMON", rgb(0xE9967A));
					colors0.put("DARKSEAGREEN", rgb(0x8FBC8F));
					colors0.put("DARKSLATEBLUE", rgb(0x483D8B));
					colors0.put("DARKSLATEGRAY", rgb(0x2F4F4F));
					colors0.put("DARKTURQUOISE", rgb(0x00CED1));
					colors0.put("DARKVIOLET", rgb(0x9400D3));
					colors0.put("DEEPPINK", rgb(0xFF1493));
					colors0.put("DEEPSKYBLUE", rgb(0x00BFFF));
					colors0.put("DIMGRAY", rgb(0x696969));
					colors0.put("DODGERBLUE", rgb(0x1E90FF));
					colors0.put("FIREBRICK", rgb(0xB22222));
					colors0.put("FLORALWHITE", rgb(0xFFFAF0));
					colors0.put("FORESTGREEN", rgb(0x228B22));
					colors0.put("GAINSBORO", rgb(0xDCDCDC));
					colors0.put("GHOSTWHITE", rgb(0xF8F8FF));
					// colors0.put("GOLD", rgb(0xFFD700));
					colors0.put("GOLDENROD", rgb(0xDAA520));
					colors0.put("GREENYELLOW", rgb(0xADFF2F));
					colors0.put("HONEYDEW", rgb(0xF0FFF0));
					colors0.put("INDIANRED", rgb(0xCD5C5C));
					// colors0.put("INDIGO", rgb(0x4B0082));
					colors0.put("IVORY", rgb(0xFFFFF0));
					colors0.put("KHAKI", rgb(0xF0E68C));
					colors0.put("LAVENDER", rgb(0xE6E6FA));
					colors0.put("LAVENDERBLUSH", rgb(0xFFF0F5));
					colors0.put("LAWNGREEN", rgb(0x7CFC00));
					colors0.put("LEMONCHIFFON", rgb(0xFFFACD));
					// colors0.put("LIGHTBLUE", rgb(0xADD8E6));
					colors0.put("LIGHTCORAL", rgb(0xF08080));
					colors0.put("LIGHTCYAN", rgb(0xE0FFFF));
					colors0.put("LIGHTGOLDENRODYELLOW", rgb(0xFAFAD2));
					// colors0.put("LIGHTGREEN", rgb(0x90EE90));
					// colors0.put("LIGHTGRAY", Color.LIGHT_GRAY);
					colors0.put("LIGHTPINK", rgb(0xFFB6C1));
					colors0.put("LIGHTSALMON", rgb(0xFFA07A));
					colors0.put("LIGHTSEAGREEN", rgb(0x20B2AA));
					colors0.put("LIGHTSKYBLUE", rgb(0x87CEFA));
					colors0.put("LIGHTSLATEGRAY", rgb(0x778899));
					colors0.put("LIGHTSTEELBLUE", rgb(0xB0C4DE));
					// colors0.put("LIGHTYELLOW", rgb(0xFFFFE0));
					colors0.put("LIMEGREEN", rgb(0x32CD32));
					colors0.put("LINEN", rgb(0xFAF0E6));
					// colors0.put("MAGENTA", rgb(0xFF00FF));
					colors0.put("MEDIUMAQUAMARINE", rgb(0x66CDAA));
					colors0.put("MEDIUMBLUE", rgb(0x0000CD));
					colors0.put("MEDIUMORCHID", rgb(0xBA55D3));
					colors0.put("MEDIUMPURPLE", rgb(0x9370DB));
					colors0.put("MEDIUMSEAGREEN", rgb(0x3CB371));
					colors0.put("MEDIUMSLATEBLUE", rgb(0x7B68EE));
					colors0.put("MEDIUMSPRINGGREEN", rgb(0x00FA9A));
					colors0.put("MEDIUMTURQUOISE", rgb(0x48D1CC));
					colors0.put("MEDIUMVIOLETRED", rgb(0xC71585));
					colors0.put("MIDNIGHTBLUE", rgb(0x191970));
					colors0.put("MINTCREAM", rgb(0xF5FFFA));
					colors0.put("MISTYROSE", rgb(0xFFE4E1));
					colors0.put("MOCCASIN", rgb(0xFFE4B5));
					colors0.put("NAVAJOWHITE", rgb(0xFFDEAD));
					colors0.put("OLDLACE", rgb(0xFDF5E6));
					colors0.put("OLIVEDRAB", rgb(0x6B8E23));
					// colors0.put("ORANGE", rgb(0xFFA500));
					colors0.put("ORANGERED", rgb(0xFF4500));
					colors0.put("ORCHID", rgb(0xDA70D6));
					colors0.put("PALEGOLDENROD", rgb(0xEEE8AA));
					colors0.put("PALETURQUOISE", rgb(0xAFEEEE));
					colors0.put("PALEVIOLETRED", rgb(0xDB7093));
					colors0.put("PAPAYAWHIP", rgb(0xFFEFD5));
					colors0.put("PEACHPUFF", rgb(0xFFDAB9));
					colors0.put("PERU", rgb(0xCD853F));
					// colors0.put("PINK", rgb(0xFFC0CB));
					colors0.put("PLUM", rgb(0xDDA0DD));
					colors0.put("POWDERBLUE", rgb(0xB0E0E6));
					colors0.put("ROSYBROWN", rgb(0xBC8F8F));
					colors0.put("ROYALBLUE", rgb(0x4169E1));
					colors0.put("SADDLEBROWN", rgb(0x8B4513));
					colors0.put("SALMON", rgb(0xFA8072));
					colors0.put("SANDYBROWN", rgb(0xF4A460));
					colors0.put("SEAGREEN", rgb(0x2E8B57));
					colors0.put("SEASHELL", rgb(0xFFF5EE));
					colors0.put("SIENNA", rgb(0xA0522D));
					colors0.put("SKYBLUE", rgb(0x87CEEB));
					colors0.put("SLATEBLUE", rgb(0x6A5ACD));
					colors0.put("SLATEGRAY", rgb(0x708090));
					colors0.put("SNOW", rgb(0xFFFAFA));
					colors0.put("SPRINGGREEN", rgb(0x00FF7F));
					colors0.put("STEELBLUE", rgb(0x4682B4));
					colors0.put("TAN", rgb(0xD2B48C));
					colors0.put("THISTLE", rgb(0xD8BFD8));
					colors0.put("TOMATO", rgb(0xFF6347));
					// colors0.put("TURQUOISE", rgb(0x40E0D0));
					// colors0.put("VIOLET", rgb(0xEE82EE));
					colors0.put("WHEAT", rgb(0xF5DEB3));
					colors0.put("WHITESMOKE", rgb(0xF5F5F5));
					colors0.put("YELLOWGREEN", rgb(0x9ACD32));

					colors = colors0;

				}
			}
		}

		return colors;
	}

	private static GColor rgb(int r, int g, int b) {
		return GColor.newColor(r, g, b);
	}

	private static GColor rgb(int rgb) {
		return GColor.newColorRGB(rgb);
	}
}
