package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HALF_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HALF_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;

import com.google.j2objc.annotations.Weak;

class IntervalTrigonometric {
	@Weak
	private Interval interval;

	IntervalTrigonometric(Interval interval) {
		this.interval = interval;
	}

	Interval cos() {
		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isEmpty() || interval.isOnlyInfinity()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isWhole()) {
			interval.set(-1, 1);
			return interval;
		}

		Interval cache = new Interval(interval);
		handleNegative(cache);

		Interval pi = IntervalConstants.pi();
		Interval pi2 = IntervalConstants.piTwice();
		cache.fmod(pi2);
		if (cache.getWidth() >= PI_TWICE_LOW) {
			interval.set(-1, 1);
			return interval;
		}

		if (cache.getLow() >= PI_HIGH) {
			cache.subtract(pi).cos();
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
	public Interval sin() {
		if (interval.isUndefined()) {
			return interval;
		} if (interval.isWhole()) {
			interval.set(-1, 1);
		} else if (interval.isEmpty() || interval.isOnlyInfinity()) {
			interval.setEmpty();
		} else {
			interval.subtract(IntervalConstants.piHalf()).cos();
		}
		return interval;
	}

	/**
	 *
	 * @return secant of the interval
	 */
	public Interval sec() {
		Interval interval = new Interval(this.interval);
		return interval.cos().multiplicativeInverse();
	}

	/**
	 *
	 * @return cotangent of the interval
	 */
	public Interval cot() {
		Interval interval = new Interval(this.interval);
		return interval.tan().multiplicativeInverse();
	}

	/**
	 *
	 * @return tangent of the interval.
	 */
	public Interval tan() {
		if (interval.isEmpty() || interval.isOnlyInfinity()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isWhole()) {
			interval.setUndefined();
			return interval;
		}

		Interval cache = new Interval(interval);
		handleNegative(cache);
		cache.fmod(IntervalConstants.pi());

		if (cache.getLow() >= PI_HALF_LOW) {
			cache.subtract(IntervalConstants.pi());
		}

		if (cache.getLow() <= -PI_HALF_LOW || cache.getHigh() >= PI_HALF_LOW) {
			interval.setWhole();
		} else {
			interval.set(RMath.tanLow(cache.getLow()), RMath.tanHigh(cache.getHigh()));
		}
		return interval;
	}

	/**
	 *
	 * @return arc sine of the interval
	 */
	public Interval asin() {
		if (interval.isEmpty() || interval.getHigh() < -1 || interval.getLow() > 1) {
			interval.setEmpty();
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
	public Interval acos() {
		if (interval.isEmpty() || interval.getHigh() < -1 || interval.getLow() > 1) {
			interval.setEmpty();
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
	public Interval atan() {
		if (!interval.isEmpty()) {
			interval.set(RMath.atanLow(interval.getLow()), RMath.atanHigh(interval.getHigh()));
		}
		return interval;
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public Interval sinh() {
		if (!interval.isEmpty()) {
  			interval.set(RMath.sinhLow(interval.getLow()), RMath.sinhHigh(interval.getHigh()));
		}
		return interval;
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public Interval cosh() {
		if (interval.isUndefined()) {
			return interval;
		}

		if (!interval.isEmpty()) {
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
	public Interval tanh() {
		if (!interval.isEmpty()) {
			interval.set(RMath.tanhLow(interval.getLow()), RMath.tanhHigh(interval.getHigh()));
		}
		return interval;
	}

	/**
	 *
	 * @return 1 / sin(x)
	 */
	public Interval csc() {
		Interval interval = new Interval(this.interval);
		return interval.sin().multiplicativeInverse();
	}
}
