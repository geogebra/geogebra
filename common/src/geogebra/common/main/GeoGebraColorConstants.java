package geogebra.common.main;

import geogebra.common.awt.GColor;
import geogebra.common.factories.AwtFactory;
import geogebra.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Color constants for swatches, spreadsheet etc. 
 */
public class GeoGebraColorConstants {

	// table colors
	/** background of selected cell */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR = AwtFactory.prototype
			.newColor(214, 224, 245);
	/** background of selected row / column header */
	public static final GColor TABLE_SELECTED_BACKGROUND_COLOR_HEADER = GColor.lightGray;
	/** background of row / column header */
	public static final GColor TABLE_BACKGROUND_COLOR_HEADER = AwtFactory.prototype
			.newColor(232, 238, 247);
	/** grid color for spreadsheet */
	public static final GColor TABLE_GRID_COLOR = GColor.gray;

	// ggb color constants
	/** red */
	public static final GColor RED = AwtFactory.prototype.newColor(255, 0, 0);
	/** orange */
	public static final GColor ORANGE = AwtFactory.prototype.newColor(255, 127,
			0);
	/** yellow */
	public static final GColor YELLOW = AwtFactory.prototype.newColor(255, 255,
			0);
	/** green */
	public static final GColor GREEN = AwtFactory.prototype.newColor(0, 255, 0);
	/** cyan */
	public static final GColor CYAN = AwtFactory.prototype.newColor(0, 255, 255);
	/** blue */
	public static final GColor BLUE = AwtFactory.prototype.newColor(0, 0, 255);
	/** violet */
	public static final GColor VIOLET = AwtFactory.prototype.newColor(127, 0,
			255);
	/** magenta */
	public static final GColor MAGENTA = AwtFactory.prototype.newColor(255, 0,
			255);
	/** lime */
	public static final GColor LIME = AwtFactory.prototype.newColor(191, 255, 0);
	/** pink */
	public static final GColor PINK = AwtFactory.prototype.newColor(255, 192,
			203);
	/** light orange (wikipedia papaya whip)*/
	public static final GColor LIGHTORANGE = AwtFactory.prototype.newColor(255,
			239, 213); 
	/** light yellow (wikipedia lemon chiffon)*/
	public static final GColor LIGHTYELLOW = AwtFactory.prototype.newColor(255,
			250, 205);
	/** aqua (wikipedia pale aqua)*/
	public static final GColor AQUA = AwtFactory.prototype.newColor(188, 212,
			230); 
	/** light purple (wikipedia periwinkle)*/
	public static final GColor LIGHTPURPLE = AwtFactory.prototype.newColor(204,
			204, 255); 
	/** light violet (wikipedia mauve)*/
	public static final GColor LIGHTVIOLET = AwtFactory.prototype.newColor(224,
			176, 255); 
	/** turqoise (wikipedia pale turquoise)*/
	public static final GColor TURQUOISE = AwtFactory.prototype.newColor(175,
			238, 238); 
	/** light green (wikipedia tea green) */
	public static final GColor LIGHTGREEN = AwtFactory.prototype.newColor(208,
			240, 192);
	/** maroon */
	public static final GColor MAROON = AwtFactory.prototype.newColor(128, 0, 0);

