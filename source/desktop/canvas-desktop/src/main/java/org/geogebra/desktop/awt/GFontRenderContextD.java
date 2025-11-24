package org.geogebra.desktop.awt;

import java.awt.font.FontRenderContext;

import org.geogebra.common.awt.GFontRenderContext;

public class GFontRenderContextD extends GFontRenderContext {
	private FontRenderContext impl;

	public GFontRenderContextD(FontRenderContext frc) {
		impl = frc;
	}

	/**
	 * @param frc cross-platform FRC
	 * @return native FRC
	 */
	public static FontRenderContext getAwtFrc(GFontRenderContext frc) {
		if (!(frc instanceof GFontRenderContextD)) {
			return null;
		}
		return ((GFontRenderContextD) frc).impl;
	}

}
