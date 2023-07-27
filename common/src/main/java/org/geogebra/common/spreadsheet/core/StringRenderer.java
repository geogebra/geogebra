package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renderer for plain text cells
 */
public class StringRenderer implements CellRenderer {
	private final String value;

	public StringRenderer(String value) {
		this.value = value;
	}

	@Override
	public void draw(GGraphics2D graphics, Rectangle cellBorder) {
		graphics.drawString(value, cellBorder.getMinX(), cellBorder.getMaxY());
	}
}
