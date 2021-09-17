package org.geogebra.common.kernel.interval;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

public class IntervalRoot {
	@Weak
	private Interval interval;

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

		double power = 1/n;
		double low = interval.getLow();
		double signLow = Math.signum(low);
		double high = interval.getHigh();
		double signHigh = Math.signum(high);
		if (isPositiveOdd(n)) {
			interval.set(pow(abs(low), power) * signLow,
					pow(abs(high), power) * signHigh);
		} else if (high < 0) {
			interval.setEmpty();
			return interval;
		}

		interval.set(pow(Math.max(0, low), power),
				pow(high, power));
		return interval;
	}

	private boolean isPositiveOdd(double n) {
		return n > 0 && DoubleUtil.isInteger(n) && (int) n % 2 == 1;
	}
}
