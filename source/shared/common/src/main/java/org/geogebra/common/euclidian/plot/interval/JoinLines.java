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

package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.DoubleUtil;

/**
 * Joining lines based on tuples in the correct way
 */
public class JoinLines {
	public static final int VERTICAL_MARGIN = 5;
	private final EuclidianViewBounds bounds;
	private final IntervalPathPlotter gp;
	private static final double INFINITY_DISPLAYED = Kernel.INV_MAX_DOUBLE_PRECISION;

	/**
	 *
	 * @param bounds {@link EuclidianViewBounds}
	 * @param gp {@link IntervalPathPlotter}
	 */
	public JoinLines(EuclidianViewBounds bounds, IntervalPathPlotter gp) {
		this.bounds = bounds;
		this.gp = gp;
	}

	/**
	 * Extend the plot defined by neighbour tuples to the top of screen.
	 * @param neighbours {@link TupleNeighbours}
	 */
	public void toTop(TupleNeighbours neighbours) {
		double leftDiff = neighbours.hasLeft()
				? Math.abs(neighbours.currentYHigh() - neighbours.leftYHigh())
				: Double.POSITIVE_INFINITY;
		double rightDiff = neighbours.hasRight()
				? Math.abs(neighbours.currentYHigh() - neighbours.rightYHigh())
				: Double.POSITIVE_INFINITY;
		if (DoubleUtil.isEqual(0, leftDiff, Kernel.MAX_PRECISION)) {
			toTopCurrentXLow(neighbours);
		} else if (DoubleUtil.isEqual(0, rightDiff, Kernel.MAX_PRECISION)) {
			toTopCurrentXHigh(neighbours);
		} else
		if (leftDiff < rightDiff) {
			toTopLeft(neighbours);
		} else {
			topToRight(neighbours);
		}
	}

	private void toTopLeft(TupleNeighbours neighbours) {
		topTo(neighbours.rightXHigh(), neighbours.leftXHigh(), neighbours.currentYHigh());
	}

	private void topToRight(TupleNeighbours neighbours) {
		double y = neighbours.currentYHigh();
		if (y < bounds.getYmax()) {
			gp.segment(bounds, neighbours.rightXHigh(), bounds.getYmax(),
					neighbours.rightXHigh(), y);
		}
	}

	private void topTo(double x1, double x2, double y) {
		if (y < bounds.getYmax()) {
			gp.segment(bounds, x1, bounds.getYmax(), x2, y);
		}
	}

	/**
	 * Extend the plot defined by neighbour tuples to the bottom of screen.
	 * @param neighbours {@link TupleNeighbours}
	 */
	public void toBottom(TupleNeighbours neighbours) {
		double yLeftDiff = getLeftScreenDifference(neighbours);
		double yRightDiff = getRightScreenDifference(neighbours);

		if (DoubleUtil.isEqual(0, yLeftDiff)) {
			toBottomCurrentXLow(neighbours);
		} else if (DoubleUtil.isEqual(0, yRightDiff)) {
			toBottomCurrentXHigh(neighbours);
		} else if (yLeftDiff < yRightDiff) {
			toBottomLeft(neighbours);
		} else {
			toBottomRight(neighbours);
		}
	}

	private double getLeftScreenDifference(TupleNeighbours neighbours) {
		if (!neighbours.hasLeft()) {
			return -1;
		}

		double diff = Math.abs(neighbours.currentYLow() - neighbours.leftYLow());
		return bounds.toScreenCoordYd(diff);
	}

	private double getRightScreenDifference(TupleNeighbours neighbours) {
		if (!neighbours.hasRight()) {
			return -1;
		}

		double diff = Math.abs(neighbours.currentYLow() - neighbours.rightYLow());
		return bounds.toScreenCoordYd(diff);
	}

	private void toBottomCurrentXLow(TupleNeighbours neighbours) {
		gp.segment(bounds, neighbours.currentXLow(), neighbours.currentYLow(),
				neighbours.currentXLow(), bounds.getYmin());

	}

	private void toBottomCurrentXHigh(TupleNeighbours neighbours) {
		gp.segment(bounds, neighbours.rightXHigh(), neighbours.currentYLow(),
				neighbours.currentXHigh(), bounds.getYmin());

	}

	private void toTopCurrentXLow(TupleNeighbours neighbours) {
		double y = neighbours.leftYHigh();
		if (isOffScreenTop(y)) {
			return;
		}

		gp.segment(bounds, neighbours.currentXLow(), y,
				neighbours.currentXLow(), bounds.getYmax());

	}

	private boolean isOffScreenTop(double y) {
		return y > bounds.getYmax() + VERTICAL_MARGIN;
	}

	private void toTopCurrentXHigh(TupleNeighbours neighbours) {
		double y = neighbours.rightYHigh();
		if (isOffScreenTop(y)) {
			return;
		}

		gp.segment(bounds, neighbours.rightXHigh(), y,
				neighbours.currentXHigh(), bounds.getYmax());

	}

	private void toBottomLeft(TupleNeighbours neighbours) {
		double y = neighbours.currentYLow();
		if (isOffScreenBottom(y)) {
			return;
		}

		gp.segment(bounds, neighbours.rightXHigh(), y,
				neighbours.currentXHigh(), bounds.getYmin());
	}

	private boolean isOffScreenBottom(double y) {
		return y < bounds.getYmin() - VERTICAL_MARGIN;
	}

	private void toBottomRight(TupleNeighbours neighbours) {
		double y = neighbours.currentYLow();
		if (isOffScreenBottom(y)) {
			return;
		}

		gp.segment(bounds, neighbours.currentXLow(), y,
				neighbours.currentXHigh(), bounds.getYmin());
	}

	/**
	 * Draws the inverted tuple
	 * @param neighbours of the inverted tuple and itself as current.
	 */
	public void inverted(TupleNeighbours neighbours) {
		if (neighbours.currentYHigh() < INFINITY_DISPLAYED) {
			if (!neighbours.isLeftInfinite()) {
				toTop(neighbours);
			}
		}

		if (neighbours.currentYLow() > -INFINITY_DISPLAYED) {
			if (neighbours.isRightInfinite()) {
				double y1 = neighbours.hasLeft() ? neighbours.leftYLow() : neighbours.currentYLow();
				gp.segment(bounds, neighbours.currentXLow(), y1,
						neighbours.currentXLow(), bounds.getYmin());
			} else {
				toBottom(neighbours);
			}
		}
	}
}
