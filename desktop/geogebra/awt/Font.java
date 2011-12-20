package geogebra.awt;

public class Font extends geogebra.common.awt.Font {
	
	private java.awt.Font adaptedFont = new java.awt.Font("Default", Font.PLAIN, 12);

	

	public java.awt.Font getAwtFont() {
		return adaptedFont;
	}

}
