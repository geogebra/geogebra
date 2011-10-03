package geogebra.main;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;



public class GeoGebraColorConstants {

	// table colors
	public static final Color TABLE_SELECTED_BACKGROUND_COLOR = new Color(214, 224, 245);
	public static final Color TABLE_SELECTED_BACKGROUND_COLOR_HEADER = Color.lightGray;
	public static final Color TABLE_BACKGROUND_COLOR_HEADER = new Color(232, 238, 247);
	public static final Color TABLE_GRID_COLOR = Color.gray;
	
	
	// ggb color constants
	 public static final Color RED = new  Color(255,0,0);
	 public static final Color ORANGE = new  Color(255,127,0);
	 public static final Color YELLOW = new  Color(255,255,0);
	 public static final Color GREEN = new  Color(0,255,0);
	 public static final Color CYAN = new  Color(0,255,255);
	 public static final Color BLUE = new  Color(0,0,255);
	 public static final Color VIOLET = new  Color(127,0,255);
	 public static final Color MAGENTA = new  Color(255,0,255);
	 
	 public static final Color LIME = new  Color(191,255,0);
	 public static final Color PINK = new  Color(255,192,203);
	 public static final Color LIGHTORANGE = new  Color(255, 239, 213); // wikipedia papaya whip 
	 public static final Color LIGHTYELLOW = new  Color(255, 250, 205); // wikipedia lemon chiffon 
	 public static final Color AQUA = new  Color(188, 212, 230); // wikipedia pale aqua 
	 public static final Color LIGHTPURPLE = new  Color(204, 204, 255); // wikipedia periwinkle 
	 public static final Color LIGHTVIOLET = new  Color(224, 176, 255); // wikipedia mauve 
	 public static final Color TURQUOISE = new  Color(175, 238, 238); // wikipedia pale turquoise 
	 public static final Color LIGHTGREEN = new  Color(208, 240, 192); // wikipedia tea green  

	 public static final Color MAROON = new  Color(128, 0, 0); 
	 
	 // don't change: default Polygon color
	 public static final Color BROWN = new Color(153, 51, 0);	
	 // don't change: default Angle color
	 public static final Color DARKGREEN = new  Color(0, 100, 0); 
	 // don't change: default Point on Path color
	 public static final Color LIGHTBLUE = new Color(125, 125, 255);
	 
	 
	 public static final Color GOLD = new  Color(255, 215, 0); 
	 public static final Color DARKBLUE = new  Color(28, 57, 187); // wikipedia persian blue 
	 public static final Color INDIGO = new  Color(75,0,130);
	 public static final Color PURPLE = new  Color(128,0,128);
	 public static final Color CRIMSON = new  Color(220,20,60);
	 
