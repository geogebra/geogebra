package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;

/**
 * Renderer for plain text cells
 */
public class StringRenderer implements CellRenderer {
	private final String value;

	public StringRenderer(String value) {
		this.value = value;
	}

	@Override
	public void draw(GGraphics2D graphics, int x, int y) {
		graphics.drawString((String) value, x, y + 20);
	}
}
