package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXIcon;

/**
 * Renderer for LaTeX cells
 */
public class LaTeXRenderer implements CellRenderer {
	@Override
	public void draw(Object data, GGraphics2D graphics, Rectangle cellBorder) {
		((TeXIcon) data).paintIcon(() -> null, graphics.getGraphicsForLaTeX(),
				cellBorder.getMinX(), cellBorder.getMinY());
	}

	@Override
	public boolean match(Object renderable) {
		return renderable instanceof TeXIcon;
	}
}