	/**
	 * Brown: don't change: default Polygon color
	 */
	public static final GColor BROWN = AwtFactory.prototype.newColor(153, 51, 0);
	/**
	 * Dark green: don't change: default Angle color
	 */
	public static final GColor DARKGREEN = AwtFactory.prototype.newColor(0, 100,
			0);
	/**
	 * Light blue: don't change: default Point on path color
	 */
	public static final GColor LIGHTBLUE = AwtFactory.prototype.newColor(125,
			125, 255);
	/** gold */
	public static final GColor GOLD = AwtFactory.prototype.newColor(255, 215, 0);
	/** dark blue (wikipedia persian blue)*/
	public static final GColor DARKBLUE = AwtFactory.prototype.newColor(28, 57,
			187); 
	/** indigo */
	public static final GColor INDIGO = AwtFactory.prototype
			.newColor(75, 0, 130);
	/** purple */
	public static final GColor PURPLE = AwtFactory.prototype.newColor(128, 0,
			128);
	/** crimson */
	public static final GColor CRIMSON = AwtFactory.prototype.newColor(220, 20,
			60);
	/** white */
	public static final GColor WHITE = AwtFactory.prototype.newColor(255, 255,
			255);
	/** black */
	public static final GColor BLACK = AwtFactory.prototype.newColor(0, 0, 0);
	/** 87.5% gray */
	public static final GColor GRAY7 = grayN(7);
	/** 75% gray (silver)*/
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
		geogebraColor.put("red", RED);
		geogebraColor.put("orange", ORANGE);
		geogebraColor.put("yellow", YELLOW);
		geogebraColor.put("green", GREEN);
		geogebraColor.put("cyan", CYAN);
		geogebraColor.put("blue", BLUE);
		geogebraColor.put("violet", VIOLET);
		geogebraColor.put("magenta", MAGENTA);
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
		geogebraColor.put("brown", BROWN);
		geogebraColor.put("gold", GOLD);
		geogebraColor.put("darkgreen", DARKGREEN);
		geogebraColor.put("lightblue", LIGHTBLUE);
		geogebraColor.put("indigo", INDIGO);
		geogebraColor.put("purple", PURPLE);
		geogebraColor.put("crimson", CRIMSON);

