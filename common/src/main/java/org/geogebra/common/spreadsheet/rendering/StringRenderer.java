package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renderer for plain text cells
 */
public class StringRenderer implements CellRenderer {

	private static final int VERTICAL_PADDING = 3;
	private static final int HORIZONTAL_PADDING = 5;

	@Override
	public void draw(Object data, GGraphics2D graphics, Rectangle cellBorder) {
		graphics.setColor(GColor.BLACK);
		graphics.drawString(data.toString(), cellBorder.getMinX() + HORIZONTAL_PADDING,
				cellBorder.getMaxY() - VERTICAL_PADDING);
	}

	@Override
	public boolean match(Object renderable) {
		return renderable instanceof String;
	}
}
