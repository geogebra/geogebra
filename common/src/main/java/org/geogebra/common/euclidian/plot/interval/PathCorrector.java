package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class to correct interval path at limits
 */
public class PathCorrector {
	private final IntervalPathPlotter gp;
	private final IntervalPlotModel model;
	private final EuclidianViewBounds bounds;
	private Interval lastY = new Interval();

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param model {@link IntervalPlotModel}
	 */
	public PathCorrector(IntervalPathPlotter gp, IntervalPlotModel model,
			EuclidianViewBounds bounds) {
		this.gp = gp;
		this.model = model;
		this.bounds = bounds;
	}

	/**
	 * Coplete inverted interval on demand.
	 *
	 * @param idx tuple index in model
	 * @param y inverted value to handle.
	 * @return the last y interval.
	 */
	public Interval handleInvertedInterval(int idx,
			Interval y) {
		lastY = y;
		IntervalTuple tuple = model.pointAt(idx);
		if (this.lastY.isEmpty()) {
			this.lastY.setEmpty();
		} else if (tuple.y().isRealWhole()) {
			this.lastY.setEmpty();
		} else if (isInvertedAround(idx)) {
			handleFill(tuple);
		} else if (bounds.isOnView(tuple.y())) {
			completePathAt(idx);
		} else {
			this.lastY.setEmpty();
		}
		return this.lastY;
	}

	private void handleFill(IntervalTuple tuple) {
		Interval sx = toScreenX(tuple);
		Interval sy = toScreenY(tuple);
		gp.moveTo(sx.getLow(), 0);
		gp.lineTo(sx.getHigh(), sy.getLow());
		Interval y = tuple.y();
		if (y.containsExclusive(0)) {
			gp.moveTo(sx.getLow(), bounds.getHeight());
			gp.lineTo(sx.getLow(), sy.getHigh());
		}
		lastY.set(sy);
	}

	private boolean isInvertedAround(int idx) {
		return (model.isInvertedAt(idx - 1) && model.isInvertedAt(idx + 1));
	}

	private void completePathAt(int idx) {
		IntervalTuple tuple = model.pointAt(idx);
		boolean before = model.isAscendingBefore(idx);
		boolean after = model.isAscendingAfter(idx);
		completePathFromLeft(tuple, before);
		if (hasPointNextTo(idx) && before == after) {
			Interval other = completePathFromRight(tuple, model.isAscendingBefore(idx));
			lastY.set(other);
		} else {
			lastY.setEmpty();
		}
	}

	private boolean hasPointNextTo(int idx) {
		return !model.isEmptyAt(idx + 1);
	}

	private void completePathFromLeft(IntervalTuple point, boolean ascending) {
		Interval x = toScreenX(point);
		Interval y = toScreenY(point);
		double xMiddle = x.getLow() + (x .getWidth() / 2);
		double yLow = y.getLow() < bounds.getHeight()
				? Math.max(0, y.getLow())
				: bounds.getHeight();

		if (ascending && yLow >= 0) {
			gp.lineTo(xMiddle, 0);
		} else if (lastY.isInverted()) {
			gp.lineTo(xMiddle, lastY.getLow());
		} else if (!DoubleUtil.isEqual(lastY.getLow(), yLow) && yLow < bounds.getHeight()) {
			gp.lineTo(xMiddle, bounds.getHeight());
		}
	}

	private Interval toScreenY(IntervalTuple point) {
		return bounds.toScreenIntervalY(point.y());
	}

	private Interval toScreenX(IntervalTuple point) {
		return bounds.toScreenIntervalX(point.x());
	}

	private Interval completePathFromRight(IntervalTuple point, boolean ascending) {
		Interval x = toScreenX(point);
		Interval y = toScreenY(point);
		double xMiddle = x.getLow() + (x.getWidth() / 2);
		double yLow = y.getLow() < bounds.getHeight()
				? Math.max(0, y.getLow())
				: bounds.getHeight();
		if (ascending) {
			if (y.getHigh() > 0) {
				gp.moveTo(xMiddle, bounds.getHeight());
				return new Interval(bounds.getHeight());
			}
		} else if (yLow < bounds.getHeight()) {
			gp.moveTo(xMiddle, 0);
			gp.lineTo(x.getHigh(), yLow);
			return new Interval(yLow);
		}
		return IntervalConstants.empty();
	}

	/**
	 * Begins drawing the function (for the first time or after empty tuple)
	 * from infinity.
	 *
	 * @param index of tuple to draw
	 * @param x interval
	 * @param y interval
	 * @return the last y value needed to continue drawing.
	 */
	public Interval beginFromInfinity(int index, Interval x, Interval y) {
		if (model.isAscendingAfter(index)) {
			completeToNegativeInfinity(x, y);
		} else {
			completeToPositiveInfinity(x, y);
		}
		return lastY;
	}

	private void completeToPositiveInfinity(Interval x, Interval y) {
		gp.moveTo(x.getHigh(), 0);
		gp.lineTo(x.getHigh(), y.getLow());
		lastY.set(0, y.getLow());
	}

	private void completeToNegativeInfinity(Interval x, Interval y) {
		int yMax = bounds.getHeight();
		gp.moveTo(x.getHigh(), yMax);
		gp.lineTo(x.getHigh(), y.getHigh());
		lastY.set(y.getHigh(), yMax);
	}
}
