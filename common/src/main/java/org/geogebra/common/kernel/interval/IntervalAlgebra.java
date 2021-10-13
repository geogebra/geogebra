package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalOperands.exp;
import static org.geogebra.common.kernel.interval.IntervalOperands.log;
import static org.geogebra.common.kernel.interval.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.RMath.powHigh;
import static org.geogebra.common.kernel.interval.RMath.powLow;

import org.geogebra.common.util.DoubleUtil;

/**
 * Implements algebra functions in interval
 *
 *  fmod, pow, sqrt, nthRoot
 *
 * @author laszlo
 */
public class IntervalAlgebra {

	/**
	 * Computes x mod y (x - k * y)
	 * @param other argument.
	 * @return this as result
	 */
	Interval fmod(Interval interval, Interval other) {
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
		interval.subtract(multiply(multiplicand, new Interval(n)));
		return interval;
	}

	/**
	 * @param power of the interval
	 * @return power of the interval
	 */
	Interval pow(Interval interval, double power) {
		if (interval.isEmpty()) {
			return interval;
		}

		if (power == 0) {
			return powerOfZero(interval);
		} else if (power < 0) {
			interval.set(pow(interval.multiplicativeInverse(), -power));
			return interval;
		}

		if (!DoubleUtil.isInteger(power)) {
			return powerOfDouble(interval, power);
		}

		return powOfInteger(interval, Math.round(power));
	}

	private Interval powerOfDouble(Interval interval, double power) {
		Interval lnPower = multiply(log(interval), new Interval(power));
		return exp(lnPower);
	}

	private Interval powOfInteger(Interval interval, long power) {
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

	private Interval powerOfZero(Interval interval) {
		if (interval.getLow() == 0 && interval.getHigh() == 0) {
			// 0^0
			interval.setEmpty();
		} else {
			// x^0
			interval.set(1, 1);
		}
		return interval;
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 * @param other interval power.
	 * @return this as result.
	 */
	Interval pow(Interval interval, Interval other) {
		if (other.isZero()) {
			interval.set(IntervalConstants.one());
			return interval;
		}

		if (!other.isSingleton()) {
			interval.setEmpty();
			return interval;
		}

		return pow(interval, other.getLow());
	}

	/**
	 * @return square root of the interval.
	 */
	Interval sqrt(Interval interval) {
		if (interval.isEmpty()) {
			interval.setEmpty();
			return interval;
		}

		return nthRoot(interval, 2);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval nthRoot(Interval interval, Interval other) {
		if (!other.isSingleton()) {
			interval.setEmpty();
			return interval;
		}

		return nthRoot(interval, other.getLow());
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval nthRoot(Interval interval, double n) {
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