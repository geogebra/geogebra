package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HALF_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalOperands.fmod;

public class IntervalTrigonometric {

	Interval cos(Interval interval) {
		if (interval.isInverted()) {
			Interval result1 = cos(interval.extractLow());
			Interval result2 = cos(interval.extractHigh());
			Interval result = IntervalOperands.computeInverted(result1, result2);
			return result.isUndefined() ? new Interval(-1, 1) : result;
		}
		return cos0(interval);
	}

	Interval cos0(Interval interval) {
		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isUndefined() || interval.isInfiniteSingleton()) {
			setDefaultInterval(interval);
			return interval;
		}

		Interval cache = new Interval(interval);
		handleNegative(cache);

		Interval pi = IntervalConstants.pi();
		Interval pi2 = IntervalConstants.piTwice();
		fmod(cache, pi2);
		if (cache.getWidth() >= PI_TWICE_LOW) {
			interval.set(-1, 1);
			return interval;
		}

		if (cache.getLow() >= PI_HIGH) {
			IntervalOperands.cos(cache.subtract(pi));
			cache.negative();
			interval.set(cache);
			return interval;
		}

		double low = cache.getLow();
		double high = cache.getHigh();
  		double rlo = RMath.cosLow(high);
  		double rhi = RMath.cosHigh(low);
		// it's ensured that t.lo < pi and that t.lo >= 0
		if (high <= PI_LOW) {
			// when t.hi < pi
			// [cos(t.lo), cos(t.hi)]
			interval.set(rlo, rhi);
		} else if (high <= PI_TWICE_LOW) {
			// when t.hi < 2pi
			// [-1, max(cos(t.lo), cos(t.hi))]
			interval.set(-1, Math.max(rlo, rhi));
		} else {
			// t.lo < pi and t.hi > 2pi
			interval.set(-1, 1);
		}

		return interval;
	}

	private static void handleNegative(Interval interval) {
		double low = interval.getLow();
		double high = interval.getHigh();
		if (low < 0) {
			if (low == Double.NEGATIVE_INFINITY) {
				interval.set(0, Double.POSITIVE_INFINITY);
			} else {
				double n = Math.ceil(-low / PI_TWICE_LOW);
				interval.set(low + PI_TWICE_LOW * n,
						high + PI_TWICE_LOW * n);
			}
		}
	}

	/**
	 *
	 * @return sine of the interval
	 */
	public Interval sin(Interval interval) {
		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isInverted()) {
			setDefaultInterval(interval);
		} else if (interval.isUndefined() || interval.isInfiniteSingleton()) {
			interval.setUndefined();
		} else {
			IntervalOperands.cos(interval.subtract(IntervalConstants.piHalf()));
		}
		return interval;
	}

	private void setDefaultInterval(Interval interval) {
		interval.set(-1, 1);
		interval.setInverted(false);
	}

	/**
	 *
	 * @return arc sine of the interval
	 */
	public Interval asin(Interval interval) {
		if (interval.isUndefined() || interval.getHigh() < -1 || interval.getLow() > 1) {
			interval.setUndefined();
		} else {
			double low = interval.getLow() <= -1 ? -PI_HALF_HIGH
					: RMath.asinLow(interval.getLow());
			double high = interval.getHigh() >= 1 ? PI_HALF_HIGH
					: RMath.asinHigh(interval.getHigh());
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
			double low = interval.getHigh() >= 1 ? 0 : RMath.acosLow(interval.getHigh());
			double high = interval.getLow() <= -1 ? PI_HIGH : RMath.acosHigh(interval.getLow());
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
			interval.set(RMath.atanLow(interval.getLow()), RMath.atanHigh(interval.getHigh()));
		}
		return interval;
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public Interval sinh(Interval interval) {
		if (!interval.isUndefined()) {
  			interval.set(RMath.sinhLow(interval.getLow()), RMath.sinhHigh(interval.getHigh()));
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
				interval.set(RMath.coshLow(high), RMath.coshHigh(low));
			} else if (low >= 0) {
				interval.set(RMath.coshLow(low), RMath.coshHigh(high));
			} else {
				interval.set(1, RMath.coshHigh(-low > high ? low : high));
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
			interval.set(RMath.tanhLow(interval.getLow()), RMath.tanhHigh(interval.getHigh()));
		}
		return interval;
	}
}