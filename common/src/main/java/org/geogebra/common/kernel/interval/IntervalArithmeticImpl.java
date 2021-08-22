package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import com.google.j2objc.annotations.Weak;

public class IntervalArithmeticImpl implements IntervalArithmetic {
	@Weak
	private final Interval interval;
	private final IntervalMultiply intervalMultiply;

	public IntervalArithmeticImpl(Interval interval) {
		this.interval = interval;
		intervalMultiply = new IntervalMultiply(interval);
	}

	@Override
	public Interval divide(Interval other) {
		if (interval.isEmpty() || other.isEmpty()) {
			interval.setEmpty();
			return interval;
		}

		if (other.hasZero()) {
			if (other.getLow() != 0) {
				if (other.getHigh() != 0) {
					return divideAndInvert(other);
				} else {
					return divisionByNegative(other.getLow());
				}
			} else {
				if (other.getHigh() != 0) {
					return divisionByPositive(other.getHigh());
				} else {
					interval.setUndefined();
				}
			}
		} else {
			return nonZero(other);
		}
		return interval;
	}

	private Interval nonZero(Interval other) {
		double xl = interval.getLow();
  		double xh = interval.getHigh();
  		double yl = other.getLow();
  		double yh = other.getHigh();
		if (xh < 0) {
			if (yh < 0) {
				interval.set(RMath.divLow(xh, yl), RMath.divHigh(xl, yh));
			} else {
				interval.set(RMath.divLow(xl, yl), RMath.divHigh(xh, yh));
			}
		} else if (xl < 0) {
			if (yh < 0) {
				interval.set(RMath.divLow(xh, yh), RMath.divHigh(xl, yh));
			} else {
				interval.set(RMath.divLow(xl, yl), RMath.divHigh(xh, yl));
			}
		} else {
			if (yh < 0) {
				interval.set(RMath.divLow(xh, yh), RMath.divHigh(xl, yl));
			} else {
				interval.set(RMath.divLow(xl, yh), RMath.divHigh(xh, yl));
			}
		}
		return interval;
	}

	private Interval divisionByPositive(double x) {
		if (interval.isZero()) {
			return interval;
		}

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

	private Interval divisionByNegative(double x) {
		if (interval.isZero()) {
			return interval;
		}

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

	private Interval divideAndInvert(Interval other) {
		if (interval.isZero()) {
			return interval;
		}
		if (interval.isInverted()) {
			interval.setUndefined();
			return interval;
		}
		Interval result = new Interval(interval.getLow() / other.getLow(),
				interval.getHigh() / other.getHigh());
		result.setInverted();
		return result;
	}

	@Override
	public Interval multiply(Interval other) {
		return intervalMultiply.multiply(other);
	}
}
