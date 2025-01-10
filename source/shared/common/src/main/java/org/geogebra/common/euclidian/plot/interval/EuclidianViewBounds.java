package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;

public interface EuclidianViewBounds {

	/**
	 * @return bounds width.
	 */
	int getWidth();

	/**
	 * @return bounds width.
	 */
	int getHeight();

	/**
	 *
	 * @return the interval of [xmin, xmax]
	 */
	Interval domain();

	/**
	 *
	 * @return the interval of [ymin, ymax]
	 */
	Interval range();

	/**
	 *
	 * @return the lowest x value in bounds.
	 */
	double getXmin();

	/**
	 *
	 * @return the highest x value in bounds.
	 */
	double getXmax();

	/**
	 *
	 * @return the lowest y value in bounds.
	 */
	double getYmin();

	/**
	 *
	 * @return the highest y value in bounds.
	 */
	double getYmax();

	/**
	 *  Converts interval of real world x coordinates
	 *  to interval of screen x coordinates.
	 * @param x interval of real world coordinates
	 * @return interval of screen x coordinates.
	 */
	Interval toScreenIntervalX(Interval x);

	/**
	 *  Converts interval of real world y coordinates
	 *  to interval of screen y coordinates.
	 * @param y interval of real world coordinates
	 * @return interval of screen y coordinates.
	 */
	Interval toScreenIntervalY(Interval y);

	/**
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @return if (x, y) within the bounds.
	 */
	boolean isOnView(double x, double y);

	/**
	 * Converts x from real world to screen coordinate.
	 * @param x real world coordinate
	 * @return as screen coordinate.
	 */
	double toScreenCoordXd(double x);

	/**
	 * Converts y from real world to screen coordinate.
	 * @param y real world coordinate
	 * @return as screen coordinate.
	 */
	double toScreenCoordYd(double y);

	/**
	 * Converts x from screen to real world coordinate.
	 *
	 * @param x screen coordinate
	 * @return as real world coordinate.
	 */
	double toRealWorldCoordX(double x);

	/**
	 * Converts y from screen to real world coordinate.
	 *
	 * @param y screen coordinate
	 * @return as real world coordinate.
	 */
	double toRealWorldCoordY(double y);

	/**
	 *
	 * @param y interval to check.
	 * @return if interval y is within the bounds.
	 */
	boolean isOnView(Interval y);

	/**
	 * @return 1/getYScale()
	 */
	double getInvYscale();
}
