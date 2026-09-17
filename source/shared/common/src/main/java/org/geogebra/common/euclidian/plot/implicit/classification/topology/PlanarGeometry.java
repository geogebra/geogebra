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

package org.geogebra.common.euclidian.plot.implicit.classification.topology;

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.AREA_RELATIVE_TOLERANCE;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.GEOMETRY_EPSILON;

import org.geogebra.common.awt.GPoint2D;

/**
 * Low-level planar geometry helpers for topology construction.
 */
final class PlanarGeometry {
	/**
	 * @return whether the point lies on the given segment at graph tolerance
	 */
	static boolean isPointOnSegment(GPoint2D p, double x1, double y1, double x2, double y2) {
		double cross = (p.y - y1) * (x2 - x1) - (p.x - x1) * (y2 - y1);
		if (Math.abs(cross) > GEOMETRY_EPSILON) {
			return false;
		}
		double dot = (p.x - x1) * (p.x - x2) + (p.y - y1) * (p.y - y2);
		return dot <= 0;
	}

	/**
	 * Axis-aligned coordinate bounds for a planar boundary.
	 */
	static final class BoundingBox {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		/**
		 * Expands this box to contain the given point.
		 * @param x x coordinate
		 * @param y y coordinate
		 */
		void include(double x, double y) {
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);
		}

		/**
		 * @return whether this box intersects another box
		 */
		boolean intersects(BoundingBox bbox) {
			return minX <= bbox.maxX && bbox.minX <= maxX && minY <= bbox.maxY && bbox.minY <= maxY;
		}

		/**
		 * @return whether this box has equivalent bounds at graph tolerance
		 */
		boolean hasNearlyEqualBounds(BoundingBox bbox) {
			double span = Math.max(
					Math.max(maxX - minX, maxY - minY),
					Math.max(bbox.maxX - bbox.minX, bbox.maxY - bbox.minY));
			double tolerance = Math.max(GEOMETRY_EPSILON, span * AREA_RELATIVE_TOLERANCE);
			return Math.abs(minX - bbox.minX) <= tolerance
					&& Math.abs(maxX - bbox.maxX) <= tolerance
					&& Math.abs(minY - bbox.minY) <= tolerance
					&& Math.abs(maxY - bbox.maxY) <= tolerance;
		}

		/**
		 * @return whether this box contains at least one finite point
		 */
		boolean isFinite() {
			return Double.isFinite(minX)
					&& Double.isFinite(minY)
					&& Double.isFinite(maxX)
					&& Double.isFinite(maxY);
		}
	}
}