		// white/gray/black
		geogebraColor.put("white", WHITE);
		geogebraColor.put("black", BLACK);
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
	public static GColor getGeogebraColor(App app,
			String colorName) {

		getGeoGebraColors();

		GColor ret = geogebraColor.get(StringUtil.toLowerCase(colorName));

		if (ret == null) {
			ret = geogebraColor.get(app.reverseGetColor(colorName));
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
	public static String getGeogebraColorName(App app,
			GColor color) {
		return app.getColor(getGeoGebraColorReverse().get(color));
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
		return AwtFactory.prototype.newColor(grayN, grayN, grayN);
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
			AwtFactory.prototype.newColor(255, 153, 204),
			AwtFactory.prototype.newColor(255, 102, 153),
			AwtFactory.prototype.newColor(255, 51, 102),
			AwtFactory.prototype.newColor(255, 0, 51),
			AwtFactory.prototype.newColor(204, 0, 0),
			MAROON,
			AwtFactory.prototype.newColor(51, 0, 0),

			// Row 2 ORANGE related colors

			LIGHTORANGE,
			AwtFactory.prototype.newColor(255, 204, 51),
			AwtFactory.prototype.newColor(255, 153, 0),
			AwtFactory.prototype.newColor(255, 153, 51),
			AwtFactory.prototype.newColor(255, 102, 0),
			AwtFactory.prototype.newColor(204, 102, 0),
			AwtFactory.prototype.newColor(153, 102, 0),
			AwtFactory.prototype.newColor(51, 51, 0),

			// Row 3 YELLOW related colors

			LIGHTYELLOW,
			AwtFactory.prototype.newColor(255, 255, 153),
			AwtFactory.prototype.newColor(255, 255, 102),
			GOLD,
			AwtFactory.prototype.newColor(255, 204, 102),
			AwtFactory.prototype.newColor(204, 153, 0),
			BROWN,
			AwtFactory.prototype.newColor(102, 51, 0),

			// Row 4 YELLOW-GREEN ("LIME") related colors

			AwtFactory.prototype.newColor(204, 255, 204),
			AwtFactory.prototype.newColor(204, 255, 102),
			AwtFactory.prototype.newColor(153, 255, 0),
			AwtFactory.prototype.newColor(153, 204, 0),
			AwtFactory.prototype.newColor(102, 204, 0),
			AwtFactory.prototype.newColor(102, 153, 0),
			AwtFactory.prototype.newColor(51, 153, 0),
			AwtFactory.prototype.newColor(0, 102, 51),

			// Row 5 GREEN related colors

			LIGHTGREEN,
			AwtFactory.prototype.newColor(153, 255, 153),
			AwtFactory.prototype.newColor(102, 255, 0),
			AwtFactory.prototype.newColor(51, 255, 0),
			AwtFactory.prototype.newColor(0, 204, 0),
			AwtFactory.prototype.newColor(0, 153, 0),
			DARKGREEN,
			AwtFactory.prototype.newColor(0, 51, 0),

			// Row 6 CYAN related colors

			TURQUOISE,
			AwtFactory.prototype.newColor(153, 255, 255),
			AwtFactory.prototype.newColor(51, 255, 204),
			AwtFactory.prototype.newColor(0, 153, 255),
			AwtFactory.prototype.newColor(0, 153, 204),
			AwtFactory.prototype.newColor(0, 102, 153),
			AwtFactory.prototype.newColor(0, 51, 204),
			AwtFactory.prototype.newColor(0, 51, 153),

			// Row 7 BLUE related colors

			AQUA,
			AwtFactory.prototype.newColor(153, 204, 255),
			AwtFactory.prototype.newColor(102, 204, 255),
			AwtFactory.prototype.newColor(102, 153, 255),
			AwtFactory.prototype.newColor(125, 125, 255),
			AwtFactory.prototype.newColor(51, 51, 255),
			AwtFactory.prototype.newColor(0, 0, 204),
			AwtFactory.prototype.newColor(0, 0, 51),

			// Row 8 VIOLET related colors

			LIGHTPURPLE,
			AwtFactory.prototype.newColor(204, 153, 255),
			AwtFactory.prototype.newColor(204, 102, 255),
			AwtFactory.prototype.newColor(153, 102, 255),
			AwtFactory.prototype.newColor(102, 0, 204),
			PURPLE,
			INDIGO,
			AwtFactory.prototype.newColor(51, 0, 51),

			// Row 9 MAGENTA related colors

			LIGHTVIOLET, AwtFactory.prototype.newColor(255, 153, 255),
			AwtFactory.prototype.newColor(255, 153, 153),
			AwtFactory.prototype.newColor(255, 51, 204), CRIMSON,
			AwtFactory.prototype.newColor(204, 0, 102),
			AwtFactory.prototype.newColor(153, 0, 51),
			AwtFactory.prototype.newColor(102, 0, 153), };

	private static HashMap<String, GColor> colors = null;

	/**
	 * @return map HTML name => color
	 */
	public static final HashMap<String, GColor> htmlColorMap() {

		if (colors != null)
			return colors;

		colors = new HashMap<String, GColor>();

		// HTML 3.2
		// colors.put("AQUA", AwtFactory.prototype.newColor(0x00FFFF));
		// colors.put("BLACK", AwtFactory.prototype.newColor(0x000000));
		// colors.put("BLUE", AwtFactory.prototype.newColor(0x0000FF));
		colors.put("FUCHSIA", AwtFactory.prototype.newColor(0xFF00FF));
		// colors.put("GRAY", AwtFactory.prototype.newColor(0x808080));
		// colors.put("GREEN", AwtFactory.prototype.newColor(0x008000));
		// colors.put("LIME", AwtFactory.prototype.newColor(0x00FF00));
		// colors.put("MAROON", AwtFactory.prototype.newColor(0x800000));
		colors.put("NAVY", AwtFactory.prototype.newColor(0x000080));
		colors.put("OLIVE", AwtFactory.prototype.newColor(0x808000));
		// colors.put("PURPLE", AwtFactory.prototype.newColor(0x800080));
		// colors.put("RED", AwtFactory.prototype.newColor(0xFF0000));
		// colors.put("SILVER", AwtFactory.prototype.newColor(0xC0C0C0));
		colors.put("TEAL", AwtFactory.prototype.newColor(0x008080));
		// colors.put("WHITE", AwtFactory.prototype.newColor(0xFFFFFF));
		// colors.put("YELLOW", AwtFactory.prototype.newColor(0xFFFF00));

		colors.put("ALICEBLUE", AwtFactory.prototype.newColor(0xEFF7FF));
		colors.put("ANTIQUEWHITE", AwtFactory.prototype.newColor(0xF9E8D2));
		colors.put("AQUAMARINE", AwtFactory.prototype.newColor(0x43B7BA));
		colors.put("AZURE", AwtFactory.prototype.newColor(0xEFFFFF));
		colors.put("BEIGE", AwtFactory.prototype.newColor(0xF5F3D7));
		colors.put("BISQUE", AwtFactory.prototype.newColor(0xFDE0BC));
		colors.put("BLANCHEDALMOND", AwtFactory.prototype.newColor(0xFEE8C6));
		colors.put("BLUEVIOLET", AwtFactory.prototype.newColor(0x7931DF));
		// colors.put("BROWN", AwtFactory.prototype.newColor(0x980516));
		colors.put("BURLYWOOD", AwtFactory.prototype.newColor(0xEABE83));
		colors.put("CADETBLUE", AwtFactory.prototype.newColor(0x578693));
		colors.put("CHARTREUSE", AwtFactory.prototype.newColor(0x8AFB17));
		colors.put("CHOCOLATE", AwtFactory.prototype.newColor(0xC85A17));
		colors.put("CORAL", AwtFactory.prototype.newColor(0xF76541));
		colors.put("CORNFLOWERBLUE", AwtFactory.prototype.newColor(0x151B8D));
		colors.put("CORNSILK", AwtFactory.prototype.newColor(0xFFF7D7));
		// colors.put("CRIMSON", AwtFactory.prototype.newColor(0xE41B17));
		colors.put("CYAN", AwtFactory.prototype.newColor(0x00FFFF));
		// colors.put("DARKBLUE", AwtFactory.prototype.newColor(0x2F2F4F));
		colors.put("DARKCYAN", AwtFactory.prototype.newColor(0x57FEFF));
		colors.put("DARKGOLDENROD", AwtFactory.prototype.newColor(0xAF7817));
		// colors.put("DARKGRAY", AwtFactory.prototype.newColor(0x7A7777));
		// colors.put("DARKGREEN", AwtFactory.prototype.newColor(0x254117));
		colors.put("DARKKHAKI", AwtFactory.prototype.newColor(0xB7AD59));
		colors.put("DARKMAGENTA", AwtFactory.prototype.newColor(0xF43EFF));
		colors.put("DARKOLIVEGREEN", AwtFactory.prototype.newColor(0xCCFB5D));
		colors.put("DARKORANGE", AwtFactory.prototype.newColor(0xF88017));
		colors.put("DARKORCHID", AwtFactory.prototype.newColor(0x7D1B7E));
		colors.put("DARKRED", AwtFactory.prototype.newColor(0xE41B17));
		colors.put("DARKSALMON", AwtFactory.prototype.newColor(0xE18B6B));
		colors.put("DARKSEAGREEN", AwtFactory.prototype.newColor(0x8BB381));
		colors.put("DARKSLATEBLUE", AwtFactory.prototype.newColor(0x2B3856));
		colors.put("DARKSLATEGRAY", AwtFactory.prototype.newColor(0x253856));
		colors.put("DARKTURQUOISE", AwtFactory.prototype.newColor(0x3B9C9C));
		colors.put("DARKVIOLET", AwtFactory.prototype.newColor(0x842DCE));
		colors.put("DEEPPINK", AwtFactory.prototype.newColor(0xF52887));
		colors.put("DEEPSKYBLUE", AwtFactory.prototype.newColor(0x3BB9FF));
		colors.put("DIMGRAY", AwtFactory.prototype.newColor(0x463E41));
		colors.put("DODGERBLUE", AwtFactory.prototype.newColor(0x1589FF));
		colors.put("FIREBRICK", AwtFactory.prototype.newColor(0x800517));
		colors.put("FLORALWHITE", AwtFactory.prototype.newColor(0xFFF9EE));
		colors.put("FORESTGREEN", AwtFactory.prototype.newColor(0x4E9258));
		colors.put("GAINSBORO", AwtFactory.prototype.newColor(0xD8D9D7));
		colors.put("GHOSTWHITE", AwtFactory.prototype.newColor(0xF7F7FF));
		// colors.put("GOLD", AwtFactory.prototype.newColor(0xD4A017));
		colors.put("GOLDENROD", AwtFactory.prototype.newColor(0xEDDA74));
		colors.put("GREENYELLOW", AwtFactory.prototype.newColor(0xB1FB17));
		colors.put("HONEYDEW", AwtFactory.prototype.newColor(0xF0FEEE));
		colors.put("INDIANRED", AwtFactory.prototype.newColor(0x5E2217));
		// colors.put("INDIGO", AwtFactory.prototype.newColor(0x307D7E));
		colors.put("IVORY", AwtFactory.prototype.newColor(0xFFFFEE));
		colors.put("KHAKI", AwtFactory.prototype.newColor(0xADA96E));
		colors.put("LAVENDER", AwtFactory.prototype.newColor(0xE3E4FA));
		colors.put("LAVENDERBLUSH", AwtFactory.prototype.newColor(0xFDEEF4));
		colors.put("LAWNGREEN", AwtFactory.prototype.newColor(0x87F717));
		colors.put("LEMONCHIFFON", AwtFactory.prototype.newColor(0xFFF8C6));
		// colors.put("LIGHTBLUE", AwtFactory.prototype.newColor(0xADDFFF));
		colors.put("LIGHTCORAL", AwtFactory.prototype.newColor(0xE77471));
		colors.put("LIGHTCYAN", AwtFactory.prototype.newColor(0xE0FFFF));
		colors.put("LIGHTGOLDENRODYELLOW",
				AwtFactory.prototype.newColor(0xFAF8CC));
		// colors.put("LIGHTGREEN", AwtFactory.prototype.newColor(0xCCFFCC));
		// colors.put("LIGHTGRAY", Color.LIGHT_GRAY);
		colors.put("LIGHTPINK", AwtFactory.prototype.newColor(0xFAAFBA));
		colors.put("LIGHTSALMON", AwtFactory.prototype.newColor(0xF9966B));
		colors.put("LIGHTSEAGREEN", AwtFactory.prototype.newColor(0x3EA99F));
		colors.put("LIGHTSKYBLUE", AwtFactory.prototype.newColor(0x82CAFA));
		colors.put("LIGHTSLATEGRAY", AwtFactory.prototype.newColor(0x6D7B8D));
		colors.put("LIGHTSTEELBLUE", AwtFactory.prototype.newColor(0x728FCE));
		// colors.put("LIGHTYELLOW", AwtFactory.prototype.newColor(0xFFFEDC));
		colors.put("LIMEGREEN", AwtFactory.prototype.newColor(0x41A317));
		colors.put("LINEN", AwtFactory.prototype.newColor(0xF9EEE2));
		// colors.put("MAGENTA", AwtFactory.prototype.newColor(0xFF00FF));
		colors.put("MEDIUMAQUAMARINE", AwtFactory.prototype.newColor(0x348781));
		colors.put("MEDIUMBLUE", AwtFactory.prototype.newColor(0x152DC6));
		colors.put("MEDIUMORCHID", AwtFactory.prototype.newColor(0xB048B5));
		colors.put("MEDIUMPURPLE", AwtFactory.prototype.newColor(0x8467D7));
		colors.put("MEDIUMSEAGREEN", AwtFactory.prototype.newColor(0x306754));
		colors.put("MEDIUMSLATEBLUE", AwtFactory.prototype.newColor(0x5E5A80));
		colors.put("MEDIUMSPRINGGREEN", AwtFactory.prototype.newColor(0x348017));
		colors.put("MEDIUMTURQUOISE", AwtFactory.prototype.newColor(0x48CCCD));
		colors.put("MEDIUMVIOLETRED", AwtFactory.prototype.newColor(0xCA226B));
		colors.put("MIDNIGHTBLUE", AwtFactory.prototype.newColor(0x151B54));
		colors.put("MINTCREAM", AwtFactory.prototype.newColor(0xF5FFF9));
		colors.put("MISTYROSE", AwtFactory.prototype.newColor(0xFDE1DD));
		colors.put("MOCCASIN", AwtFactory.prototype.newColor(0xFDE0AC));
		colors.put("NAVAJOWHITE", AwtFactory.prototype.newColor(0xFDDAA3));
		colors.put("OLDLACE", AwtFactory.prototype.newColor(0xFCF3E2));
		colors.put("OLIVEDRAB", AwtFactory.prototype.newColor(0x658017));
		// colors.put("ORANGE", AwtFactory.prototype.newColor(0xF87A17));
		colors.put("ORANGERED", AwtFactory.prototype.newColor(0xF63817));
		colors.put("ORCHID", AwtFactory.prototype.newColor(0xE57DED));
		colors.put("PALEGOLDENROD", AwtFactory.prototype.newColor(0xEDE49E));
		colors.put("PALETURQUOISE", AwtFactory.prototype.newColor(0xAEEBEC));
		colors.put("PALEVIOLETRED", AwtFactory.prototype.newColor(0xD16587));
		colors.put("PAPAYAWHIP", AwtFactory.prototype.newColor(0xFEECCF));
		colors.put("PEACHPUFF", AwtFactory.prototype.newColor(0xFCD5B0));
		colors.put("PERU", AwtFactory.prototype.newColor(0xC57726));
		// colors.put("PINK", AwtFactory.prototype.newColor(0xFAAFBE));
		colors.put("PLUM", AwtFactory.prototype.newColor(0xB93B8F));
		colors.put("POWDERBLUE", AwtFactory.prototype.newColor(0xADDCE3));
		colors.put("ROSYBROWN", AwtFactory.prototype.newColor(0xB38481));
		colors.put("ROYALBLUE", AwtFactory.prototype.newColor(0x2B60DE));
		colors.put("SADDLEBROWN", AwtFactory.prototype.newColor(0xF63526));
		colors.put("SALMON", AwtFactory.prototype.newColor(0xF88158));
		colors.put("SANDYBROWN", AwtFactory.prototype.newColor(0xEE9A4D));
		colors.put("SEAGREEN", AwtFactory.prototype.newColor(0x4E8975));
		colors.put("SEASHELL", AwtFactory.prototype.newColor(0xFEF3EB));
		colors.put("SIENNA", AwtFactory.prototype.newColor(0x8A4117));
		colors.put("SKYBLUE", AwtFactory.prototype.newColor(0x6698FF));
		colors.put("SLATEBLUE", AwtFactory.prototype.newColor(0x737CA1));
		colors.put("SLATEGRAY", AwtFactory.prototype.newColor(0x657383));
		colors.put("SNOW", AwtFactory.prototype.newColor(0xFFF9FA));
		colors.put("SPRINGGREEN", AwtFactory.prototype.newColor(0x4AA02C));
		colors.put("STEELBLUE", AwtFactory.prototype.newColor(0x4863A0));
		colors.put("TAN", AwtFactory.prototype.newColor(0xD8AF79));
		colors.put("THISTLE", AwtFactory.prototype.newColor(0xD2B9D3));
		colors.put("TOMATO", AwtFactory.prototype.newColor(0xF75431));
		// colors.put("TURQUOISE", AwtFactory.prototype.newColor(0x43C6DB));
		// colors.put("VIOLET", AwtFactory.prototype.newColor(0x8D38C9));
		colors.put("WHEAT", AwtFactory.prototype.newColor(0xF3DAA9));
		colors.put("WHITESMOKE", AwtFactory.prototype.newColor(0xFFFFFF));
		colors.put("YELLOWGREEN", AwtFactory.prototype.newColor(0x52D017));

		return colors;
	}
}
