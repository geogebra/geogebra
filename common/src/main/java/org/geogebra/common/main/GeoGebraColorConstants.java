package org.geogebra.common.main;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.StringUtil;

/**
 * Color constants for swatches, spreadsheet etc.
 */
public class GeoGebraColorConstants {

	// table colors
	/** background of selected cell */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR = AwtFactory.prototype
			.newColor(214, 224, 245);
	/** background of selected row / column header */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR_HEADER = GColor.LIGHT_GRAY;
	/** background of row / column header */
	public static final GColor TABLE_BACKGROUND_COLOR_HEADER = AwtFactory.prototype
			.newColor(232, 238, 247);
	/** grid color for spreadsheet */
	public static final GColor TABLE_GRID_COLOR = GColor.GRAY;

	private static GColor rgb(int r, int g, int b) {
		return AwtFactory.prototype.newColor(r, g, b);
	};

	private static GColor rgb(int rgb) {
		return AwtFactory.prototype.newColor(rgb);
	};
	// ggb color constants

	/** violet */
	public static final GColor VIOLET = rgb(127, 0, 255);

	/** lime */
	public static final GColor LIME = AwtFactory.prototype
			.newColor(191, 255, 0);
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
	public static final GColor GGB_PURPLE = rgb(153, 51, 255);

	public static final GColor GGB_RED = rgb(204, 0, 0);
	public static final GColor GGB_VIOLET = rgb(244, 0, 153);

	public static final GColor GGB_GRAY = rgb(102, 102, 102);
	public static final GColor GGB_BROWN = rgb(153, 51, 0);
	public static final GColor GGB_ORANGE = rgb(255, 85, 0);
	/**
	 * Light blue: don't change: default Point on path color
	 */
	public static final GColor LIGHTBLUE = rgb(125, 125, 255);
	/** gold */
	public static final GColor GOLD = AwtFactory.prototype
			.newColor(255, 215, 0);
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
	public static final GColor DARKGRAY = GRAY7;
	/** 37.5% gray */
	public static final GColor LIGHTGRAY = GRAY3;
	/** 75% gray */
	public static final GColor SILVER = GRAY6;

	// input coloring
	/** defined objects (violet) */
	public static final GColor DEFINED_OBJECT_COLOR = AwtFactory.prototype
			.newColor(102, 102, 255);
	/** undefined objects */
	public static final GColor UNDEFINED_OBJECT_COLOR = GRAY3;
	/** local objects (local variables, blue) */
	public static final GColor LOCAL_OBJECT_COLOR = AwtFactory.prototype
			.newColor(0, 102, 255);
	/** unbalanced brackets */
	public static final GColor UNBALANCED_BRACKET_COLOR = GColor.RED;
	/** balanced bracket (green) */
	public static final GColor BALANCED_BRACKET_COLOR = AwtFactory.prototype
			.newColor(0, 127, 0);
	/** text mode */
	public static final GColor INPUT_TEXT_COLOR = GRAY4;
	/** default color */
	public static final GColor INPUT_DEFAULT_COLOR = GColor.BLACK;

	/**
	 * HashMap recording RGB color values with named colors key = color name
	 * from colors.properties value = RBG color
	 */
	private static volatile HashMap<String, GColor> geogebraColor = null; // must
																			// be
																			// declared
																			// volatile
	private static final Object lock = new Object();

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
					HashMap<String, GColor> geogebraColor0 = new HashMap<String, GColor>();

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
					geogebraColor0.put("darkgray", DARKGRAY);
					geogebraColor0.put("lightgray", LIGHTGRAY);
					geogebraColor0.put("silver", SILVER);

