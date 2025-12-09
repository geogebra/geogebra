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

package org.geogebra.common.awt.font;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;

/**
 * Interface for classes compatible with java.awt.font.TextLayout
 */
public interface GTextLayout {

	/**
	 * @return width of the text in pixels
	 */
	double getAdvance();

	/**
	 * @return bounding rectangle
	 */
	GRectangle2D getBounds();

	/**
	 * 
	 * @return distance between baseline and top of the highest character
	 */
	double getAscent();

	/**
	 * Draw this text in graphics
	 * 
	 * @param g2
	 *            graphics
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	void draw(GGraphics2D g2, int x, int y);

	/**
	 * @return distance between baseline and deepest bottom of a character
	 */
	double getDescent();

}
