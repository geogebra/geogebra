package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.DoubleUtil;

/**
 * Joining lines based on tuples in the correct way
 */
public class JoinLines {
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
		double leftDiff = neighbours.hasLeft()
				? Math.abs(neighbours.currentYLow() - neighbours.leftYLow())
				: Double.NEGATIVE_INFINITY;
		double rightDiff = neighbours.hasRight()
				? Math.abs(neighbours.currentYLow() - neighbours.rightYLow())
				: Double.NEGATIVE_INFINITY;
		if (DoubleUtil.isEqual(0, leftDiff, Kernel.MAX_PRECISION)) {
			toBottomCurrentXLow(neighbours);
		} else if (DoubleUtil.isEqual(0, rightDiff, Kernel.MAX_PRECISION)) {
			toBottomCurrentXHigh(neighbours);
		} else
			if (leftDiff < rightDiff) {
			toBottomLeft(neighbours);
		} else {
			toBottomRight(neighbours);
		}
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
		gp.segment(bounds, neighbours.currentXLow(), neighbours.leftYHigh(),
				neighbours.currentXLow(), bounds.getYmax());

	}

	private void toTopCurrentXHigh(TupleNeighbours neighbours) {
		gp.segment(bounds, neighbours.rightXHigh(), neighbours.rightYHigh(),
				neighbours.currentXHigh(), bounds.getYmax());

	}

	private void toBottomLeft(TupleNeighbours neighbours) {
		gp.segment(bounds, neighbours.rightXHigh(), neighbours.currentYLow(),
				neighbours.currentXHigh(), bounds.getYmin());
	}

	private void toBottomRight(TupleNeighbours neighbours) {
		gp.segment(bounds, neighbours.currentXLow(), neighbours.currentYLow(),
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
				gp.segment(bounds, neighbours.currentXLow(), neighbours.leftYLow(),
						neighbours.currentXLow(), bounds.getYmin());
			} else {
				toBottom(neighbours);
			}
		}
	}
}
