package org.geogebra.common.kernel.interval;

import com.google.j2objc.annotations.Weak;

/**
 * Multiplication of intervals.
 *
 * Note: an interval [high, low] is called mixed when low < 0 < high.
 */
public class IntervalMultiply {

	@Weak
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
		if (interval.isZero()) {
			return interval;
		}

		if (other.isZero()) {
			return other;
		}

		if (interval.isWhole() || other.isOne()) {
			return interval;
		}

		if (interval.isOne()) {
			return other;
		}

		if (interval.isMinusOne()) {
			return other.negative();
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
			interval.markAsInverted();
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
					multiplyPositiveWithMixed(other);
				} else {
					multiplyPositiveWithNegative(other);
				}
			} else {
				if (other.getHigh() > 0) {
					multiplyPositiveWithPositive(other);
				} else {
					multiplyWithZero();
				}
			}
		} else {
			multiplyWithZero();
		}
	}

	private void multiplyWithZero() {
		interval.setZero();
	}

	private void multiplyPositiveWithPositive(Interval other) {
		multiply(interval.getLow(), other.getLow(), interval.getHigh(), other.getHigh());
	}

	private void multiply(double low1, double low2, double high1, double high2) {
		interval.set(RMath.mulLow(low1, low2), RMath.mulHigh(high1, high2));
	}

	private void multiplyPositiveWithNegative(Interval other) {
		multiply(interval.getHigh(), other.getLow(), interval.getLow(), other.getHigh());
	}

	private void multiplyPositiveWithMixed(Interval other) {
		multiply(interval.getHigh(), other.getLow(), interval.getHigh(), other.getHigh());
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
				multiplyNegativeWithNegative(other);
			}
		} else {
			if (other.getHigh() > 0) {
				multiplyNegativeWithPositive(other);
			} else {
				multiplyWithZero();
			}
		}
	}

	private void multiplyNegativeWithPositive(Interval other) {
		multiply(interval.getLow(), other.getHigh(), interval.getHigh(), other.getLow());
	}

	private void multiplyNegativeWithNegative(Interval other) {
		multiply(interval.getHigh(), other.getHigh(), interval.getLow(), other.getLow());
	}

	private void multiplyNegativeWithMixed(Interval other) {
		multiply(interval.getLow(), other.getHigh(), interval.getLow(), other.getLow());
	}

	private void multiplyMixedWidth(Interval other) {
		if (other.getLow() < 0) {
			if (other.getHigh() > 0) {
				multiplyMixedWithMixed(other);
			} else {
				multiplyMixedWithNegative(other);
			}
		} else {
			if (other.getHigh() > 0) {
				multiplyMixedWithPositive(other);
			} else {
				multiplyWithZero();
			}
		}
	}

	private void multiplyMixedWithPositive(Interval other) {
		multiply(interval.getLow(), other.getHigh(), interval.getHigh(), other.getHigh());
	}

	private void multiplyMixedWithNegative(Interval other) {
		multiply(interval.getHigh(), other.getLow(), interval.getLow(), other.getLow());
	}

	private void multiplyMixedWithMixed(Interval other) {
		interval
				.set(Math.min(RMath.mulLow(interval.getLow(), other.getHigh()),
								RMath.mulLow(interval.getHigh(), other.getLow())),
						Math.max(RMath.mulHigh(interval.getLow(), other.getLow()),
								RMath.mulHigh(interval.getHigh(), other.getHigh())));
	}
}