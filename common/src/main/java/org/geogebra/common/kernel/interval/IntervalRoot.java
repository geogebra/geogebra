package org.geogebra.common.kernel.interval;

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
		if (interval.isEmpty() || interval.isNegative()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isZero()) {
			return interval;
		}

		if (interval.isWhole()) {
			return IntervalConstants.wholeDouble();
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
