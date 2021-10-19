package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.RMath.divHigh;
import static org.geogebra.common.kernel.interval.RMath.divLow;

import com.google.j2objc.annotations.Weak;

public class IntervalDivide {

	@Weak
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
		if (other.isZero() || other.hasZero()) {
			return divideByZero(other);
		} else {
			return divideByNonZero(other);
		}
	}

	private Interval divideByZero(Interval other) {
		if (isNominatorNegative()) {
			if (other.isZero()) {
				 return negativeInfinity();
			} else {
				divideNegativeBy(other);
				interval.markAsInverted();
			}
		} else {
			if (other.isZero()) {
				return positiveInfinity();
			} else {
				dividePositiveBy(other);
				interval.markAsInverted();
			}
		}
		return interval;
	}

	private boolean isNominatorNegative() {
		return interval.getLow() < 0  && interval.getHigh() <= 0;
	}

	private Interval divideByNonZero(Interval other) {
		if (isNominatorNegative()) {
			divideNegativeBy(other);

		} else {
			dividePositiveBy(other);
		}
		return interval;
	}

	private void dividePositiveBy(Interval other) {
		if (other.isNegative()) {
			interval.set(divLow(interval.getHigh(), other.getHigh()),
					divHigh(interval.getLow(), other.getHigh()));
		} else {
			interval.set(divLow(interval.getLow(), other.getHigh()),
					divHigh(interval.getHigh(), other.getLow()));
		}
	}

	private void divideNegativeBy(Interval other) {
		if (other.isPositive()) {
			double low = divLow(interval.getHigh(), other.getLow());
			double high = divHigh(interval.getLow(), other.getHigh());
			if (high < low) {
				low = divLow(interval.getHigh(), other.getHigh());
				high = divHigh(interval.getHigh(), other.getLow());
			}
			interval.set(low, high);

		} else {
			double low = divLow(interval.getHigh(), other.getLow());
			double high = divHigh(interval.getHigh(), other.getHigh());
			interval.set(low, high);

		}
	}
}