package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.util.DoubleUtil;

public class IntervalRoot {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalRoot(IntervalNodeEvaluator evaluator) {

		this.evaluator = evaluator;
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, Interval other) {
		if (!other.isSingleton()) {
			return IntervalConstants.undefined();
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

			return evaluator.unionInvertedResults(compute(interval.extractLow(), n),
					compute(interval.extractHigh(), n));
		}

		double power = 1 / n;
		if (isPositiveOdd(n)) {
			return new Interval(oddFractionPower(interval.getLow(), power),
					oddFractionPower(interval.getHigh(), power));
		}
		return evaluator.pow(interval, power).round();
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
