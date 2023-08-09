package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renders content of a single spreadsheet cell
 */
public interface CellRenderer {

	/**
	 * @param data object to be rendered
	 * @param g2d graphics
	 * @param cellBorder cell rectangle, coordinates relative to the graphics
	 */
	public void draw(Object data, GGraphics2D g2d, Rectangle cellBorder);

	/**
	 * @param renderable object to be potentially rendered
	 * @return whether this can render given object
	 */
	boolean match(Object renderable);
}
