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

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

/**
 * Bounding box delegate.
 */
public interface BoundingBoxDelegate {
	/**
	 * Create all handlers.
	 */
	void createHandlers();

	/**
	 * @return single corner handler
	 */
	GShape createCornerHandler();

	/**
	 * @return single side handler
	 */
	GShape createSideHandler();

	/**
	 * Draw in graphics.
	 * @param g2 graphics
	 */
	void draw(GGraphics2D g2);

	/**
	 * Check if side of bounding box is hit.
	 * @param x x-coordinate in pixels
	 * @param y y-coordinate in pixels
	 * @param hitThreshold hit threshold
	 * @return whether side was hit.
	 */
	boolean hitSideOfBoundingBox(int x, int y, int hitThreshold);

	/**
	 * Update the shape of given handler.
	 * @param handlerIndex handler index
	 * @param x x-coordinate of handler's center
	 * @param y y-coordinate of handler's center
	 */
	void setHandlerFromCenter(int handlerIndex, double x, double y);
}
