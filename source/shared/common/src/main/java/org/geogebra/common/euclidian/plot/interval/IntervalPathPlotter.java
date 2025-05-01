package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.interval.Interval;

/**
 * Interval path plotter.
 */
public interface IntervalPathPlotter {
	void reset();

	/**
	 * Add a move-to point.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void moveTo(double x, double y);

	/**
	 * Add a line segment from current point to given coordinates.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void lineTo(double x, double y);

	/**
	 * Draws a line segment.
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void segment(double x1, double y1, double x2, double y2);

	/**
	 * Draws a line segment clipped to view bounds.
	 * @param bounds view bounds
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void segment(EuclidianViewBounds bounds, double x1, double y1, double x2, double y2);

	void draw(GGraphics2D g2);

	/**
	 * Add segment from lower bound of y to upper bound of the view.
	 * @param bounds view bounds
	 * @param x x-coordinate range of the segment
	 * @param y lower bound for the segment
	 */
	void leftToTop(EuclidianViewBounds bounds, Interval x, Interval y);

	/**
	 * Add segment from upper bound of y to lower bound of the view.
	 * @param bounds view bounds
	 * @param x x-coordinate range of the segment
	 * @param y upper bound for the segment
	 */
	void leftToBottom(EuclidianViewBounds bounds, Interval x, Interval y);
}
