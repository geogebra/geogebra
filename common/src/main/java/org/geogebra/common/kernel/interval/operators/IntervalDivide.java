package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.union;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class IntervalDivide {

	/**
	 * Divide intervals.
	 *
	 * @param divisor interval to divide by.
	 * @return the result of interval divided by divisor.
	 */
	public Interval compute(Interval numerator, Interval divisor) {
		if (divisor.isInverted()) {
			Interval result1 = divide(numerator, divisor.extractLow());
			Interval result2 = divide(numerator, divisor.extractHigh());
			return union(result1, result2);
		}

		return divide(numerator, divisor);
	}

	private Interval divide(Interval numerator, Interval divisor) {
		if (divisor.isZero()) {
			return IntervalConstants.undefined();
		}

		if (isZeroByZero(numerator, divisor) || isWholeByNonZero(numerator, divisor)) {
			return whole();
		}

		if (numerator.isUndefined() || divisor.isUndefined()) {
			return undefined();
		}

		if (numerator.isPositiveInfinity()) {
			return divideSingletonPositiveInfinity(divisor);
		}

		if (numerator.isNegative()) {
			return divideNegativeBy(numerator, divisor);
		} else if (numerator.isPositive()) {
			return dividePositiveBy(numerator, divisor);
		}

		return divideMixedBy(numerator, divisor);
	}

	private Interval divideSingletonPositiveInfinity(Interval divisor) {
		if (divisor.isPositiveInfinity() || divisor.isNegativeInfinity()) {
			return zero();
		}

		if (divisor.isPositive()) {
			return positiveInfinity();
		} else if (divisor.isNegative()) {
			return negativeInfinity();
		}

		return whole();
	}

	private boolean isWholeByNonZero(Interval numerator, Interval divisor) {
		return numerator.isWhole() && !divisor.hasZero();
	}

	private Interval divideNegativeBy(Interval numerator, Interval divisor) {
		if (divisor.isZero()) {
			return numerator.isSingleton() ? negativeInfinity() : undefined();
		}

		if (divisor.isNegative()) {
			return divideNegativeByNegative(numerator, divisor);
		}

		if (divisor.highEquals(0)) {
			return divideNegativeByNegativeWithZeroAsHigh(prev(numerator.getHigh()
					/ divisor.getLow()));
		}

		if (hasZeroInBetween(divisor)) {
			return new Interval(next(numerator.getHigh() / divisor.getHigh()),
					prev(numerator.getHigh() / divisor.getLow())).invert();
		}

		if (divisor.lowEquals(0)) {
			return new Interval(Double.NEGATIVE_INFINITY,
					next(numerator.getHigh() / divisor.getHigh()));
		}

		if (divisor.getLow() > 0) {
			return divideNegativeByPositive(numerator, divisor);
		}
		return undefined();
	}

	private Interval divideNegativeByPositive(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.POSITIVE_INFINITY)) {
			return new Interval(prev(numerator.getLow() / divisor.getLow()), 0);
		}
		return new Interval(prev(numerator.getLow() / divisor.getLow()),
				next(numerator.getHigh() / divisor.getHigh()));
	}

	private Interval dividePositiveBy(Interval numerator, Interval divisor) {
		if (divisor.isZero()) {
			return 1 / divisor.getLow() > 0 ? positiveInfinity() : negativeInfinity();
		}

		if (divisor.highEquals(0)) {
			return new Interval(Double.NEGATIVE_INFINITY,
					next(numerator.getLow() / divisor.getLow()));
		}
		if (hasZeroInBetween(divisor)) {
			return new Interval(next(numerator.getLow() / divisor.getLow()), prev(
					numerator.getLow() / divisor.getHigh())).invert();
		}
		if (divisor.lowEquals(0)) {
			return dividePositiveByNegativeWithZeroAsHigh(numerator.getLow(), divisor.getHigh());
		}

		if (divisor.isPositive()) {
			return dividePositiveByPositive(numerator, divisor);
		}

		if (divisor.isNegative()) {
			if (numerator.highEquals(Double.POSITIVE_INFINITY)) {
				return new Interval(Double.NEGATIVE_INFINITY,
						next(numerator.getLow() / divisor.getLow()));
			}
			return new Interval(prev(numerator.getHigh() / divisor.getHigh()),
					next(numerator.getLow() / divisor.getLow()));
		}
		return undefined();
	}

	private Interval dividePositiveByNegativeWithZeroAsHigh(double a1, double b2) {
		return new Interval(prev(a1 / b2), Double.POSITIVE_INFINITY);
	}

	public static boolean hasZeroInBetween(final Interval interval) {
		return interval.containsExclusive(0);
	}

	private Interval divideNegativeByNegativeWithZeroAsHigh(double low) {
		return new Interval(low, Double.POSITIVE_INFINITY);
	}

	private Interval divideNegativeByNegative(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.NEGATIVE_INFINITY)) {
			return new Interval(0, next(numerator.getLow() / divisor.getHigh()));
		}
		return new Interval(prev(numerator.getHigh() / divisor.getLow()),
				next(numerator.getLow() / divisor.getHigh()));
	}

	private Interval dividePositiveByPositive(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.POSITIVE_INFINITY)) {
			return new Interval(prev(numerator.getLow() / divisor.getLow()), 0);
		}
		return new Interval(prev(numerator.getLow() / divisor.getHigh()),
				next(numerator.getHigh() / divisor.getLow()));
	}

	private boolean isZeroByZero(Interval numerator, Interval divisor) {
		return numerator.hasZero() && divisor.hasZero();
	}

	// just for the notation of the paper
	static double prev(double v) {
		return v;
	}

	static double next(double v) {
		return v;
	}

	private Interval divideMixedBy(Interval numerator, Interval divisor) {
		if (divisor.isNegative()) {
			return new Interval(prev(numerator.getHigh() / divisor.getHigh()),
					next(numerator.getLow() / divisor.getHigh()));
		}

		if (divisor.isPositive()) {
			if (numerator.lowEquals(Double.NEGATIVE_INFINITY)) {
				if (divisor.highEquals(Double.POSITIVE_INFINITY)) {
					return new Interval(Double.NEGATIVE_INFINITY,
							next(numerator.getHigh() / divisor.getLow()));
				}
				return new Interval(Double.NEGATIVE_INFINITY,
						next(numerator.getHigh() / divisor.getHigh()));
			}

			return new Interval(prev(numerator.getLow() / divisor.getLow()),
					next(numerator.getHigh() / divisor.getLow()));
		}
		return undefined();
	}
}