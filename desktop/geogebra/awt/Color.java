package geogebra.awt;



public class Color implements geogebra.common.awt.Color {

	
	public static final Color white = new Color(255,255,255);
	public static final Color black = new Color(0,0,0);
	
	private java.awt.Color adaptedColor = new java.awt.Color(0, 0, 0);

	public Color(float r, float g, float b, float alpha) {
		adaptedColor = new java.awt.Color(r,g,b,alpha); 
	}

	public Color(int r, int g, int b) {
		adaptedColor = new java.awt.Color(r,g,b); 
	}

	public void getRGBColorComponents(float[] rgb) {
		adaptedColor.getRGBComponents(rgb);
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
}
