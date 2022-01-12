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
			extractAndDraw(tuple);
			lastY.setUndefined();
		}

		return this.lastY;
	}

	private boolean isInvertedAround(int idx) {
		return model.isInvertedAt(idx - 1) && model.isInvertedAt(idx + 1);
	}

	public void extractAndDraw(IntervalTuple tuple) {
		Interval extractLow = tuple.y().extractLow();
		Interval extractHigh = tuple.y().extractHigh();
		drawHigh(tuple.x(), extractLow.getHigh());
		drawLow(tuple.x(), extractHigh.getLow());
	}

	private void drawLow(Interval x, double value) {
		if (value > bounds.getYmin()) {
			Interval sx = bounds.toScreenIntervalX(x);
			double sValue = bounds.toScreenCoordYd(value);
			gp.moveTo(sx.getLow(), 0);
			gp.lineTo(sx.getLow(), sValue);
		}
	}

	private void drawHigh(Interval x, double value) {
		if (value < bounds.getYmax()) {
			Interval sx = bounds.toScreenIntervalX(x);
			double sValue = bounds.toScreenCoordYd(value);
			gp.moveTo(sx.getHigh(), bounds.getHeight());
			gp.lineTo(sx.getHigh(), sValue);
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
