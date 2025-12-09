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

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

public interface EndDecoratedDrawable {
	/**
	 * @return screen x-coordinate of start point
	 */
	double getX1();

	/**
	 * @return screen x-coordinate of end point
	 */
	double getX2();

	/**
	 * @return screen y-coordinate of start point
	 */
	double getY1();

	/**
	 * @return screen y-coordinate of end point
	 */
	double getY2();

	/**
	 * Set style for highlighting.
	 */
	void setHighlightingStyle(GGraphics2D g2);

	/**
	 * @return shape without decoration
	 */
	GShape getLine();

	/**
	 * Set style for default drawing.
	 * @param g2 graphics
	 */
	void setBasicStyle(GGraphics2D g2);

	/**
	 * @param isStart whether to consider the first segment
	 * @return angle between x-axis and first or last segment
	 */
	double getAngle(boolean isStart);

	/**
	 * @return stroke with no dash pattern
	 */
	GBasicStroke getDecoStroke();

	/**
	 * @return whether the shape is highlighted
	 */
	boolean isHighlighted();
}
