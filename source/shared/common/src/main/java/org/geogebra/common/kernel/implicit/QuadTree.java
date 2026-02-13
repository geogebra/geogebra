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

package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Base class for quadtree algorithms
 */
abstract class QuadTree {
	protected double x;
	protected double y;
	protected double w;
	protected double h;
	protected double scaleX;
	protected double scaleY;
	protected ArrayList<MyPoint> locusPoints;
	private LinkSegments segments;

	QuadTree() {
		segments = new LinkSegments();
	}

	/**
	 * force to redraw the rectangular area bounded by (startX, startY, startX +
	 * w, startY + h)
	 * @param startX starting x coordinate
	 * @param startY starting y coordinate
	 * @param width width of the rectangular view
	 * @param height height of the rectangular view
	 * @param slX scaleX
	 * @param slY scaleY
	 */
	void updatePath(double startX, double startY, double width,
			double height, double slX, double slY, GeoLocus locus) {
		this.x = startX;
		this.y = startY;
		this.w = width;
		this.h = height;
		this.scaleX = slX;
		this.scaleY = slY;
		this.locusPoints = locus.getPoints();
		segments.updatePoints(locusPoints);
		this.updatePath();
		segments.flush();
	}

	/**
	 * @param pt point to be polished
	 */
	void polishPointOnPath(GeoPointND pt) {
		// pt.setUndefined();
	}

	int edgeConfig(ImplicitCurveMarchingRect r) {
		int config = (intersect(r.evals[0], r.evals[1]) << 3)
				| (intersect(r.evals[1], r.evals[2]) << 2)
				| (intersect(r.evals[2], r.evals[3]) << 1)
				| intersect(r.evals[3], r.evals[0]);
		if (config == 15 || config == 0) {
			return QuadTreeEdgeConfig.EMPTY.flag();
		}
		return config;
	}

	/**
	 * @param c1 the value of curve at one of the square vertices
	 * @param c2 the value of curve at the other vertex
	 * @return 1 if the edge connecting two vertices intersect with curve
	 * segment
	 */
	private static int intersect(double c1, double c2) {
		if (c1 * c2 <= 0.0) {
			return 1;
		}
		return 0;
	}

	abstract void updatePath();

	LinkSegments segments() {
		return segments;
	}

	void setListThreshold(int threshold) {
		segments.setListThreshold(threshold);
	}
}