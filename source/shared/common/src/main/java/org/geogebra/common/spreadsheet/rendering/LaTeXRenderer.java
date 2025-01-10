package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXIcon;

/**
 * Renderer for LaTeX cells
 */
public final class LaTeXRenderer implements CellRenderer {

	private AwtReTeXGraphicsBridge bridge;

	public LaTeXRenderer(AwtReTeXGraphicsBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void draw(Object data, int fontStyle, double offsetX,
			GGraphics2D graphics, Rectangle cellBorder) {
		graphics.setColor(GColor.BLACK);
		TeXIcon teXIcon = (TeXIcon) data;
		teXIcon.paintIcon(null, bridge.convert(graphics),
				cellBorder.getMinX() + offsetX, cellBorder.getMinY()
						+ (cellBorder.getHeight() - teXIcon.getIconHeight()) / 2);
	}

	@Override
	public boolean match(Object renderable) {
		return renderable instanceof TeXIcon;
	}

	@Override
	public double measure(Object renderable, int fontStyle) {
		return ((TeXIcon) renderable).getIconWidth();
	}
}
