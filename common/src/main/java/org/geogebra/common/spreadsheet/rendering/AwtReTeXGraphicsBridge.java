package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GGraphics2D;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/** Converts AWT graphics object to ReTeX graphics object. */
public interface AwtReTeXGraphicsBridge {

	/**
	 * Convert AWT to ReTeX graphics object.
	 * @param gGraphics2D AWT graphics object
	 * @return ReTeX graphics object
	 */
	Graphics2DInterface convert(GGraphics2D gGraphics2D);
}
