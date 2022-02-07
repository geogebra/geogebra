package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

/**
 * Multiplication of intervals.
 *
 */
public class IntervalMultiply {

	/**
	 * Multiplies two intervals
	 *
	 * @param interval the multiplicand
	 * @param other the other multiplicand
	 * @return the result
	 */
	public Interval compute(Interval interval, Interval other) {
		Interval result = multiply(interval, other);
		result.setInverted(interval.isInverted() || other.isInverted());
		return result;
	}

	private Interval multiply(Interval interval, Interval other) {
		if (interval.isZero() && other.isInverted()) {
			return IntervalConstants.whole();
		}

		if (interval.isZero() || other.isZero()) {
			return IntervalConstants.zero();
		}

		if (interval.isWhole() || other.isWhole()) {
			return IntervalConstants.whole();
		}

		if (interval.isNegativeWithZero()) {
			return mulNegativeWithZeroAnd(interval, other);
		}

		if (isZeroInBetween(interval)) {
			return mulIsZeroInBetween(interval, other);
		}

		if (interval.isPositiveWithZero()) {
			return mulPositiveWithZeroAnd(interval, other);
		}

		return undefined();
	}

	private Interval mulPositiveWithZeroAnd(Interval interval, Interval other) {
		if (other.isNegativeWithZero()) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getLow() * other.getHigh()));
		}

		if (other.isPositiveWithZero()) {
			return new Interval(prev(interval.getLow() * other.getLow()), next(
					interval.getHigh() * other.getHigh()));
		}

		if (isZeroInBetween(other)) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getHigh() * other.getHigh()));
		}
		return undefined();
	}

	private Interval mulIsZeroInBetween(Interval interval, Interval other) {
		if (isZeroInBetween(other)) {
			return new Interval(Math.min(prev(interval.getLow() * other.getHigh()), prev(
					interval.getHigh() * other.getLow())),
					Math.max(next(interval.getLow() * other.getLow()), next(
							interval.getHigh() * other.getHigh())));
		}

		if (other.isNegativeWithZero()) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getLow() * other.getLow()));

		}

		if (other.isPositiveWithZero()) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getHigh() * other.getHigh()));
		}
		return undefined();
	}

	private boolean isZeroInBetween(Interval interval) {
		return interval.containsExclusive(0);
	}

	private Interval mulNegativeWithZeroAnd(Interval interval, Interval other) {
		if (other.getHigh() <= 0) {
			return new Interval(prev(interval.getHigh() * other.getHigh()), next(
					interval.getLow() * other.getLow()));
		}

		if (isZeroInBetween(other)) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getLow() * other.getLow()));
		}

		if (other.getLow() >= 0) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getHigh() * other.getLow()));
		}

		if (other.lowEquals(Double.NEGATIVE_INFINITY) && other.getHigh() <= 0) {
				return new Interval(prev(interval.getHigh() * other.getHigh()),
						Double.POSITIVE_INFINITY);
		}

		return undefined();
	}

	double next(double v) {
		return v;
	}

	double prev(double v) {
		return v;
	}
}