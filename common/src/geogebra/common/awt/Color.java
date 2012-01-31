package geogebra.common.awt;

import geogebra.common.factories.AwtFactory;

public abstract class Color implements Paint{

	public static Color white;
	public static Color black;
	public static Color RED;
	public static Color WHITE;
	public static Color BLACK;
	public static Color BLUE;
	public static Color GRAY;
	public static Color GREEN;
	public static Color YELLOW;
	public static Color DARK_GRAY;
	public static Color LIGHT_GRAY;
	public static Color CYAN;
	public static Color MAGENTA;
	public static Color red;
	public static Color yellow;
	public static Color green;
	public static Color blue;
	public static Color cyan;
	public static Color magenta;
	public static Color lightGray;
	public static Color gray;
	public static Color darkGray;
	
	public static void initColors(AwtFactory f){
		 white = f.newColor(255,255,255);
		 black = f.newColor(0, 0, 0);
		 RED = f.newColor(255, 0, 0);
		 WHITE = f.newColor(255, 255, 255);
		 BLACK = f.newColor(0, 0, 0);
		 BLUE = f.newColor(0, 0, 255);
		 GRAY = f.newColor(128, 128, 128);
		 GREEN = f.newColor(0, 255, 0);
		 YELLOW = f.newColor(255, 255, 0);
		 DARK_GRAY = f.newColor(68, 68, 68);
		 LIGHT_GRAY = f.newColor(192, 192, 192);
		 CYAN = f.newColor(0, 255, 255);
		 MAGENTA = f.newColor(255, 0, 255);
		 red = f.newColor(255, 0, 0);
		 yellow = f.newColor(255, 255, 0);
		 green = f.newColor(0, 255, 0);
		 blue = f.newColor(0, 0, 255);
		 cyan = f.newColor(0, 255, 255);
		 magenta = f.newColor(255, 0, 255);
		 lightGray = f.newColor(192, 192, 192);
		 gray = f.newColor(128, 128, 128);
		 darkGray = f.newColor(68, 68, 68);
	}
	public abstract int getRed();
	public abstract int getBlue();
	public abstract int getGreen();
	public abstract int getAlpha();
	/*ÉJ float[]*/ public abstract void getRGBColorComponents(float[] rgb);
	
	//public Color(float r, float g, float b, float alpha);
	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
			if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)java.lang.Math.floor(h);
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
	public abstract geogebra.common.awt.Color darker();
	public abstract geogebra.common.awt.Color brighter();
	public static String getColorString(Color fillColor){
		return "rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+(fillColor.getAlpha()/255d)+")";
	}
}
