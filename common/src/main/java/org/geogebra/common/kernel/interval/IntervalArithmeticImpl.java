package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.RMath.mulHigh;
import static org.geogebra.common.kernel.interval.RMath.mulLow;

import com.google.j2objc.annotations.Weak;

public class IntervalArithmeticImpl implements IntervalArithmetic {
	@Weak
	private final Interval interval;

	public IntervalArithmeticImpl(Interval interval) {
		this.interval = interval;
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
					return divisionByZero();
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

	private Interval divisionByZero() {
		if (interval.isZero()) {
			return interval;
		}
		interval.setUndefined();
		return interval;
	}

	@Override
	public Interval multiply(Interval other) {
		if (interval.isEmpty() || other.isEmpty()) {
			return empty();
		}

		if (interval.isUndefined() || other.isUndefined()) {
			return undefined();
		}

		double xl = interval.getLow();
		double xh = interval.getHigh();
		double yl = other.getLow();
		double yh = other.getHigh();
		if (xl < 0) {
			if (xh > 0) {
				if (yl < 0) {
					if (yh > 0) {
						// mixed * mixed
						interval.set(Math.min(mulLow(xl, yh), mulLow(xh, yl)),
							Math.max(mulHigh(xl, yl), mulHigh(xh, yh)));
					} else {
						// mixed * negative
						interval.set(mulLow(xh, yl), mulHigh(xl, yl));
					}
				} else {
					if (yh > 0) {
						// mixed * positive
						interval.set(mulLow(xl, yh), mulHigh(xh, yh));
					} else {
						// mixed * zero
						interval.setZero();
					}
				}
			} else {
				if (yl < 0) {
					if (yh > 0) {
						// negative * mixed
						interval.set(mulLow(xl, yh), mulHigh(xl, yl));
					} else {
						// negative * negative
						interval.set(mulLow(xh, yh), mulHigh(xl, yl));
					}
				} else {
					if (yh > 0) {
						// negative * positive
						interval.set(mulLow(xl, yh), mulHigh(xh, yl));
					} else {
						// negative * zero
						interval.setZero();
					}
				}
			}
		} else {
			if (xh > 0) {
				if (yl < 0) {
					if (yh > 0) {
						// positive * mixed
						interval.set(mulLow(xh, yl), mulHigh(xh, yh));
					} else {
						// positive * negative
						interval.set(mulLow(xh, yl), mulHigh(xl, yh));
					}
				} else {
					if (yh > 0) {
						// positive * positive
						interval.set(mulLow(xl, yl), mulHigh(xh, yh));
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
		return interval;
	}
}
