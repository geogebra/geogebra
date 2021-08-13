package org.geogebra.common.kernel.interval;

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

		return inverseWithPositiveBounds();
	}

	private Interval inverseWithPositiveBounds() {
		return new Interval(RMath.divLow(1, interval.getHigh()),
				RMath.divHigh(1, interval.getLow()));
	}

	private void handleInverted() {
		interval.uninvert();
		if (!interval.isWhole()) {
			invertBounds();
		}
	}

	private void invertBounds() {
		interval.set(RMath.divLow(1, interval.getLow()), RMath.divHigh(1, interval.getHigh()));
	}

	private void handleWithZero() {
		if (interval.getLow() != 0) {
			handleLowNotZero();
		} else {
			handleLowIsZero();
		}
	}

	private void handleLowIsZero() {
		if (interval.getHigh() != 0) {
			inverseWithBoundsZeroAndPositive();
		} else {
			inverseOfZero();
		}
	}

	private void inverseOfZero() {
		interval.setInverted();
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
		interval.setInverted();
	}
}
