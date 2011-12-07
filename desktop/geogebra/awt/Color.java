package geogebra.awt;



public class Color extends geogebra.common.awt.Color {

	
	public static final Color white = new Color(255,255,255);
	public static final Color black = new Color(0, 0, 0);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color DARK_GRAY = new Color(68, 68, 68);
	public static final Color red = new Color(255, 0, 0);
	public static final Color yellow = new Color(255, 255, 0);
	public static final Color green = new Color(0, 255, 0);
	public static final Color blue = new Color(0, 0, 255);
	public static final Color cyan = new Color(0, 255, 255);
	public static final Color magenta = new Color(255, 0, 255);
	public static final Color lightGray = new Color(192, 192, 192);
	public static final Color gray = new Color(128, 128, 128);
	public static final Color darkGray = new Color(68, 68, 68);
	
	private static final double FACTOR = 0.7;
	
	private java.awt.Color adaptedColor = new java.awt.Color(0, 0, 0);

	public Color(int r, int g, int b, int alpha) {
		adaptedColor = new java.awt.Color(r,g,b,alpha); 
	}
	
	public Color(float r, float g, float b, float alpha) {
		adaptedColor = new java.awt.Color(r,g,b,alpha); 
	}

	public Color(int r, int g, int b) {
		adaptedColor = new java.awt.Color(r,g,b); 
	}

	public Color(int r, float g, int b) {
		adaptedColor = new java.awt.Color(r, g ,b);
	}

	public Color(int rgb) {
		adaptedColor = new java.awt.Color(rgb);
	}

	public Color(java.awt.Color hsbColor) {
		adaptedColor = hsbColor;
		// TODO Auto-generated constructor stub
	}

	public Color(float f, float g, float h) {
		adaptedColor = new java.awt.Color(f,g,h);
	}

	public void getRGBColorComponents(float[] rgb) {
		adaptedColor.getRGBColorComponents(rgb);
	}

	public int getBlue() {
		return adaptedColor.getBlue();
	}

	public int getAlpha() {
		return adaptedColor.getAlpha();
	}

	public int getGreen() {
		return adaptedColor.getGreen();
	}

	public int getRed() {
		return adaptedColor.getRed();
	}

	public static int HSBtoRGB(float redD, float greenD, float blueD) {
		return java.awt.Color.HSBtoRGB(redD, greenD, blueD);
	}
    
	public java.awt.Color getAwtColor() {
		return adaptedColor;
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsb) {
		return java.awt.Color.RGBtoHSB(r, g, b, hsb);
		
	}

	public static Color getHSBColor(float h, float s, float b) {
		// TODO Auto-generated method stub
		return new Color(java.awt.Color.getHSBColor(h, s, b));
	}
	
    public Color darker() {
    	return new Color(Math.max((int)(getRed()  *FACTOR), 0), 
			 Math.max((int)(getGreen()*FACTOR), 0),
			 Math.max((int)(getBlue() *FACTOR), 0));
    }
}