					geogebraColor = geogebraColor0;
				}
			}
		}

		return geogebraColor;
	}

	/** popup color menu type: standard */
	public static final int COLORSET_STANDARD = 0;
	/** popup color menu type: background */
	public static final int COLORSET_BGCOLOR = 1;

	/**
	 * Reverse lookup for GeoGebraColors key = RBG color value = color name from
	 * colors.properties
	 */
	private static volatile HashMap<GColor, String> geogebraColorReverse = null;

	private static HashMap<GColor, String> getGeoGebraColorReverse() {
		if (geogebraColorReverse == null) { // avoid sync penalty if we can
			synchronized (lock2) { // declare a private static Object to use for
									// mutex
				if (geogebraColorReverse == null) { // have to do this inside
													// the sync

					HashMap<GColor, String> geogebraColorReverse0 = new HashMap<GColor, String>();

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

		GColor ret = geogebraColor.get(StringUtil.toLowerCase(colorName));

		if (ret == null) {
			ret = geogebraColor.get(app.getLocalization().reverseGetColor(
					colorName));
		}
		if (ret == null)
			// will need only English characters
			ret = htmlColorMap().get(colorName.toUpperCase());
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
		return app.getLocalization().getColor(
				getGeoGebraColorReverse().get(color));
	}

	/**
	 * Returns a gray Color where the gray hue is determined by n, an integer
	 * from 1-7. Gray, in RGB values, is given by red = green = blue = (256 -
	 * 32*n) For example: n = 7 gives RGB[32,32,32], v. dark gray n = 6 gives
	 * RGB[64,64,64], dark gray ... n = 1 gives RGB[224,224,224] v.light gray
	 * 
	 * @param n
	 * @return
	 */
	private static GColor grayN(int n) {
		int grayN = 256 - 32 * n;
		return rgb(grayN, grayN, grayN);
	}

	private static volatile GColor[] primaryColors = null;

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

	private static volatile GColor[] lightPrimaryColors = null;

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

	private static volatile GColor[] darkPrimaryColors = null;

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

	private static volatile GColor[] grayColors = null;
	private static final Object lock2 = new Object();

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
	 * @param colorSetType
	 *            COLORSET_* (background or primary)
	 * @return array of colors for color popup menus
	 */
	public static GColor[] getSimplePopupArray(int colorSetType) {

		GColor[] popupColors = new GColor[10];

		popupColors[0] = GGB_GREEN;
		popupColors[1] = GColor.BLUE;
		popupColors[2] = GGB_PURPLE;
		popupColors[3] = GGB_VIOLET;// rgb(244, 0, 153);
		popupColors[4] = GGB_RED;

		popupColors[5] = GColor.BLACK;
		popupColors[6] = GGB_GRAY;// rgb(102, 102, 102);
		popupColors[7] = GGB_BROWN; // rgb(153, 51, 0);
		popupColors[8] = GGB_ORANGE;// rgb(255, 85, 0);
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
	 */
	public static GColor[] mainColorSwatchColors = {

			// Row 1 RED related colors

			PINK,
			rgb(255, 153, 204),
			rgb(255, 102, 153),
			rgb(255, 51, 102),
			rgb(255, 0, 51),
			GGB_RED,
			MAROON,
			rgb(51, 0, 0),

			// Row 2 ORANGE related colors

			LIGHTORANGE,
			rgb(255, 204, 51),
			rgb(255, 153, 0),
			rgb(255, 153, 51),
			rgb(255, 102, 0),
			rgb(204, 102, 0),
			rgb(153, 102, 0),
			rgb(51, 51, 0),

			// Row 3 YELLOW related colors

			LIGHTYELLOW,
			rgb(255, 255, 153),
			rgb(255, 255, 102),
			GOLD,
			rgb(255, 204, 102),
			rgb(204, 153, 0),
			GGB_BROWN,
			rgb(102, 51, 0),

			// Row 4 YELLOW-GREEN ("LIME") related colors

			rgb(204, 255, 204),
			rgb(204, 255, 102),
			rgb(153, 255, 0),
			rgb(153, 204, 0),
			rgb(102, 204, 0),
			rgb(102, 153, 0),
			rgb(51, 153, 0),
			rgb(0, 102, 51),

			// Row 5 GREEN related colors

			LIGHTGREEN,
			rgb(153, 255, 153),
			rgb(102, 255, 0),
			rgb(51, 255, 0),
			rgb(0, 204, 0),
			rgb(0, 153, 0),
			GGB_GREEN,
			rgb(0, 51, 0),

			// Row 6 CYAN related colors

			TURQUOISE,
			rgb(153, 255, 255),
			rgb(51, 255, 204),
			rgb(0, 153, 255),
			rgb(0, 153, 204),
			rgb(0, 102, 153),
			rgb(0, 51, 204),
			rgb(0, 51, 153),

			// Row 7 BLUE related colors

			AQUA, rgb(153, 204, 255), rgb(102, 204, 255),
			rgb(102, 153, 255),
			rgb(125, 125, 255),
			rgb(51, 51, 255),
			rgb(0, 0, 204),
			rgb(0, 0, 51),

			// Row 8 VIOLET related colors

			LIGHTPURPLE, rgb(204, 153, 255), rgb(204, 102, 255),
			rgb(153, 102, 255), rgb(102, 0, 204),
			PURPLE,
			INDIGO,
			rgb(51, 0, 51),

			// Row 9 MAGENTA related colors

			LIGHTVIOLET, rgb(255, 153, 255), rgb(255, 153, 153),
			rgb(255, 51, 204), CRIMSON, rgb(204, 0, 102), rgb(153, 0, 51),
			rgb(102, 0, 153), };

	private static volatile HashMap<String, GColor> colors = null;

	/**
	 * initialize (once) only when needed
	 * 
	 * @return map HTML name => color
	 */
	public static final HashMap<String, GColor> htmlColorMap() {

		if (colors == null) { // avoid sync penalty if we can
			synchronized (lock) { // declare a private static Object to use for
									// mutex
				if (colors == null) { // have to do this inside the sync

					// findbugs DC_PARTIALLY_CONSTRUCTED
					HashMap<String, GColor> colors0 = new HashMap<String, GColor>();

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

					colors0.put("ALICEBLUE", rgb(0xEFF7FF));
					colors0.put("ANTIQUEWHITE", rgb(0xF9E8D2));
					colors0.put("AQUAMARINE", rgb(0x43B7BA));
					colors0.put("AZURE", rgb(0xEFFFFF));
					colors0.put("BEIGE", rgb(0xF5F3D7));
					colors0.put("BISQUE", rgb(0xFDE0BC));
					colors0.put("BLANCHEDALMOND", rgb(0xFEE8C6));
					colors0.put("BLUEVIOLET", rgb(0x7931DF));
					// colors0.put("BROWN", rgb(0x980516));
					colors0.put("BURLYWOOD", rgb(0xEABE83));
					colors0.put("CADETBLUE", rgb(0x578693));
					colors0.put("CHARTREUSE", rgb(0x8AFB17));
					colors0.put("CHOCOLATE", rgb(0xC85A17));
					colors0.put("CORAL", rgb(0xF76541));
					colors0.put("CORNFLOWERBLUE", rgb(0x151B8D));
					colors0.put("CORNSILK", rgb(0xFFF7D7));
					// colors0.put("CRIMSON", rgb(0xE41B17));
					colors0.put("CYAN", rgb(0x00FFFF));
					// colors0.put("DARKBLUE", rgb(0x2F2F4F));
					colors0.put("DARKCYAN", rgb(0x57FEFF));
					colors0.put("DARKGOLDENROD", rgb(0xAF7817));
					// colors0.put("DARKGRAY", rgb(0x7A7777));
					// colors0.put("DARKGREEN", rgb(0x254117));
					colors0.put("DARKKHAKI", rgb(0xB7AD59));
					colors0.put("DARKMAGENTA", rgb(0xF43EFF));
					colors0.put("DARKOLIVEGREEN", rgb(0xCCFB5D));
					colors0.put("DARKORANGE", rgb(0xF88017));
					colors0.put("DARKORCHID", rgb(0x7D1B7E));
					colors0.put("DARKRED", rgb(0xE41B17));
					colors0.put("DARKSALMON", rgb(0xE18B6B));
					colors0.put("DARKSEAGREEN", rgb(0x8BB381));
					colors0.put("DARKSLATEBLUE", rgb(0x2B3856));
					colors0.put("DARKSLATEGRAY", rgb(0x253856));
					colors0.put("DARKTURQUOISE", rgb(0x3B9C9C));
					colors0.put("DARKVIOLET", rgb(0x842DCE));
					colors0.put("DEEPPINK", rgb(0xF52887));
					colors0.put("DEEPSKYBLUE", rgb(0x3BB9FF));
					colors0.put("DIMGRAY", rgb(0x463E41));
					colors0.put("DODGERBLUE", rgb(0x1589FF));
					colors0.put("FIREBRICK", rgb(0x800517));
					colors0.put("FLORALWHITE", rgb(0xFFF9EE));
					colors0.put("FORESTGREEN", rgb(0x4E9258));
					colors0.put("GAINSBORO", rgb(0xD8D9D7));
					colors0.put("GHOSTWHITE", rgb(0xF7F7FF));
					// colors0.put("GOLD", rgb(0xD4A017));
					colors0.put("GOLDENROD", rgb(0xEDDA74));
					colors0.put("GREENYELLOW", rgb(0xB1FB17));
					colors0.put("HONEYDEW", rgb(0xF0FEEE));
					colors0.put("INDIANRED", rgb(0x5E2217));
					// colors0.put("INDIGO", rgb(0x307D7E));
					colors0.put("IVORY", rgb(0xFFFFEE));
					colors0.put("KHAKI", rgb(0xADA96E));
					colors0.put("LAVENDER", rgb(0xE3E4FA));
					colors0.put("LAVENDERBLUSH", rgb(0xFDEEF4));
					colors0.put("LAWNGREEN", rgb(0x87F717));
					colors0.put("LEMONCHIFFON", rgb(0xFFF8C6));
					// colors0.put("LIGHTBLUE", rgb(0xADDFFF));
					colors0.put("LIGHTCORAL", rgb(0xE77471));
					colors0.put("LIGHTCYAN", rgb(0xE0FFFF));
					colors0.put("LIGHTGOLDENRODYELLOW", rgb(0xFAF8CC));
					// colors0.put("LIGHTGREEN", rgb(0xCCFFCC));
					// colors0.put("LIGHTGRAY", Color.LIGHT_GRAY);
					colors0.put("LIGHTPINK", rgb(0xFAAFBA));
					colors0.put("LIGHTSALMON", rgb(0xF9966B));
					colors0.put("LIGHTSEAGREEN", rgb(0x3EA99F));
					colors0.put("LIGHTSKYBLUE", rgb(0x82CAFA));
					colors0.put("LIGHTSLATEGRAY", rgb(0x6D7B8D));
					colors0.put("LIGHTSTEELBLUE", rgb(0x728FCE));
					// colors0.put("LIGHTYELLOW", rgb(0xFFFEDC));
					colors0.put("LIMEGREEN", rgb(0x41A317));
					colors0.put("LINEN", rgb(0xF9EEE2));
					// colors0.put("MAGENTA", rgb(0xFF00FF));
					colors0.put("MEDIUMAQUAMARINE", rgb(0x348781));
					colors0.put("MEDIUMBLUE", rgb(0x152DC6));
					colors0.put("MEDIUMORCHID", rgb(0xB048B5));
					colors0.put("MEDIUMPURPLE", rgb(0x8467D7));
					colors0.put("MEDIUMSEAGREEN", rgb(0x306754));
					colors0.put("MEDIUMSLATEBLUE", rgb(0x5E5A80));
					colors0.put("MEDIUMSPRINGGREEN", rgb(0x348017));
					colors0.put("MEDIUMTURQUOISE", rgb(0x48CCCD));
					colors0.put("MEDIUMVIOLETRED", rgb(0xCA226B));
					colors0.put("MIDNIGHTBLUE", rgb(0x151B54));
					colors0.put("MINTCREAM", rgb(0xF5FFF9));
					colors0.put("MISTYROSE", rgb(0xFDE1DD));
					colors0.put("MOCCASIN", rgb(0xFDE0AC));
					colors0.put("NAVAJOWHITE", rgb(0xFDDAA3));
					colors0.put("OLDLACE", rgb(0xFCF3E2));
					colors0.put("OLIVEDRAB", rgb(0x658017));
					// colors0.put("ORANGE", rgb(0xF87A17));
					colors0.put("ORANGERED", rgb(0xF63817));
					colors0.put("ORCHID", rgb(0xE57DED));
					colors0.put("PALEGOLDENROD", rgb(0xEDE49E));
					colors0.put("PALETURQUOISE", rgb(0xAEEBEC));
					colors0.put("PALEVIOLETRED", rgb(0xD16587));
					colors0.put("PAPAYAWHIP", rgb(0xFEECCF));
					colors0.put("PEACHPUFF", rgb(0xFCD5B0));
					colors0.put("PERU", rgb(0xC57726));
					// colors0.put("PINK", rgb(0xFAAFBE));
					colors0.put("PLUM", rgb(0xB93B8F));
					colors0.put("POWDERBLUE", rgb(0xADDCE3));
					colors0.put("ROSYBROWN", rgb(0xB38481));
					colors0.put("ROYALBLUE", rgb(0x2B60DE));
					colors0.put("SADDLEBROWN", rgb(0xF63526));
					colors0.put("SALMON", rgb(0xF88158));
					colors0.put("SANDYBROWN", rgb(0xEE9A4D));
					colors0.put("SEAGREEN", rgb(0x4E8975));
					colors0.put("SEASHELL", rgb(0xFEF3EB));
					colors0.put("SIENNA", rgb(0x8A4117));
					colors0.put("SKYBLUE", rgb(0x6698FF));
					colors0.put("SLATEBLUE", rgb(0x737CA1));
					colors0.put("SLATEGRAY", rgb(0x657383));
					colors0.put("SNOW", rgb(0xFFF9FA));
					colors0.put("SPRINGGREEN", rgb(0x4AA02C));
					colors0.put("STEELBLUE", rgb(0x4863A0));
					colors0.put("TAN", rgb(0xD8AF79));
					colors0.put("THISTLE", rgb(0xD2B9D3));
					colors0.put("TOMATO", rgb(0xF75431));
					// colors0.put("TURQUOISE", rgb(0x43C6DB));
					// colors0.put("VIOLET", rgb(0x8D38C9));
					colors0.put("WHEAT", rgb(0xF3DAA9));
					colors0.put("WHITESMOKE", rgb(0xFFFFFF));
					colors0.put("YELLOWGREEN", rgb(0x52D017));

					colors = colors0;

				}
			}
		}

		return colors;
	}
}
