package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.wholeR;

import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

public class IntervalRoot {
	@Weak
	private final Interval interval;

	public IntervalRoot(Interval interval) {
		this.interval = interval;
	}

	/**
	 * @return square root of the interval.
	 */
	Interval sqrt() {
		if (interval.isEmpty() || interval.isNegative() || interval.isUndefined()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.hasZero()) {
			if (interval.getLow() < 0) {
				interval.set(0, oddFractionPower(interval.getHigh(), 0.5));
				return interval;
			}
			return IntervalConstants.zero();
		}

		if (interval.isWhole()) {
			return interval.isInverted() ? undefined() : wholeR();
		}

		return nRoot(2);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval nRoot(Interval other) {
		if (!other.isSingleton()) {
			interval.setEmpty();
			return interval;
		}

		return nRoot(other.getLow());
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval nRoot(double n) {
		if (interval.isEmpty()) {
			return interval;
		}
		double power = 1 / n;
		if (isPositiveOdd(n)) {
			return new Interval(oddFractionPower(interval.getLow(), power),
					oddFractionPower(interval.getHigh(), power));
		}
		return interval.pow(power);
	}

	private double oddFractionPower(double x, double power) {
		return Math.signum(x) * Math.pow(Math.abs(x), power);
	}

	private boolean isPositiveOdd(double n) {
		return n > 0 && DoubleUtil.isInteger(n) && (int) n % 2 != 0;
	}
}
