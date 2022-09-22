package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

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
				: Double.POSITIVE_INFINITY;
		double rightDiff = neighbours.hasRight()
				? Math.abs(neighbours.currentYLow() - neighbours.rightYLow())
				: Double.POSITIVE_INFINITY;
		double eps = 2*neighbours.current().x().getLength();
		Log.debug("n: " + neighbours + " leftDiff: " + leftDiff + " rightDiff: " + rightDiff
				+ "eps: " + eps);
		if (DoubleUtil.isEqual(0, leftDiff, eps)) {
			toStraightBottom1(neighbours);
		} else if (DoubleUtil.isEqual(0, rightDiff, eps)) {
			toStraightBottom2(neighbours);
		} else if (leftDiff < rightDiff) {
			toBottomLeft(neighbours);
		} else {
			Log.debug(neighbours.toString());
			toBottomRight(neighbours);
		}
	}

	private void toStraightBottom1(TupleNeighbours neighbours) {
		Log.debug("bottom1");
		gp.segment(bounds, neighbours.currentXLow(), neighbours.currentYLow(),
				neighbours.currentXLow(), bounds.getYmin());

	}

	private void toStraightBottom2(TupleNeighbours neighbours) {
		Log.debug("bottom2");
		gp.segment(bounds, neighbours.rightXHigh(), neighbours.currentYLow(),
				neighbours.currentXHigh(), bounds.getYmin());

	}

	private void toBottomLeft(TupleNeighbours neighbours) {
		Log.debug("bottomLeft");
		gp.segment(bounds, neighbours.rightXHigh(), neighbours.currentYLow(),
				neighbours.currentXHigh(), bounds.getYmin());
	}

	private void toBottomRight(TupleNeighbours neighbours) {
		Log.debug("bottomRight");
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
