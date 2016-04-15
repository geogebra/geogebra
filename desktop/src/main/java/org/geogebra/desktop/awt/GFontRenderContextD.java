package org.geogebra.desktop.awt;

import java.awt.font.FontRenderContext;

import org.geogebra.common.awt.GFontRenderContext;

public class GFontRenderContextD extends GFontRenderContext {
	private FontRenderContext impl;

	public GFontRenderContextD(java.awt.font.FontRenderContext frc) {
		impl = frc;
	}

	public static java.awt.font.FontRenderContext getAwtFrc(
			GFontRenderContext frc) {
		if (!(frc instanceof GFontRenderContextD)) {
			return null;
		}
		return ((GFontRenderContextD) frc).impl;
	}


}
