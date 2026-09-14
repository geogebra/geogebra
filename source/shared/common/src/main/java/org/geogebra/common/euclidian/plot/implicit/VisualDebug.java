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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.MyPoint;

/**
 * Visual debug aid for Bernstein poly plotter.
 */
public interface VisualDebug {
	/**
	 * Draw this in graphics.
	 * @param g2 graphics
	 */
	void draw(GGraphics2D g2);

	/**
	 * Fills the debug overlay into the given graphics context.
	 *
	 * @param g2 graphics target
	 */
	void fill(GGraphics2D g2);

	/**
	 * Supplies edge-intersection points to visualize.
	 *
	 * @param edgePoints boundary points collected during clipping
	 */
	void setEdgePoints(List<MyPoint> edgePoints);
}
