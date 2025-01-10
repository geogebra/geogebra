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

	public QuadTree() {
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
	public void updatePath(double startX, double startY, double width,
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
	public void polishPointOnPath(GeoPointND pt) {
		// pt.setUndefined();
	}

	public int edgeConfig(Rect r) {
		int config = (intersect(r.evals[0], r.evals[1]) << 3)
				| (intersect(r.evals[1], r.evals[2]) << 2)
				| (intersect(r.evals[2], r.evals[3]) << 1)
				| (intersect(r.evals[3], r.evals[0]));
		if (config == 15 || config == 0) {
			return QuadTreeEdgeConfig.EMPTY.flag();
		}
		return config;
	}

	/**
	 * @param c1 the value of curve at one of the square vertices
	 * @param c2 the value of curve at the other vertex
	 * @return true if the edge connecting two vertices intersect with curve
	 * segment
	 */
	private static int intersect(double c1, double c2) {
		if (c1 * c2 <= 0.0) {
			return 1;
		}
		return 0;
	}

	public abstract void updatePath();

	public LinkSegments segments() {
		return segments;
	}

	void setListThreshold(int threshold) {
		segments.setListThreshold(threshold);
	}
}