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
			extractAndDraw(tuple);
		} else {
			drawFromNegativeInfinity(idx);
			lastY.set(IntervalConstants.undefined());
		}

		return this.lastY;
	}

	private void drawFromNegativeInfinity(int idx) {
		IntervalTuple prev = model.pointAt(idx - 1);
		IntervalTuple tuple = model.pointAt(idx);
		drawFromNegativeInfinity0(idx, tuple.y().getLow());
	}

	private boolean isInvertedAround(int idx) {
		return model.isInvertedAt(idx - 1) && model.isInvertedAt(idx + 1);
	}

	public void extractAndDraw(IntervalTuple tuple) {
		Interval extractLow = tuple.y().extractLow();
		Interval extractHigh = tuple.y().extractHigh();
		drawFromNegativeInfinity(tuple.x(), extractLow.getHigh());
		drawFromPositiveInfinity(tuple.x(), extractHigh.getLow());
	}

	private void drawFromNegativeInfinity(Interval x, double value) {
		if (value < bounds.getYmax()) {
			Interval sx = bounds.toScreenIntervalX(x);
			double sValue = bounds.toScreenCoordYd(value);
			gp.moveTo(sx.getLow(), bounds.getHeight());
			gp.lineTo(sx.getHigh(), sValue);
			lastY.set(sValue, bounds.getHeight());
		}
	}

	private void drawFromNegativeInfinity0(int idx, double value) {
		if (value < bounds.getYmax()) {
			boolean ascendingAfter = model.isAscendingAfter(idx);
			Interval x = ascendingAfter ? model.pointAt(idx - 1).x()
					: model.pointAt(idx).x();
			double sValue = bounds.toScreenCoordYd(value);
			if (ascendingAfter) {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx + 1).x());
				gp.moveTo(sx.getHigh(), bounds.getHeight());
				gp.lineTo(sx.getHigh(), sValue);
			} else {
				Interval sx = bounds.toScreenIntervalX(model.pointAt(idx - 1).x());
				gp.moveTo(sx.getLow(), bounds.getHeight());
				gp.lineTo(sx.getHigh(), sValue);
			}
			lastY.set(sValue, bounds.getHeight());
		}
	}

	private void drawFromPositiveInfinity(Interval x, double value) {
		if (value > bounds.getYmin()) {
			Interval sx = bounds.toScreenIntervalX(x);
			double sValue = bounds.toScreenCoordYd(value);
			gp.moveTo(sx.getLow(), 0);
			gp.lineTo(sx.getLow(), sValue);
			lastY.set(0, sValue);
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
