package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;

/**
 * Renders text for the input box.
 */
public interface TextRenderer {

	/**
	 * Draw the text in the specified location.
	 *
	 * @param geo geo
	 * @param graphics graphics
	 * @param font font
	 * @param text text to draw
	 * @param xPos x coordinate
	 * @param yPos y coordinate
	 */
	void drawText(GeoInputBox geo, GGraphics2D graphics,
				  GFont font, String text,
				  double xPos, double yPos);

	/**
	 * Measure the text that is draw.
	 *
	 * @param graphics graphics
	 * @param geo geo
	 * @param font font
	 * @param labelDescription label
	 *
	 * @return size of the drawn text
	 */
	GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font,
							 String labelDescription);
}
