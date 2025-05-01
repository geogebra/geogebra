package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HALF_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HIGH;

import org.geogebra.common.kernel.interval.Interval;

public class IntervalTrigonometric {

	/**
	 *
	 * @return arc sine of the interval
	 */
	public Interval asin(Interval interval) {
		if (interval.isUndefined() || interval.getHigh() < -1 || interval.getLow() > 1) {
			interval.setUndefined();
		} else {
			double low = interval.getLow() <= -1 ? -PI_HALF_HIGH
					: RMath.prev(Math.asin(interval.getLow()));
			double high = interval.getHigh() >= 1 ? PI_HALF_HIGH
					: RMath.prev(Math.asin(interval.getHigh()));
			interval.set(low, high);
		}

		return interval;
	}

	/**
	 *
	 * @return arc cosine of the interval
	 */
	public Interval acos(Interval interval) {
		if (interval.isUndefined() || interval.getHigh() < -1 || interval.getLow() > 1) {
			interval.setUndefined();
		} else {
			double low = interval.getHigh() >= 1 ? 0 : RMath.prev(Math.acos(interval.getHigh()));
			double high = interval.getLow() <= -1 ? PI_HIGH
					: RMath.next(Math.acos(interval.getLow()));
			interval.set(low, high);
		}
		return interval;
	}

	/**
	 *
	 * @return arc tangent of the interval
	 */
	public Interval atan(Interval interval) {
		if (!interval.isUndefined()) {
			interval.set(RMath.prev(Math.atan(interval.getLow())),
					RMath.next(Math.atan(interval.getHigh())));
		}
		return interval;
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public Interval sinh(Interval interval) {
		if (!interval.isUndefined()) {
			interval.set(RMath.prev(Math.sinh(interval.getLow())),
					RMath.next(Math.sinh(interval.getHigh())));
		}
		return interval;
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public Interval cosh(Interval interval) {
		if (interval.isUndefined()) {
			return interval;
		}

		if (!interval.isUndefined()) {
			double low = interval.getLow();
			double high = interval.getHigh();
			if (high < 0) {
				interval.set(RMath.prev(Math.cosh(high)), RMath.next(Math.cosh(low)));
			} else if (low >= 0) {
				interval.set(RMath.prev(Math.cosh(low)), RMath.next(Math.cosh(high)));
			} else {
				interval.set(1, RMath.next(Math.cosh(-low > high ? low : high)));
			}
		}

		return interval;
	}

	/**
	 *
	 * @return hyperbolic tangent of the interval
	 */
	public Interval tanh(Interval interval) {
		if (!interval.isUndefined()) {
			interval.set(RMath.prev(Math.tanh(interval.getLow())),
					RMath.next(Math.tanh(interval.getHigh())));
		}
		return interval;
	}
}