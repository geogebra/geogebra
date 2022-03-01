package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalOperands.computeInverted;
import static org.geogebra.common.kernel.interval.IntervalOperands.divide;
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
		if (interval.isUndefined() || other.isUndefined()) {
			interval.setUndefined();
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
	 * @param interval to power.
	 * @param power of the interval
	 * @return power of the interval
	 */
	Interval pow(Interval interval, double power) {
		if (interval.isUndefined()) {
			return interval;
		}

		if (interval.isInverted()) {
			return computeInverted(pow(interval.extractLow(), power),
					pow(interval.extractHigh(), power));
		}

		if (power == 0) {
			return powerOfZero(interval);
		} else if (power < 0) {
			Interval divide = divide(one(), pow(interval, -power));
			interval.set(divide);
			return interval;
		}

		if (!DoubleUtil.isInteger(power)) {
			if (interval.isOne()) {
				return interval;
			}
			return powerOfDouble(interval, power);
		}

		return powOfInteger(interval, Math.round(power));
	}

	private Interval powerOfDouble(Interval interval, double power) {
		Interval other = new Interval(power);
		if (interval.isInverted()) {
			Interval lnPower2 = lnPower(interval.extractHigh(), other);
			return exp(new Interval(lnPower2));
		} else {
			return exp(lnPower(interval, other));
		}
	}

	private Interval lnPower(Interval interval, Interval other) {
		return multiply(log(interval), other);
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
			interval.setUndefined();
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
			interval.set(one());
			return interval;
		}

		if (!other.isSingleton()) {
			interval.setUndefined();
			return interval;
		}

		return pow(interval, other.getLow());
	}
}
