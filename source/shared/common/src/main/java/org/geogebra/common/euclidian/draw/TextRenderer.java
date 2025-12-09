/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.TextRendererSettings;
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
			GFont font, String text, double xPos, double yPos);

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

	/**
	 *
	 * @return the settings of the renderer.
	 */
	TextRendererSettings getSettings();
}
