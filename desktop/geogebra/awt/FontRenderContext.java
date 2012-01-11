package geogebra.awt;


public class FontRenderContext extends geogebra.common.awt.FontRenderContext {
	private java.awt.font.FontRenderContext impl;
	public FontRenderContext(java.awt.font.FontRenderContext frc){
		impl = frc;
	}
	public static java.awt.font.FontRenderContext getAwtFrc(geogebra.awt.FontRenderContext frc) {
		if(!(frc instanceof FontRenderContext)){
			return null;
		}
		return ((FontRenderContext)frc).impl;
	}

}