	 public static final Color WHITE = new  Color(255,255,255);
	 public static final Color BLACK = new  Color(0,0,0);
	 public static final Color GRAY7 = grayN(7);
	 public static final Color GRAY6 = grayN(6); // silver
	 public static final Color GRAY5 = grayN(5);
	 public static final Color GRAY4 = grayN(4);
	 public static final Color GRAY3 = grayN(3);
	 public static final Color GRAY2 = grayN(2);
	 public static final Color GRAY1 = grayN(1);
	 public static final Color DARKGRAY = GRAY7;
	 public static final Color LIGHTGRAY = GRAY3;
	 public static final Color SILVER = GRAY6;
	 
	 
	/**
	 * HashMap recording RGB color values with named colors
	 * key = color name from colors.properties
	 * value = RBG color 
	 */
	private static HashMap<String, Color> geogebraColor = null;//new HashMap<String, Color>();
	//static
	private static HashMap<String, Color> getGeoGebraColors()
	{
		
		if (geogebraColor != null) return geogebraColor;
		
		geogebraColor = new HashMap<String, Color>();
		
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
		geogebraColor.put("gold",  GOLD);   
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

	

	

	
	// popup color menu type constants
	public static final int COLORSET_STANDARD = 0;
	public static final int COLORSET_BGCOLOR = 1;

	
	
	/**
	 * Reverse lookup for GeoGebraColors
	 * key = RBG color 
	 * value = color name from colors.properties
	 */
	private static HashMap<Color, String> geogebraColorReverse = null;//new HashMap<Color, String>();
	
	private static HashMap<Color, String> getGeoGebraColorReverse() 
	{	
	if (geogebraColorReverse != null) return geogebraColorReverse;
	
	geogebraColorReverse = new HashMap<Color, String>();
	
	for (Entry<String, Color> entry : getGeoGebraColors().entrySet())	
		geogebraColorReverse.put(entry.getValue(), entry.getKey());
	
	return geogebraColorReverse;
	}

	
	/**
	 * Returns Color object corresponding with given GeoGebra color name string 
	 * @param app
	 * @param colorName
	 * @return Color object corresponding with given GeoGebra color name string
	 */
	public static Color getGeogebraColor(Application app, String colorName){
		Color ret = geogebraColor.get(colorName.toLowerCase(Locale.US));
		
		if (ret == null){			
			ret = geogebraColor.get(app.reverseGetColor(colorName));
		}
		if (ret == null)
			ret = htmlColorMap().get(colorName.toUpperCase(Locale.US));
		return ret;
	}

	/**
	 * Returns GeoGebra color name string corresponding with given Color object 
	 * @param app
	 * @param color
	 * @return GeoGebra color name string corresponding with given Color object
	 */
	public static String getGeogebraColorName(Application app, Color color){
		return app.getColor(getGeoGebraColorReverse().get(color));
	}

	/**
	 * Returns a gray Color where the gray hue is determined by n, an integer from 1-7. 
	 * Gray, in RGB values, is given by red = green = blue = (256 - 32*n)
	 * For example: 
	 * n = 7 gives RGB[32,32,32], v. dark gray 
	 * n = 6 gives RGB[64,64,64], dark gray 
	 * ... 
	 * n = 1 gives RGB[224,224,224] v.light gray
	 * 
	 * @param n
	 * @return
	 */
	private static Color grayN(int n){
		int grayN = 256 - 32 * n;
		return new Color(grayN, grayN, grayN);
	}



	private static Color[] primaryColors = null;
	private static Color[] getPrimaryColors() {
		
		if (primaryColors != null) return primaryColors;
		
		
		getGeoGebraColors();
		
		primaryColors = new Color[9];
		primaryColors[0] = geogebraColor.get("red");
		primaryColors[1] = geogebraColor.get("orange");
		primaryColors[2] = geogebraColor.get("yellow");
		primaryColors[3] = geogebraColor.get("green");
		primaryColors[4] = geogebraColor.get("cyan");
		primaryColors[5] = geogebraColor.get("blue");
		primaryColors[6] = geogebraColor.get("violet");
		primaryColors[7] = geogebraColor.get("magenta");
		primaryColors[8] = geogebraColor.get("lime");
		
		return primaryColors;
	}


	private static Color[] lightPrimaryColors = null;
	private static Color[] getLightPrimaryColors() {
		
		if (lightPrimaryColors != null) return lightPrimaryColors;
		
		getGeoGebraColors();

		lightPrimaryColors = new Color[9];
		lightPrimaryColors[0] = null;  // for the null icon symbol (for removing bgcolor)
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


	private static Color[] darkPrimaryColors = null;
	private static Color[] getDarkPrimaryColors() {
		
		if (darkPrimaryColors != null) return darkPrimaryColors;
		
		getGeoGebraColors();

		darkPrimaryColors = new Color[9];
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


	private static Color[] grayColors = null;
	private static Color[] getGrayColors() {
		
		if (grayColors != null) return grayColors;
		
		grayColors = new Color[9];
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
	 * Returns array of colors for color popup menus
	 * @param colorSetType
	 * @return array of colors for color popup menus
	 */
	public static Color[] getPopupArray(int colorSetType) {

		Color[] colors = new Color[27];		

		for(int i = 0; i< 9; i++){
			if(colorSetType == COLORSET_STANDARD){
				// first row
				colors[i] = getPrimaryColors()[i];
				// second row
				colors[i+9] = getDarkPrimaryColors()[i];
				// third row
				colors[i+18] = getGrayColors()[i];	
			}
			
			if(colorSetType == COLORSET_BGCOLOR){
				// first row
				colors[i] = getLightPrimaryColors()[i];
				// second row
				colors[i+9] = getPrimaryColors()[i];
				// third row
				colors[i+18] = getGrayColors()[i];	
			}
		}	

		return colors;	
	}

	/**
	 * Returns array of localized color names 
	 * @param app
	 * @param color
	 * @return array of localized color names
	 */
	public static String[] getColorNames(Application app, Color[] color){
		String[] s = new String[color.length];
		for(int i=0; i<s.length; i++){
			s[i] =  getGeogebraColorName(app, color[i]);
		}
		return s;
	}


	private static HashMap<String, Color> colors = null;

	public static final HashMap<String, Color> htmlColorMap() {
		
		if (colors != null) return colors;

		colors = new HashMap<String, Color>();

		// HTML 3.2
		//colors.put("AQUA", new Color(0x00FFFF));
		//colors.put("BLACK", new Color(0x000000));
		//colors.put("BLUE", new Color(0x0000FF));
		colors.put("FUCHSIA", new Color(0xFF00FF));
		//colors.put("GRAY", new Color(0x808080));
		//colors.put("GREEN", new Color(0x008000));
		//colors.put("LIME", new Color(0x00FF00));
		//colors.put("MAROON", new Color(0x800000));
		colors.put("NAVY", new Color(0x000080));
		colors.put("OLIVE", new Color(0x808000));
		//colors.put("PURPLE", new Color(0x800080));
		//colors.put("RED", new Color(0xFF0000));
		//colors.put("SILVER", new Color(0xC0C0C0));
		colors.put("TEAL", new Color(0x008080));
		//colors.put("WHITE", new Color(0xFFFFFF));
		//colors.put("YELLOW", new Color(0xFFFF00));

		colors.put("ALICEBLUE", new Color(0xEFF7FF));
		colors.put("ANTIQUEWHITE", new Color(0xF9E8D2));
		colors.put("AQUAMARINE", new Color(0x43B7BA));
		colors.put("AZURE", new Color(0xEFFFFF));
		colors.put("BEIGE", new Color(0xF5F3D7));
		colors.put("BISQUE", new Color(0xFDE0BC));
		colors.put("BLANCHEDALMOND", new Color(0xFEE8C6));
		colors.put("BLUEVIOLET", new Color(0x7931DF));
		//colors.put("BROWN", new Color(0x980516));
		colors.put("BURLYWOOD", new Color(0xEABE83));
		colors.put("CADETBLUE", new Color(0x578693));
		colors.put("CHARTREUSE", new Color(0x8AFB17));
		colors.put("CHOCOLATE", new Color(0xC85A17));
		colors.put("CORAL", new Color(0xF76541));
		colors.put("CORNFLOWERBLUE", new Color(0x151B8D));
		colors.put("CORNSILK", new Color(0xFFF7D7));
		//colors.put("CRIMSON", new Color(0xE41B17));
		colors.put("CYAN", new Color(0x00FFFF));
		//colors.put("DARKBLUE", new Color(0x2F2F4F));
		colors.put("DARKCYAN", new Color(0x57FEFF));
		colors.put("DARKGOLDENROD", new Color(0xAF7817));
		//colors.put("DARKGRAY", new Color(0x7A7777));
		//colors.put("DARKGREEN", new Color(0x254117));
		colors.put("DARKKHAKI", new Color(0xB7AD59));
		colors.put("DARKMAGENTA", new Color(0xF43EFF));
		colors.put("DARKOLIVEGREEN", new Color(0xCCFB5D));
		colors.put("DARKORANGE", new Color(0xF88017));
		colors.put("DARKORCHID", new Color(0x7D1B7E));
		colors.put("DARKRED", new Color(0xE41B17));
		colors.put("DARKSALMON", new Color(0xE18B6B));
		colors.put("DARKSEAGREEN", new Color(0x8BB381));
		colors.put("DARKSLATEBLUE", new Color(0x2B3856));
		colors.put("DARKSLATEGRAY", new Color(0x253856));
		colors.put("DARKTURQUOISE", new Color(0x3B9C9C));
		colors.put("DARKVIOLET", new Color(0x842DCE));
		colors.put("DEEPPINK", new Color(0xF52887));
		colors.put("DEEPSKYBLUE", new Color(0x3BB9FF));
		colors.put("DIMGRAY", new Color(0x463E41));
		colors.put("DODGERBLUE", new Color(0x1589FF));
		colors.put("FIREBRICK", new Color(0x800517));
		colors.put("FLORALWHITE", new Color(0xFFF9EE));
		colors.put("FORESTGREEN", new Color(0x4E9258));
		colors.put("GAINSBORO", new Color(0xD8D9D7));
		colors.put("GHOSTWHITE", new Color(0xF7F7FF));
		//colors.put("GOLD", new Color(0xD4A017));
		colors.put("GOLDENROD", new Color(0xEDDA74));
		colors.put("GREENYELLOW", new Color(0xB1FB17));
		colors.put("HONEYDEW", new Color(0xF0FEEE));
		colors.put("INDIANRED", new Color(0x5E2217));
		//colors.put("INDIGO", new Color(0x307D7E));
		colors.put("IVORY", new Color(0xFFFFEE));
		colors.put("KHAKI", new Color(0xADA96E));
		colors.put("LAVENDER", new Color(0xE3E4FA));
		colors.put("LAVENDERBLUSH", new Color(0xFDEEF4));
		colors.put("LAWNGREEN", new Color(0x87F717));
		colors.put("LEMONCHIFFON", new Color(0xFFF8C6));
		//colors.put("LIGHTBLUE", new Color(0xADDFFF));
		colors.put("LIGHTCORAL", new Color(0xE77471));
		colors.put("LIGHTCYAN", new Color(0xE0FFFF));
		colors.put("LIGHTGOLDENRODYELLOW", new Color(0xFAF8CC));
		//colors.put("LIGHTGREEN", new Color(0xCCFFCC));
		//colors.put("LIGHTGRAY", Color.LIGHT_GRAY);
		colors.put("LIGHTPINK", new Color(0xFAAFBA));
		colors.put("LIGHTSALMON", new Color(0xF9966B));
		colors.put("LIGHTSEAGREEN", new Color(0x3EA99F));
		colors.put("LIGHTSKYBLUE", new Color(0x82CAFA));
		colors.put("LIGHTSLATEGRAY", new Color(0x6D7B8D));
		colors.put("LIGHTSTEELBLUE", new Color(0x728FCE));
		//colors.put("LIGHTYELLOW", new Color(0xFFFEDC));
		colors.put("LIMEGREEN", new Color(0x41A317));
		colors.put("LINEN", new Color(0xF9EEE2));
		//colors.put("MAGENTA", new Color(0xFF00FF));
		colors.put("MEDIUMAQUAMARINE", new Color(0x348781));
		colors.put("MEDIUMBLUE", new Color(0x152DC6));
		colors.put("MEDIUMORCHID", new Color(0xB048B5));
		colors.put("MEDIUMPURPLE", new Color(0x8467D7));
		colors.put("MEDIUMSEAGREEN", new Color(0x306754));
		colors.put("MEDIUMSLATEBLUE", new Color(0x5E5A80));
		colors.put("MEDIUMSPRINGGREEN", new Color(0x348017));
		colors.put("MEDIUMTURQUOISE", new Color(0x48CCCD));
		colors.put("MEDIUMVIOLETRED", new Color(0xCA226B));
		colors.put("MIDNIGHTBLUE", new Color(0x151B54));
		colors.put("MINTCREAM", new Color(0xF5FFF9));
		colors.put("MISTYROSE", new Color(0xFDE1DD));
		colors.put("MOCCASIN", new Color(0xFDE0AC));
		colors.put("NAVAJOWHITE", new Color(0xFDDAA3));
		colors.put("OLDLACE", new Color(0xFCF3E2));
		colors.put("OLIVEDRAB", new Color(0x658017));
		//colors.put("ORANGE", new Color(0xF87A17));
		colors.put("ORANGERED", new Color(0xF63817));
		colors.put("ORCHID", new Color(0xE57DED));
		colors.put("PALEGOLDENROD", new Color(0xEDE49E));
		colors.put("PALETURQUOISE", new Color(0xAEEBEC));
		colors.put("PALEVIOLETRED", new Color(0xD16587));
		colors.put("PAPAYAWHIP", new Color(0xFEECCF));
		colors.put("PEACHPUFF", new Color(0xFCD5B0));
		colors.put("PERU", new Color(0xC57726));
		//colors.put("PINK", new Color(0xFAAFBE));
		colors.put("PLUM", new Color(0xB93B8F));
		colors.put("POWDERBLUE", new Color(0xADDCE3));
		colors.put("ROSYBROWN", new Color(0xB38481));
		colors.put("ROYALBLUE", new Color(0x2B60DE));
		colors.put("SADDLEBROWN", new Color(0xF63526));
		colors.put("SALMON", new Color(0xF88158));
		colors.put("SANDYBROWN", new Color(0xEE9A4D));
		colors.put("SEAGREEN", new Color(0x4E8975));
		colors.put("SEASHELL", new Color(0xFEF3EB));
		colors.put("SIENNA", new Color(0x8A4117));
		colors.put("SKYBLUE", new Color(0x6698FF));
		colors.put("SLATEBLUE", new Color(0x737CA1));
		colors.put("SLATEGRAY", new Color(0x657383));
		colors.put("SNOW", new Color(0xFFF9FA));
		colors.put("SPRINGGREEN", new Color(0x4AA02C));
		colors.put("STEELBLUE", new Color(0x4863A0));
		colors.put("TAN", new Color(0xD8AF79));
		colors.put("THISTLE", new Color(0xD2B9D3));
		colors.put("TOMATO", new Color(0xF75431));
		//colors.put("TURQUOISE", new Color(0x43C6DB));
		//colors.put("VIOLET", new Color(0x8D38C9));
		colors.put("WHEAT", new Color(0xF3DAA9));
		colors.put("WHITESMOKE", new Color(0xFFFFFF));
		colors.put("YELLOWGREEN", new Color(0x52D017));

		return colors;
	}
}





