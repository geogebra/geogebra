package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.interval.Interval;

/**
 * Interval path plotter.
 */
public interface IntervalPathPlotter {
	void reset();

	void moveTo(double x, double y);

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

	void leftToTop(EuclidianViewBounds bounds, Interval x, Interval y);

	void leftToBottom(EuclidianViewBounds bounds, Interval x, Interval y);
}
