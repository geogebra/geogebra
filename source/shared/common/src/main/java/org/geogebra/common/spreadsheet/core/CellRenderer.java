package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renders content of a single spreadsheet cell
 */
public interface CellRenderer {

	/**
	 * @param data object to be rendered
	 * @param fontStyle bitmask for GFont.BOLD and GFont.ITALIC
	 * @param offsetX x-Offset
	 * @param g2d graphics
	 * @param cellBorder cell rectangle, coordinates relative to the graphics
	 */
	void draw(Object data, int fontStyle, double offsetX, GGraphics2D g2d, Rectangle cellBorder);

	/**
	 * @param renderable object to be potentially rendered
	 * @return whether this can render given object
	 */
	boolean match(Object renderable);

	double measure(Object renderable, int fontStyle);
}
