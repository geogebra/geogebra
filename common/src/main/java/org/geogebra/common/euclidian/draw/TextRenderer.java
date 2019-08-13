package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;

/**
 * Renders text for the input box.
 */
public interface TextRenderer {

	/**
	 * Draw the text in the specified location.
	 *
	 * @param app app
	 * @param geo geo
	 * @param graphics graphics
	 * @param font font
	 * @param text text to draw
	 * @param xPos x coordinate
	 * @param yPos y coordinate
	 * @param boxWidth width of the box
	 * @param lineHeight size of the line
	 */
	void drawText(App app, GeoInputBox geo, GGraphics2D graphics, GFont font,
				  String text, double xPos, double yPos, double boxWidth, int lineHeight);

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
	GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font, String labelDescription);
}
