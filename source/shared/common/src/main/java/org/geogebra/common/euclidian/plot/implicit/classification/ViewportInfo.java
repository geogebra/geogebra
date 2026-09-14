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

package org.geogebra.common.euclidian.plot.implicit.classification;

import org.geogebra.common.awt.GRectangle2D;

/**
 * Immutable snapshot of the viewport used while building one implicit-plot graph.
 * <p>
 * It ties the four graph vertices that represent the viewport corners to the
 * world-coordinate bounds used for clipping and area comparisons. The graph can be
 * rebuilt for another viewport by creating a new instance instead of mutating this one.
 * </p>
 *
 * @param topLeft graph vertex id for the top-left viewport corner
 * @param topRight graph vertex id for the top-right viewport corner
 * @param bottomLeft graph vertex id for the bottom-left viewport corner
 * @param bottomRight graph vertex id for the bottom-right viewport corner
 * @param xmin viewport minimum x-coordinate in world space
 * @param xmax viewport maximum x-coordinate in world space
 * @param ymin viewport minimum y-coordinate in world space
 * @param ymax viewport maximum y-coordinate in world space
 * @param absArea absolute viewport area in world-coordinate units
 */
public record ViewportInfo(int topLeft, int topRight, int bottomLeft, int bottomRight,
		double xmin, double xmax, double ymin, double ymax, double absArea) {

	/**
	 * Creates a viewport snapshot from graph corner vertices and the rectangle that
	 * was used to clip the implicit fragments.
	 *
	 * @param topLeft graph vertex id for the top-left viewport corner
	 * @param topRight graph vertex id for the top-right viewport corner
	 * @param bottomLeft graph vertex id for the bottom-left viewport corner
	 * @param bottomRight graph vertex id for the bottom-right viewport corner
	 * @param rect clipping viewport in world coordinates
	 * @return viewport metadata for the current graph build
	 */
	static ViewportInfo from(
			int topLeft, int topRight, int bottomLeft, int bottomRight,
			GRectangle2D rect) {
		double absArea = Math.abs(rect.getWidth() * rect.getHeight());
		return new ViewportInfo(
				topLeft, topRight, bottomLeft, bottomRight,
				rect.getMinX(), rect.getMaxX(), rect.getMinY(), rect.getMaxY(),
				absArea);
	}

	/**
	 * Creates a viewport snapshot from explicit bounds. This is useful for tests and
	 * callers that already created the corner vertices without a rectangle object.
	 *
	 * @param topLeft graph vertex id for the top-left viewport corner
	 * @param topRight graph vertex id for the top-right viewport corner
	 * @param bottomLeft graph vertex id for the bottom-left viewport corner
	 * @param bottomRight graph vertex id for the bottom-right viewport corner
	 * @param xmin viewport minimum x-coordinate in world space
	 * @param xmax viewport maximum x-coordinate in world space
	 * @param ymin viewport minimum y-coordinate in world space
	 * @param ymax viewport maximum y-coordinate in world space
	 * @return viewport metadata for the current graph build
	 */
	public static ViewportInfo ofBounds(
			int topLeft, int topRight, int bottomLeft, int bottomRight,
			double xmin, double xmax, double ymin, double ymax) {
		return new ViewportInfo(topLeft, topRight, bottomLeft, bottomRight,
				xmin, xmax, ymin, ymax,
				Math.abs((xmax - xmin) * (ymax - ymin)));
		}

		/**
		 * @return whether the viewport area can be used for boundary matching
		 */
		public boolean hasValidAbsArea() {
			return Double.isFinite(absArea);
		}
}
