package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.DoubleUtil;

/**
 * Computes 1 / interval.
 *
 * @author Laszlo
 */
public class IntervalMultiplicativeInverse {
	private final Interval interval;

	public IntervalMultiplicativeInverse(Interval interval) {
		this.interval = interval;
	}

	/**
	 *
	 * @return 1 / interval
	 */
	public Interval getResult() {
		if (interval.isEmpty()) {
			return interval;
		}

		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isInverted()) {
			handleInverted();
			return interval;
		}

		if (interval.hasZero()) {
			handleWithZero();
			return interval;
		}

		if (interval.isPositiveInfinity()) {
			interval.setZero();
			interval.invert();
			return interval;
		}

		if (interval.isNegativeInfinity()) {
			interval.set(IntervalConstants.zeroWithNegativeSign());
			interval.invert();
			
			return interval;
		}

		return inverseWithPositiveBounds();
	}

	private Interval inverseWithPositiveBounds() {
		return new Interval(RMath.divLow(1, interval.getHigh()),
				RMath.divHigh(1, interval.getLow()));
	}

	private void handleInverted() {
		if (!interval.isWhole()) {
			interval.invert();
			invertBounds();
		}
	}

	private void invertBounds() {
		interval.set(RMath.divLow(1, interval.getLow()), RMath.divHigh(1, interval.getHigh()));
	}

	private void handleWithZero() {
		if (!DoubleUtil.isEqual(interval.getLow(), 0, 1E-6)) {
			handleLowNotZero();
		} else {
			handleLowIsZero();
		}
	}

	private void handleLowIsZero() {
		if (!DoubleUtil.isEqual(interval.getHigh(), 0, 1E-6)) {
			inverseWithBoundsZeroAndPositive();
		} else {
			inverseOfZero();
		}
	}

	private void inverseOfZero() {
		if (interval.isInverted()) {
			invertBounds();
			interval.uninvert();
		} else {
			interval.set(interval.getLow() < 0
					? Double.NEGATIVE_INFINITY
					: Double.POSITIVE_INFINITY);
		}
	}

	private void inverseWithBoundsZeroAndPositive() {
		interval.set(RMath.divLow(1, interval.getHigh()), Double.POSITIVE_INFINITY);
	}

	private void handleLowNotZero() {
		if (interval.getHigh() != 0) {
			inverseWithZeroWithinBounds();
		} else {
			inverseWithBoundsNegativeAndZero();
		}
	}

	private void inverseWithBoundsNegativeAndZero() {
		interval.set(Double.NEGATIVE_INFINITY, RMath.divHigh(1.0, interval.getLow()));
	}

	private void inverseWithZeroWithinBounds() {
		invertBounds();
		interval.invert();
	}
}
