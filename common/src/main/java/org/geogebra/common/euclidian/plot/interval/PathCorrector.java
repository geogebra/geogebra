package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTuple;

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
		if (this.lastY.isUndefined()) {
			this.lastY.setUndefined();
		} else if (tuple.y().isWhole()) {
			this.lastY.setUndefined();
		} else if (isInvertedAround(idx)) {
			drawInvertedInterval(idx);
		} else {
			drawInvertedInterval(idx);
			lastY.set(IntervalConstants.undefined());
		}

		return this.lastY;
	}

	/**
	 * Draws union of two disjunct intervals, comleting it to +/- infinity.
	 *
	 * @param idx index of the inverted tuple in model to draw.
	 */
	public void drawInvertedInterval(int idx) {
		Interval y = model.pointAt(idx).y();
		double low = y.getLow();
		double high = y.getHigh();

		if (Double.isFinite(low)) {

			if (Double.isFinite(high)) {
				drawFromNegativeInfinity(idx, low);
				drawFromPositiveInfinity(idx, high);
			} else {
				drawFromNegativeInfinityOnly(idx, low);
			}
		} else {
			drawFromPositiveInfinityOnly(idx, high);
		}
	}

	private boolean isInvertedAround(int idx) {
		return model.isInvertedAt(idx - 1) && model.isInvertedAt(idx + 1);
	}

	private void drawFromNegativeInfinity(int idx, double value) {
		if (value < bounds.getYmax()) {
			boolean ascendingAfter = model.isAscendingAfter(idx);
			double sValue = bounds.toScreenCoordYd(value);
			if (ascendingAfter) {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx + 1).x());
				gp.moveTo(sx.getHigh(), bounds.getHeight());
				gp.lineTo(sx.getHigh(), sValue);
			} else if (model.pointAt(idx - 1) != null) {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx - 1).x());
				gp.moveTo(sx.getLow(), bounds.getHeight());
				gp.lineTo(sx.getHigh(), sValue);
			}
			lastY.set(sValue, bounds.getHeight());
		}
	}

	private void drawFromPositiveInfinity(int idx, double value) {
		if (value > bounds.getYmin()) {
			boolean ascendingAfter = model.isAscendingAfter(idx);
			double sValue = bounds.toScreenCoordYd(value);
			if (ascendingAfter) {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx).x());
				gp.moveTo(sx.getLow(), 0);
				gp.lineTo(sx.getLow(), sValue);
			} else if (model.pointAt(idx + 1) != null) {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx + 1).x());
				gp.moveTo(sx.getLow(), 0);
				gp.lineTo(sx.getHigh(), sValue);
			}
			lastY.set(0, sValue);
		}
	}

	private void drawFromPositiveInfinityOnly(int idx, double value) {
		if (value > bounds.getYmin()) {
			double sValue = bounds.toScreenCoordYd(value);
			IntervalTuple current = model.pointAt(idx);
			IntervalTuple next = model.pointAt(idx + 1);
			Interval sx = bounds.toScreenIntervalX(
					next != null && !next.y().isUndefined()
							? next.x()
							: current.x());
			gp.moveTo(sx.getLow(), 0);
			gp.lineTo(sx.getLow(), sValue);
			lastY.set(0, sValue);
		}
	}

	private void drawFromNegativeInfinityOnly(int idx, double value) {
		if (value < bounds.getYmax()) {
			double sValue = bounds.toScreenCoordYd(value);
			IntervalTuple current = model.pointAt(idx);
			IntervalTuple next = model.pointAt(idx + 1);
			Interval sx = bounds.toScreenIntervalX(
					next != null && !next.y().isUndefined()
							? next.x()
							: current.x());
			gp.moveTo(sx.getLow(), bounds.getHeight());
			gp.lineTo(sx.getLow(), sValue);
			lastY.set(sValue, bounds.getHeight());
		}
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
		if (index + 1 >= model.pointCount()) {
			return IntervalConstants.undefined();
		}

		Interval nextY = bounds.toScreenIntervalY(model.pointAt(index + 1).y());
		if (model.isAscendingAfter(index)) {
			completeToNegativeInfinity(x, nextY);
		} else {
			completeToPositiveInfinity(x, nextY);
		}
		return lastY;
	}

	private void completeToPositiveInfinity(Interval x, Interval y) {
		gp.moveTo(x.getHigh(), 0);
		double y1 = Math.min(y.getLow(), bounds.getHeight());
		gp.lineTo(x.getHigh(), y1);
		lastY.set(0, y1);
	}

	private void completeToNegativeInfinity(Interval x, Interval y) {
		int yMax = bounds.getHeight();
		double y1 = Math.max(0, y.getHigh());
		gp.moveTo(x.getHigh(), yMax);
		gp.lineTo(x.getHigh(), y1);
		lastY.set(y.getHigh(), yMax);
	}
}
