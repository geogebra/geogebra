package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.RMath.powHigh;
import static org.geogebra.common.kernel.interval.RMath.powLow;

import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

/**
 * Implements algebra functions in interval
 *
 *  fmod, pow, sqrt, nthRoot
 *
 * @author laszlo
 */
class IntervalAlgebra {
	@Weak
	private final Interval interval;

	IntervalAlgebra(Interval interval) {
		this.interval = interval;
	}

	/**
	 * Computes x mod y (x - k * y)
	 * @param other argument.
	 * @return this as result
	 */
	Interval fmod(Interval other) {
		if (interval.isEmpty() || other.isEmpty()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isUndefined()) {
			interval.setWhole();
			return interval;
		}

		double yb = interval.getLow() < 0 ? other.getLow() : other.getHigh();
		double n = interval.getLow() / yb;
		if (n < 0) {
			n = Math.ceil(n);
		} else {
			n = Math.floor(n);
		}

		Interval multiplicand = new Interval(other);
		// x mod y = x - n * y
		this.interval.subtract(multiplicand.multiply(new Interval(n)));
		return this.interval;
	}

	/**
	 * @param power of the interval
	 * @return power of the interval
	 */
	Interval pow(double power) {
		if (interval.isEmpty()) {
			return interval;
		}

		if (power == 0) {
			return powerOfZero();
		} else if (power < 0) {
			interval.set(interval.multiplicativeInverse().pow(-power));
			return interval;
		}

		return powOfInteger((int) power);
	}

	private Interval powOfInteger(int power) {
		if (interval.getHigh() < 0) {
			// [negative, negative]
			double yl = powLow(-interval.getHigh(), power);
			double yh = powHigh(-interval.getLow(), power);
			if ((power & 1) == 1) {
				// odd power
				interval.set(-yh, -yl);
			} else {
				// even power
				interval.set(yl, yh);
			}
		} else if (interval.getLow() < 0) {
			// [negative, positive]
			if ((power & 1) == 1) {
				interval.set(-powLow(-interval.getLow(), power),
						powHigh(interval.getHigh(), power));
			} else {
				// even power means that any negative number will be zero (min value = 0)
				// and the max value will be the max of x.lo^power, x.hi^power
				interval.set(0,
						powHigh(Math.max(-interval.getLow(), interval.getHigh()), power));
			}
		} else {
			// [positive, positive]
			interval.set(powLow(interval.getLow(), power),
					powHigh(interval.getHigh(), power));
		}
		return interval;
	}

	private Interval powerOfZero() {
		if (interval.getLow() == 0 && interval.getHigh() == 0) {
			// 0^0
			interval.setEmpty();
			return interval;
		} else {
			// x^0
			interval.set(1, 1);
			return interval;
		}
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 * @param other interval power.
	 * @return this as result.
	 * @throws PowerIsNotInteger if other is not a singleton interval.
	 */
	Interval pow(Interval other) throws PowerIsNotInteger {
		if (!other.isSingleton()) {
			interval.setEmpty();
			return interval;
		}

		if (!DoubleUtil.isInteger(other.getLow())) {
			throw new PowerIsNotInteger();
		}

		return pow((int) other.getLow());
	}

	/**
	 * @return square root of the interval.
	 */
	Interval sqrt() {
		if (interval.isEmpty()) {
			interval.setEmpty();
			return interval;
		}

		return nthRoot(2);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval nthRoot(Interval other) {
		if (!other.isSingleton()) {
			interval.setEmpty();
			return interval;
		}

		return nthRoot(other.getLow());
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval nthRoot(double n) {
		if (interval.isEmpty() || n < 1) {
			interval.setEmpty();
			return interval;
		}

		double power = 1 / n;
		if (interval.getHigh() < 0) {
			if (DoubleUtil.isInteger(n) && ((int) n & 1) == 1) {
				double resultLow = powHigh(-interval.getLow(), power);
				double resultHigh = powLow(-interval.getHigh(), power);
				interval.set(-resultLow, -resultHigh);
				return interval;
			}
			interval.setEmpty();
			return interval;
		} else if (interval.getLow() < 0) {
			double yp = powHigh(interval.getHigh(), power);
			if (DoubleUtil.isInteger(n) && ((int) n & 1) == 1) {
				double yn = -powHigh(-interval.getLow(), power);
				interval.set(yn, yp);
				return interval;
			}
			interval.set(0, yp);
			return interval;
		} else {
			interval.set(powLow(interval.getLow(), power),
					powHigh(interval.getHigh(), power));
		}
		return interval;
	}
}