package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.computeInverted;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.pow;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.util.DoubleUtil;

public class IntervalRoot {

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, Interval other) {
		if (!other.isSingleton()) {
			interval.setUndefined();
			return interval;
		}

		double power = other.getLow();
		return compute(interval, power);
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, double n) {
		if (interval.isUndefined()) {
			return undefined();
		}

		if (interval.isInverted()) {
			if (isOdd(n)) {
				return compute(interval.uninvert(), n).invert();
			}
			Interval result1 = compute(interval.extractLow(), n);
			Interval result2 = compute(interval.extractHigh(), n);
			Interval result = computeInverted(result1, result2);
			return result;
		}

		double power = 1 / n;
		if (isPositiveOdd(n)) {
			return new Interval(oddFractionPower(interval.getLow(), power),
					oddFractionPower(interval.getHigh(), power));
		}
		return pow(interval, power).round();
	}

	private double oddFractionPower(double x, double power) {
		double fractionPower = Math.pow(Math.abs(x), power);
		return x > 0
				? Math.max(IntervalConstants.PRECISION, fractionPower)
				: Math.min(-IntervalConstants.PRECISION, -fractionPower);
	}

	private boolean isPositiveOdd(double n) {
		return n > 0 && isOdd(n);
	}

	private boolean isOdd(double n) {
		return DoubleUtil.isInteger(n) && ((int) n) % 2 != 0;
	}
}
