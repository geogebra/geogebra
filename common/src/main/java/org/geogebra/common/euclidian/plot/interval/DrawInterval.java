package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.util.DoubleUtil;

public class DrawInterval {

	private boolean joinToPrevious;
	private IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;

	/**
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 */
	public DrawInterval(IntervalPathPlotter gp, EuclidianViewBounds bounds) {
		this.gp = gp;
		this.bounds = bounds;
	}

	public void setJoinToPrevious(boolean joinToPrevious) {
		this.joinToPrevious = joinToPrevious;
	}

	/**
	 * Draw interval (x, y) joined to the last y value
	 * @param lastY the last y value to join to.
	 * @param x interval
	 * @param y interval
	 */
	public void drawJoined(Interval lastY, Interval x, Interval y) {
		if (y.isGreaterThan(lastY)) {
			drawUp(x, y);
		} else {
			drawDown(x, y);
		}
	}

	void drawUp(Interval x, Interval y) {
		if (joinToPrevious) {
			lineTo(x.getLow(), y.getLow());
		} else {
			moveTo(x.getLow(), y.getLow());
		}

		lineTo(x.getHigh(), y.getHigh());
	}

	void drawDown(Interval x, Interval y) {
		if (joinToPrevious) {
			lineTo(x.getLow(), y.getHigh());
		} else {
			moveTo(x.getLow(), y.getHigh());
		}

		lineTo(x.getHigh(), y.getLow());
	}

	private void moveTo(double low, double high) {
		gp.moveTo(clamp(low), clamp(high));
	}

	void lineTo(double low, double high) {
		gp.lineTo(clamp(low), clamp(high));
	}

	private double clamp(double value) {
		if (DoubleUtil.isEqual(value, Double.POSITIVE_INFINITY)) {
			return IntervalPath.CLAMPED_INFINITY;
		}

		if (DoubleUtil.isEqual(value, Double.NEGATIVE_INFINITY)) {
			return -IntervalPath.CLAMPED_INFINITY;
		}
		return value;
	}

	/**
	 * Draws a whole interval (from screen top to bottom) at x.low
	 * @param x interval
	 */
	public void drawWhole(Interval x) {
		gp.segment(bounds,
				x.getLow(), bounds.getYmin(), x.getLow(), bounds.getYmax());
	}

	Interval drawIndependent(IntervalTuple tuple) {
		Interval x = bounds.toScreenIntervalX(tuple.x());
		Interval y = bounds.toScreenIntervalY(tuple.y());

		if (y.isUndefined()) {
			return IntervalConstants.undefined();
		} else {
			line(x, y);
			return y;
		}
	}

	/**
	 * Draws straight vertical line at x.high of interval y.
	 *
	 * @param x interval
	 * @param y interval
	 */
	public void line(Interval x, Interval y) {
		moveTo(x.getHigh(), y.getLow());
		lineTo(x.getHigh(), y.getHigh());
	}
}
