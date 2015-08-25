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
	private static HashMap<String, GColor> geogebraColor = null;// new
																// HashMap<String,
																// Color>();

	// static
	/**
	 * @return map of internal lowercase strings to GColors
	 */
	public static HashMap<String, GColor> getGeoGebraColors() {

		if (geogebraColor != null)
			return geogebraColor;

		geogebraColor = new HashMap<String, GColor>();

		// primary
		geogebraColor.put("red", GColor.RED);
		geogebraColor.put("orange", GColor.ORANGE);
		geogebraColor.put("yellow", GColor.YELLOW);
		geogebraColor.put("green", GColor.GREEN);
		geogebraColor.put("cyan", GColor.CYAN);
		geogebraColor.put("blue", GColor.BLUE);
		geogebraColor.put("violet", VIOLET);
		geogebraColor.put("magenta", GColor.MAGENTA);
		geogebraColor.put("lime", LIME);

		// light primary
		geogebraColor.put("pink", PINK);
		geogebraColor.put("lightorange", LIGHTORANGE);
		geogebraColor.put("lightyellow", LIGHTYELLOW);
		geogebraColor.put("aqua", AQUA);
		geogebraColor.put("lightpurple", LIGHTPURPLE);
		geogebraColor.put("lightviolet", LIGHTVIOLET);
		geogebraColor.put("turquoise", TURQUOISE);
		geogebraColor.put("lightgreen", LIGHTGREEN);
		geogebraColor.put("darkblue", DARKBLUE);

		// dark primary
		geogebraColor.put("maroon", MAROON);
		geogebraColor.put("brown", GGB_BROWN);
		geogebraColor.put("gold", GOLD);
		geogebraColor.put("darkgreen", GGB_GREEN);
		geogebraColor.put("lightblue", LIGHTBLUE);
		geogebraColor.put("indigo", INDIGO);
		geogebraColor.put("purple", PURPLE);
		geogebraColor.put("crimson", CRIMSON);

		// white/gray/black
		geogebraColor.put("white", GColor.WHITE);
		geogebraColor.put("black", GColor.BLACK);
		geogebraColor.put("gray7", GRAY7);
		geogebraColor.put("gray6", GRAY6);
		geogebraColor.put("gray5", GRAY5);
		geogebraColor.put("gray", GRAY4);
		geogebraColor.put("gray3", GRAY3);
		geogebraColor.put("gray2", GRAY2);
		geogebraColor.put("gray1", GRAY1);
		geogebraColor.put("darkgray", DARKGRAY);
		geogebraColor.put("lightgray", LIGHTGRAY);
		geogebraColor.put("silver", SILVER);

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
	private static HashMap<GColor, String> geogebraColorReverse = null;// new
																		// HashMap<Color,
																		// String>();

	private static HashMap<GColor, String> getGeoGebraColorReverse() {
		if (geogebraColorReverse != null)
			return geogebraColorReverse;

		geogebraColorReverse = new HashMap<GColor, String>();

		for (Entry<String, GColor> entry : getGeoGebraColors().entrySet())
			geogebraColorReverse.put(entry.getValue(), entry.getKey());

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

	private static GColor[] primaryColors = null;

	private static GColor[] getPrimaryColors() {

		if (primaryColors != null)
			return primaryColors;

		getGeoGebraColors();

		primaryColors = new GColor[9];
		primaryColors[0] = geogebraColor.get("red");
		primaryColors[1] = geogebraColor.get("orange");
		primaryColors[2] = geogebraColor.get("yellow");
		primaryColors[3] = geogebraColor.get("lime");
		primaryColors[4] = geogebraColor.get("green");
		primaryColors[5] = geogebraColor.get("cyan");
		primaryColors[6] = geogebraColor.get("blue");
		primaryColors[7] = geogebraColor.get("violet");
		primaryColors[8] = geogebraColor.get("magenta");

		return primaryColors;
	}

	private static GColor[] lightPrimaryColors = null;

	private static GColor[] getLightPrimaryColors() {

		if (lightPrimaryColors != null)
			return lightPrimaryColors;

		getGeoGebraColors();

		lightPrimaryColors = new GColor[9];
		lightPrimaryColors[0] = null; // for the null icon symbol (for removing
										// bgcolor)
		lightPrimaryColors[1] = geogebraColor.get("pink");
		lightPrimaryColors[2] = geogebraColor.get("lightorange");
		lightPrimaryColors[3] = geogebraColor.get("lightyellow");
		lightPrimaryColors[4] = geogebraColor.get("lightgreen");
		lightPrimaryColors[5] = geogebraColor.get("turquoise");
		lightPrimaryColors[6] = geogebraColor.get("aqua");
		lightPrimaryColors[7] = geogebraColor.get("lightpurple");
		lightPrimaryColors[8] = geogebraColor.get("lightviolet");

		return lightPrimaryColors;
	}

	private static GColor[] darkPrimaryColors = null;

	private static GColor[] getDarkPrimaryColors() {

		if (darkPrimaryColors != null)
			return darkPrimaryColors;

		getGeoGebraColors();

		darkPrimaryColors = new GColor[9];
		darkPrimaryColors[0] = geogebraColor.get("maroon");
		darkPrimaryColors[1] = geogebraColor.get("brown");
		darkPrimaryColors[2] = geogebraColor.get("gold");
		darkPrimaryColors[3] = geogebraColor.get("darkgreen");
		darkPrimaryColors[4] = geogebraColor.get("lightblue");
		darkPrimaryColors[5] = geogebraColor.get("purple");
		darkPrimaryColors[6] = geogebraColor.get("indigo");
		darkPrimaryColors[7] = geogebraColor.get("crimson");
		darkPrimaryColors[8] = geogebraColor.get("pink");

		return darkPrimaryColors;
	}

	private static GColor[] grayColors = null;

	private static GColor[] getGrayColors() {

		if (grayColors != null)
			return grayColors;

		grayColors = new GColor[9];
		grayColors[0] = getGeoGebraColors().get("white");
		grayColors[1] = grayN(1);
		grayColors[2] = grayN(2);
		grayColors[3] = grayN(3);
		grayColors[4] = grayN(4);
		grayColors[5] = grayN(5);
		grayColors[6] = grayN(6);
		grayColors[7] = grayN(7);
		grayColors[8] = getGeoGebraColors().get("black");

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

			AQUA,
 rgb(153, 204, 255), rgb(102, 204, 255),
			rgb(102, 153, 255),
			rgb(125, 125, 255),
			rgb(51, 51, 255),
			rgb(0, 0, 204),
			rgb(0, 0, 51),

			// Row 8 VIOLET related colors

			LIGHTPURPLE,
 rgb(204, 153, 255), rgb(204, 102, 255),
			rgb(153, 102, 255), rgb(102, 0, 204),
			PURPLE,
			INDIGO,
			rgb(51, 0, 51),

			// Row 9 MAGENTA related colors

			LIGHTVIOLET, rgb(255, 153, 255), rgb(255, 153, 153),
			rgb(255, 51, 204), CRIMSON, rgb(204, 0, 102), rgb(153, 0, 51),
			rgb(102, 0, 153), };

	private static HashMap<String, GColor> colors = null;

	/**
	 * @return map HTML name => color
	 */
	public static final HashMap<String, GColor> htmlColorMap() {

		if (colors != null)
			return colors;

		colors = new HashMap<String, GColor>();

		// HTML 3.2
		// colors.put("AQUA", rgb(0x00FFFF));
		// colors.put("BLACK", rgb(0x000000));
		// colors.put("BLUE", rgb(0x0000FF));
		colors.put("FUCHSIA", rgb(0xFF00FF));
		// colors.put("GRAY", rgb(0x808080));
		// colors.put("GREEN", rgb(0x008000));
		// colors.put("LIME", rgb(0x00FF00));
		// colors.put("MAROON", rgb(0x800000));
		colors.put("NAVY", rgb(0x000080));
		colors.put("OLIVE", rgb(0x808000));
		// colors.put("PURPLE", rgb(0x800080));
		// colors.put("RED", rgb(0xFF0000));
		// colors.put("SILVER", rgb(0xC0C0C0));
		colors.put("TEAL", rgb(0x008080));
		// colors.put("WHITE", rgb(0xFFFFFF));
		// colors.put("YELLOW", rgb(0xFFFF00));

		colors.put("ALICEBLUE", rgb(0xEFF7FF));
		colors.put("ANTIQUEWHITE", rgb(0xF9E8D2));
		colors.put("AQUAMARINE", rgb(0x43B7BA));
		colors.put("AZURE", rgb(0xEFFFFF));
		colors.put("BEIGE", rgb(0xF5F3D7));
		colors.put("BISQUE", rgb(0xFDE0BC));
		colors.put("BLANCHEDALMOND", rgb(0xFEE8C6));
		colors.put("BLUEVIOLET", rgb(0x7931DF));
		// colors.put("BROWN", rgb(0x980516));
		colors.put("BURLYWOOD", rgb(0xEABE83));
		colors.put("CADETBLUE", rgb(0x578693));
		colors.put("CHARTREUSE", rgb(0x8AFB17));
		colors.put("CHOCOLATE", rgb(0xC85A17));
		colors.put("CORAL", rgb(0xF76541));
		colors.put("CORNFLOWERBLUE", rgb(0x151B8D));
		colors.put("CORNSILK", rgb(0xFFF7D7));
		// colors.put("CRIMSON", rgb(0xE41B17));
		colors.put("CYAN", rgb(0x00FFFF));
		// colors.put("DARKBLUE", rgb(0x2F2F4F));
		colors.put("DARKCYAN", rgb(0x57FEFF));
		colors.put("DARKGOLDENROD", rgb(0xAF7817));
		// colors.put("DARKGRAY", rgb(0x7A7777));
		// colors.put("DARKGREEN", rgb(0x254117));
		colors.put("DARKKHAKI", rgb(0xB7AD59));
		colors.put("DARKMAGENTA", rgb(0xF43EFF));
		colors.put("DARKOLIVEGREEN", rgb(0xCCFB5D));
		colors.put("DARKORANGE", rgb(0xF88017));
		colors.put("DARKORCHID", rgb(0x7D1B7E));
		colors.put("DARKRED", rgb(0xE41B17));
		colors.put("DARKSALMON", rgb(0xE18B6B));
		colors.put("DARKSEAGREEN", rgb(0x8BB381));
		colors.put("DARKSLATEBLUE", rgb(0x2B3856));
		colors.put("DARKSLATEGRAY", rgb(0x253856));
		colors.put("DARKTURQUOISE", rgb(0x3B9C9C));
		colors.put("DARKVIOLET", rgb(0x842DCE));
		colors.put("DEEPPINK", rgb(0xF52887));
		colors.put("DEEPSKYBLUE", rgb(0x3BB9FF));
		colors.put("DIMGRAY", rgb(0x463E41));
		colors.put("DODGERBLUE", rgb(0x1589FF));
		colors.put("FIREBRICK", rgb(0x800517));
		colors.put("FLORALWHITE", rgb(0xFFF9EE));
		colors.put("FORESTGREEN", rgb(0x4E9258));
		colors.put("GAINSBORO", rgb(0xD8D9D7));
		colors.put("GHOSTWHITE", rgb(0xF7F7FF));
		// colors.put("GOLD", rgb(0xD4A017));
		colors.put("GOLDENROD", rgb(0xEDDA74));
		colors.put("GREENYELLOW", rgb(0xB1FB17));
		colors.put("HONEYDEW", rgb(0xF0FEEE));
		colors.put("INDIANRED", rgb(0x5E2217));
		// colors.put("INDIGO", rgb(0x307D7E));
		colors.put("IVORY", rgb(0xFFFFEE));
		colors.put("KHAKI", rgb(0xADA96E));
		colors.put("LAVENDER", rgb(0xE3E4FA));
		colors.put("LAVENDERBLUSH", rgb(0xFDEEF4));
		colors.put("LAWNGREEN", rgb(0x87F717));
		colors.put("LEMONCHIFFON", rgb(0xFFF8C6));
		// colors.put("LIGHTBLUE", rgb(0xADDFFF));
		colors.put("LIGHTCORAL", rgb(0xE77471));
		colors.put("LIGHTCYAN", rgb(0xE0FFFF));
		colors.put("LIGHTGOLDENRODYELLOW",
 rgb(0xFAF8CC));
		// colors.put("LIGHTGREEN", rgb(0xCCFFCC));
		// colors.put("LIGHTGRAY", Color.LIGHT_GRAY);
		colors.put("LIGHTPINK", rgb(0xFAAFBA));
		colors.put("LIGHTSALMON", rgb(0xF9966B));
		colors.put("LIGHTSEAGREEN", rgb(0x3EA99F));
		colors.put("LIGHTSKYBLUE", rgb(0x82CAFA));
		colors.put("LIGHTSLATEGRAY", rgb(0x6D7B8D));
		colors.put("LIGHTSTEELBLUE", rgb(0x728FCE));
		// colors.put("LIGHTYELLOW", rgb(0xFFFEDC));
		colors.put("LIMEGREEN", rgb(0x41A317));
		colors.put("LINEN", rgb(0xF9EEE2));
		// colors.put("MAGENTA", rgb(0xFF00FF));
		colors.put("MEDIUMAQUAMARINE", rgb(0x348781));
		colors.put("MEDIUMBLUE", rgb(0x152DC6));
		colors.put("MEDIUMORCHID", rgb(0xB048B5));
		colors.put("MEDIUMPURPLE", rgb(0x8467D7));
		colors.put("MEDIUMSEAGREEN", rgb(0x306754));
		colors.put("MEDIUMSLATEBLUE", rgb(0x5E5A80));
		colors.put("MEDIUMSPRINGGREEN", rgb(0x348017));
		colors.put("MEDIUMTURQUOISE", rgb(0x48CCCD));
		colors.put("MEDIUMVIOLETRED", rgb(0xCA226B));
		colors.put("MIDNIGHTBLUE", rgb(0x151B54));
		colors.put("MINTCREAM", rgb(0xF5FFF9));
		colors.put("MISTYROSE", rgb(0xFDE1DD));
		colors.put("MOCCASIN", rgb(0xFDE0AC));
		colors.put("NAVAJOWHITE", rgb(0xFDDAA3));
		colors.put("OLDLACE", rgb(0xFCF3E2));
		colors.put("OLIVEDRAB", rgb(0x658017));
		// colors.put("ORANGE", rgb(0xF87A17));
		colors.put("ORANGERED", rgb(0xF63817));
		colors.put("ORCHID", rgb(0xE57DED));
		colors.put("PALEGOLDENROD", rgb(0xEDE49E));
		colors.put("PALETURQUOISE", rgb(0xAEEBEC));
		colors.put("PALEVIOLETRED", rgb(0xD16587));
		colors.put("PAPAYAWHIP", rgb(0xFEECCF));
		colors.put("PEACHPUFF", rgb(0xFCD5B0));
		colors.put("PERU", rgb(0xC57726));
		// colors.put("PINK", rgb(0xFAAFBE));
		colors.put("PLUM", rgb(0xB93B8F));
		colors.put("POWDERBLUE", rgb(0xADDCE3));
		colors.put("ROSYBROWN", rgb(0xB38481));
		colors.put("ROYALBLUE", rgb(0x2B60DE));
		colors.put("SADDLEBROWN", rgb(0xF63526));
		colors.put("SALMON", rgb(0xF88158));
		colors.put("SANDYBROWN", rgb(0xEE9A4D));
		colors.put("SEAGREEN", rgb(0x4E8975));
		colors.put("SEASHELL", rgb(0xFEF3EB));
		colors.put("SIENNA", rgb(0x8A4117));
		colors.put("SKYBLUE", rgb(0x6698FF));
		colors.put("SLATEBLUE", rgb(0x737CA1));
		colors.put("SLATEGRAY", rgb(0x657383));
		colors.put("SNOW", rgb(0xFFF9FA));
		colors.put("SPRINGGREEN", rgb(0x4AA02C));
		colors.put("STEELBLUE", rgb(0x4863A0));
		colors.put("TAN", rgb(0xD8AF79));
		colors.put("THISTLE", rgb(0xD2B9D3));
		colors.put("TOMATO", rgb(0xF75431));
		// colors.put("TURQUOISE", rgb(0x43C6DB));
		// colors.put("VIOLET", rgb(0x8D38C9));
		colors.put("WHEAT", rgb(0xF3DAA9));
		colors.put("WHITESMOKE", rgb(0xFFFFFF));
		colors.put("YELLOWGREEN", rgb(0x52D017));

		return colors;
	}
}
