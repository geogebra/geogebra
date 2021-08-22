package org.geogebra.common.kernel.interval;

/**
 * Multiplication of intervals.
 */
public class IntervalMultiply {
	private final Interval interval;

	/**
	 *
	 * @param interval to multiply
	 */
	public IntervalMultiply(Interval interval) {
		this.interval = interval;
	}

	/**
	 *
	 * @param other to multiply with.
	 * @return interval * other.
	 */
	public Interval multiply(Interval other) {
		if (interval.isZero() || other.isZero()) {
			return IntervalConstants.zero();
		}

		if (interval.isWhole() || other.isOne()) {
			return interval;
		}

		if (interval.isOne()) {
			return other;
		}

		if (interval.isEmpty() || other.isEmpty()) {
			return IntervalConstants.empty();
		}

		if (interval.isUndefined() || other.isUndefined()) {
			return IntervalConstants.undefined();
		}

		return multiplyNonTrivial(other);
	}

	private Interval multiplyNonTrivial(Interval other) {
		if (interval.getLow() < 0) {
			xlNegative(other);
		} else {
			xlPositiveOrZero(other);
		}

		if (other.isInverted()) {
			interval.setInverted();
		}
		if (other.isUninverted()) {
			interval.uninvert();
		}
		return interval;
	}

	private void xlPositiveOrZero(Interval other) {
		if (interval.getHigh() > 0) {
			if (other.getLow() < 0) {
				if (other.getHigh() > 0) {
					// positive * mixed
					interval
							.set(RMath.mulLow(interval.getHigh(), other.getLow()),
									RMath.mulHigh(interval.getHigh(), other.getHigh()));
				} else {
					// positive * negative
					interval
							.set(RMath.mulLow(interval.getHigh(), other.getLow()),
									RMath.mulHigh(interval.getLow(), other.getHigh()));
				}
			} else {
				if (other.getHigh() > 0) {
					// positive * positive
					interval
							.set(RMath.mulLow(interval.getLow(), other.getLow()),
									RMath.mulHigh(interval.getHigh(), other.getHigh()));
				} else {
					// positive * zero
					interval.setZero();
				}
			}
		} else {
			// zero * any other value
			interval.setZero();
		}
	}

	private void xlNegative(Interval other) {
		if (interval.getHigh() > 0) {
			multiplyMixedWidth(other);
		} else {
			multiplyNegativeWith(other);
		}
	}

	private void multiplyNegativeWith(Interval other) {
		if (other.getLow() < 0) {
			if (other.getHigh() > 0) {
				multiplyNegativeWithMixed(other);
			} else {
				// negative * negative
				interval
						.set(RMath.mulLow(interval.getHigh(), other.getHigh()),
								RMath.mulHigh(interval.getLow(), other.getLow()));
			}
		} else {
			if (other.getHigh() > 0) {
				// negative * positive
				interval
						.set(RMath.mulLow(interval.getLow(), other.getHigh()),
								RMath.mulHigh(interval.getHigh(), other.getLow()));
			} else {
				// negative * zero
				interval.setZero();
			}
		}
	}

	private void multiplyNegativeWithMixed(Interval other) {
		interval
				.set(RMath.mulLow(interval.getLow(), other.getHigh()),
						RMath.mulHigh(interval.getLow(), other.getLow()));
	}

	private void multiplyMixedWidth(Interval other) {

		if (other.getLow() < 0) {
			if (other.getHigh() > 0) {
				// mixed * mixed
				interval
						.set(Math.min(RMath.mulLow(interval.getLow(), other.getHigh()),
										RMath.mulLow(interval.getHigh(), other.getLow())),
								Math.max(RMath.mulHigh(interval.getLow(), other.getLow()),
										RMath.mulHigh(interval.getHigh(), other.getHigh())));
			} else {
				// mixed * negative
				interval
						.set(RMath.mulLow(interval.getHigh(), other.getLow()), RMath.mulHigh(interval.getLow(),
								other.getLow()));
			}
		} else {
			if (other.getHigh() > 0) {
				// mixed * positive
				interval
						.set(RMath.mulLow(interval.getLow(), other.getHigh()), RMath.mulHigh(
								interval.getHigh(),
								other.getHigh()));
			} else {
				// mixed * zero
				interval.setZero();
			}
		}
	}
}