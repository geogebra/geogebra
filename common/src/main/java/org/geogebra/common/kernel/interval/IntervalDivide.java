package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

public class IntervalDivide {

	private final Interval interval;

	/**
	 *
	 * @param interval to divide.
	 */
	public IntervalDivide(Interval interval) {
		this.interval = interval;
	}

	/**
	 * Divide intervals.
	 *
	 * @param other interval to divide by.
	 * @return the result of interval divided by other.
	 */
	public Interval divide(Interval other) {
		if (interval.isEmpty() || other.isEmpty()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isZero() || other.isOne()) {
			return interval;
		}

		return divideByNonTrivial(other);
	}

	private Interval divideByNonTrivial(Interval other) {
		if (other.hasZero()) {
			return divideByHasZero(other);
		} else {
			return divideByNonZero(other);
		}
	}

	private Interval divideByHasZero(Interval other) {
		if (other.getLow() != 0) {
			if (other.getHigh() != 0) {
				return divideByMixed(other);
			} else {
				return divideByNegativeAndZero(other.getLow());
			}
		} else {
			if (other.getHigh() != 0) {
				return divideByPositive(other.getHigh());
			} else {
				interval.setUndefined();
				return interval;
			}
		}
	}

	private Interval divideByNonZero(Interval other) {
		if (interval.getHigh() < 0) {
			if (other.getHigh() < 0) {
				divideNegativeByNegative(other);
			} else {
				divideNegativeByPositive(other);
			}
		} else if (interval.getLow() < 0) {
			if (other.getHigh() < 0) {
				interval.set(RMath.divLow(interval.getHigh(), other.getHigh()), RMath.divHigh(
						interval.getLow(),
						other.getHigh()));
			} else {
				interval.set(RMath.divLow(interval.getLow(), other.getLow()),
						RMath.divHigh(interval.getHigh(), other.getLow()));
			}
		} else {
			if (other.getHigh() < 0) {
				interval.set(RMath.divLow(interval.getHigh(), other.getHigh()), RMath.divHigh(
						interval.getLow(),
						other.getLow()));
			} else {
				interval.set(RMath.divLow(interval.getLow(), other.getHigh()),
						RMath.divHigh(interval.getHigh(), other.getLow()));
			}
		}
		return interval;
	}

	private void divideNegativeByPositive(Interval other) {
		interval.set(RMath.divLow(interval.getLow(), other.getLow()),
				RMath.divHigh(interval.getHigh(), other.getHigh()));
	}

	private void divideNegativeByNegative(Interval other) {
		interval.set(RMath.divLow(interval.getHigh(), other.getLow()),
				RMath.divHigh(interval.getLow(), other.getHigh()));
	}

	private Interval divideByPositive(double x) {
		if (interval.hasZero()) {
			return undefined();
		}

		if (interval.getHigh() < 0) {
			interval.set(NEGATIVE_INFINITY, RMath.divHigh(interval.getHigh(), x));
		} else {
			interval.set(RMath.divLow(interval.getLow(), x), POSITIVE_INFINITY);
		}
		return interval;
	}

	private Interval divideByNegativeAndZero(double x) {
		if (interval.hasZero()) {
			interval.setUndefined();
			return interval;
		}

		if (interval.getHigh() < 0) {
			interval.set(RMath.divLow(interval.getHigh(), x), POSITIVE_INFINITY);
		} else {
			interval.set(NEGATIVE_INFINITY, RMath.divHigh(interval.getLow(), x));
		}

		return interval;
	}

	private Interval divideByMixed(Interval other) {
		if (interval.isInverted()) {
			interval.setUndefined();
			return interval;
		}

		Interval result = new Interval(interval.getLow() / other.getLow(),
				interval.getHigh() / other.getHigh());
		result.setInverted();
		return result;
	}
}