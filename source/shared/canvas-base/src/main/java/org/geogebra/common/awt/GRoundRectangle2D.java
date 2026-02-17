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

package org.geogebra.common.awt;

public interface GRoundRectangle2D extends GShape {

	/**
	 * Sets the location, size, and corner radii of this
	 * <code>RoundRectangle2D</code> to the specified
	 * <code>double</code> values.
	 *
	 * @param x the X coordinate to which to set the
	 *          location of this <code>RoundRectangle2D</code>
	 * @param y the Y coordinate to which to set the
	 *          location of this <code>RoundRectangle2D</code>
	 * @param w the width to which to set this
	 *          <code>RoundRectangle2D</code>
	 * @param h the height to which to set this
	 *          <code>RoundRectangle2D</code>
	 * @param arcWidth the width to which to set the arc of this
	 *                 <code>RoundRectangle2D</code>
	 * @param arcHeight the height to which to set the arc of this
	 *                  <code>RoundRectangle2D</code>
	 */
	void setRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight);
}
