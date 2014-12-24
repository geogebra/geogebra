package geogebra.awt;

public class GFontRenderContextD extends geogebra.common.awt.GFontRenderContext {
	private java.awt.font.FontRenderContext impl;

	public GFontRenderContextD(java.awt.font.FontRenderContext frc) {
		impl = frc;
	}

	public static java.awt.font.FontRenderContext getAwtFrc(
			geogebra.common.awt.GFontRenderContext frc) {
		if (!(frc instanceof GFontRenderContextD)) {
			return null;
		}
		return ((GFontRenderContextD) frc).impl;
	}

}
