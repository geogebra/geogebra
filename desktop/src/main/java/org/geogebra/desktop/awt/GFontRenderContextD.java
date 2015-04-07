package org.geogebra.desktop.awt;

public class GFontRenderContextD extends org.geogebra.common.awt.GFontRenderContext {
	private java.awt.font.FontRenderContext impl;

	public GFontRenderContextD(java.awt.font.FontRenderContext frc) {
		impl = frc;
	}

	public static java.awt.font.FontRenderContext getAwtFrc(
			org.geogebra.common.awt.GFontRenderContext frc) {
		if (!(frc instanceof GFontRenderContextD)) {
			return null;
		}
		return ((GFontRenderContextD) frc).impl;
	}

}
