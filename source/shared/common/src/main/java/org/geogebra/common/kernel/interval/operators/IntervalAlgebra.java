/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalSet.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.isExactSingleton;
import static org.geogebra.common.kernel.interval.IntervalSetOps.isPositive;
import static org.geogebra.common.kernel.interval.IntervalSetOps.isZero;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;
import static org.geogebra.common.kernel.interval.IntervalSetOps.zero;
import static org.geogebra.common.kernel.interval.operators.RMath.powHigh;
import static org.geogebra.common.kernel.interval.operators.RMath.powLow;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.util.DoubleUtil;

/**
 * Implements algebra functions in interval
 *
 *  fmod, pow, sqrt, nthRoot
 *
 * @author laszlo
 */
public class IntervalAlgebra {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalAlgebra(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * Computes x mod y (x - k * y)
	 * @param other argument.
	 * @return this as result
	 */
	Interval fmod(Interval interval, Interval other) {
		return toLegacy(fmodSet(fromLegacy(interval), fromLegacy(other)));
	}

	IntervalSet fmodSet(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return empty();
		}

		if (!set2.isConnected() || IntervalSetOps.hasZero(set2)) {
			return empty();
		}

		Interval interval2 = connectedInterval(set2);
		if (set1.isWhole() || set1.isInverted()) {
			double maxAbsDivisor = Math.max(Math.abs(interval2.getLow()),
					Math.abs(interval2.getHigh()));
			return connected(-maxAbsDivisor, maxAbsDivisor);
		}

		Interval interval1 = connectedInterval(set1);
		double yb = interval1.getLow() < 0 ? interval2.getLow() : interval2.getHigh();
		double n = interval1.getLow() / yb;
		if (n < 0) {
			n = Math.ceil(n);
		} else {
			n = Math.floor(n);
		}

		// x mod y = x - n * y
		Interval multiplied = evaluator.multiply(connectedInterval(set2), new Interval(n));
		return connected(interval1.getLow() - multiplied.getHigh(),
				interval1.getHigh() - multiplied.getLow());
	}

	/**
	 * @param interval to power.
	 * @param power of the interval
	 * @return power of the interval
	 */
	Interval pow(Interval interval, double power) {
		return toLegacy(powSet(fromLegacy(interval), power));
	}

	IntervalSet powSet(IntervalSet set, IntervalSet power) {
		if (set.isEmpty() || power.isEmpty()) {
			return empty();
		}

		if (isZero(set) && isZero(power)) {
			return IntervalSetOps.one();
		}

		if (isZero(set, 0) && isNonSingletonConnected(power)) {
			return powerOfZeroBaseWithConnectedExponent(power);
		}

		if (power.isInverted()) {
			IntervalSet left = leftRayFromInverted(power);
			IntervalSet right = rightRayFromInverted(power);
			return evaluator.unionInvertedSet(evaluator.powSet(set, left),
					evaluator.powSet(set, right));
		}

		if (power.isConnected() && connectedInterval(power).isExactSingleton()) {
			if (isZero(set)) {
				return connectedInterval(power).getHigh() >= 0 ? zero() : empty();
			}
			if (!isZero(power, 0)) {
				return powSet(set, connectedInterval(power).getLow());
			}
		}

		return powOfSingleton(set, power);
	}

	private boolean isNonSingletonConnected(IntervalSet set) {
		return set.isConnected() && !connectedInterval(set).isSingleton();
	}

	IntervalSet powSet(IntervalSet set, double power) {
		if (set.isEmpty() || DoubleUtil.isEqual(power, 1)) {
			return set;
		}

		if (DoubleUtil.isEqual(power, -1)) {
			return evaluator.multiplicativeInverseSet(set);
		}

		if (set.isInverted()) {
			IntervalSet left = leftRayFromInverted(set);
			IntervalSet right = rightRayFromInverted(set);
			IntervalSet leftPow = evaluator.powSet(left, power);
			IntervalSet rightPow = evaluator.powSet(right, power);
			if (power > 0 && DoubleUtil.isInteger(power) && Math.round(power) % 2 == 0) {
				return evaluator.unionSet(leftPow, rightPow);
			}
			if (power > 0 && DoubleUtil.isInteger(power) && Math.round(power) % 2 != 0
					&& leftPow.isConnected() && rightPow.isConnected()) {
				Interval leftInterval = connectedInterval(leftPow);
				Interval rightInterval = connectedInterval(rightPow);
				return IntervalSet.inverted(leftInterval.getHigh(), rightInterval.getLow());
			}
			return evaluator.unionInvertedSet(leftPow, rightPow);
		}

		if (power == 0) {
			return powerOfZeroSet(set);
		} else if (power < 0) {
			return evaluator.divideSet(IntervalSetOps.one(),
					evaluator.powSet(set, -power));
		}

		if (!DoubleUtil.isInteger(power) || !isCloseToInteger(power)) {
			if (IntervalSetOps.isOne(set)) {
				return set;
			}
			return powerOfDoubleSet(set, power);
		}

		return legacyIntegerPowerFallback(set, Math.round(power));
	}

	private boolean isCloseToInteger(double power) {
		return DoubleUtil.isEqual(power, Math.round(power), IntervalConstants.PRECISION * 2);
	}

	private IntervalSet legacyIntegerPowerFallback(IntervalSet set, long power) {
		// Integer powers still rely on the legacy numeric kernel for compatibility.
		return fromLegacy(powOfInteger(toLegacy(set), power));
	}

	private IntervalSet powerOfDoubleSet(IntervalSet set, double power) {
		return evaluator.expSet(lnPower(positiveDomainForFractionalPower(set),
				connected(power, power)));
	}

	private IntervalSet positiveDomainForFractionalPower(IntervalSet set) {
		// Non-integer double powers are evaluated via exp(power * log(x)),
		// so only the positive-domain contribution is admissible here.
		return set.isInverted() ? rightRayFromInverted(set) : set;
	}

	private IntervalSet lnPower(IntervalSet set1, IntervalSet set2) {
		return evaluator.multiplySet(evaluator.logSet(set1), set2);
	}

	private Interval powOfInteger(Interval interval, long power) {
		if (interval.getHigh() < 0) {
			// [negative, negative]
			double yl = powLow(-interval.getHigh(), power);
			double yh = powHigh(-interval.getLow(), power);
			if ((power & 1) == 1) {
				// odd power
				return new Interval(-yh, -yl);
			} else {
				// even power
				return new Interval(yl, yh);
			}
		} else if (interval.getLow() < 0) {
			// [negative, positive]
			if ((power & 1) == 1) {
				return new Interval(-powLow(-interval.getLow(), power),
						powHigh(interval.getHigh(), power));
			} else {
				// even power means that any negative number will be zero (min value = 0)
				// and the max value will be the max of x.lo^power, x.hi^power
				return new Interval(0,
						powHigh(Math.max(-interval.getLow(), interval.getHigh()), power));
			}
		}

		// [positive, positive]
		if (interval.isSingleton()) {
			return new Interval(Math.pow(interval.getLow(), power));
		}
		return new Interval(powLow(interval.getLow(), power),
					powHigh(interval.getHigh(), power));

	}

	private IntervalSet powerOfZeroSet(IntervalSet set) {
		if (isZero(set, 0)) {
			// 0^0
			return empty();
		}

		return IntervalSetOps.one();
	}

	private IntervalSet powerOfZeroBaseWithConnectedExponent(IntervalSet power) {
		return connectedInterval(power).isPositive() ? IntervalSetOps.zero() : empty();
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 * @param base power base
	 * @param power interval power.
	 * @return this as result.
	 */
	IntervalSet powOfSingleton(IntervalSet base, IntervalSet power) {
		if (isZero(power)) {
			// x^0 should be 1 for x around 0, 0^x should be 0 for small x
			return isZero(base) && isExactSingleton(base) && !isExactSingleton(power)
					? IntervalSetOps.empty() : connected(1, 1);
		}

		if (isZero(base, IntervalConstants.PRECISION / 2)) {
			return isPositive(power) ? connected(0, 0) : empty();
		}

		if (!isExactSingleton(power)) {
			return powerOfInterval(base, power);
		}

		return powSet(base, connectedInterval(power).getLow());
	}

	private IntervalSet powerOfInterval(IntervalSet baseSet, IntervalSet powerSet) {
		if (baseSet.isEmpty() || powerSet.isEmpty()) {
			return empty();
		}

		if (baseSet.isWhole() || powerSet.isWhole()) {
			return whole();
		}

		if (powerSet.isInverted()) {
			IntervalSet extractedLow = powOfSingleton(baseSet, leftRayFromInverted(powerSet));
			IntervalSet extractedHigh = powOfSingleton(baseSet, rightRayFromInverted(powerSet));
			if (extractedHigh.isEmpty()) {
				return empty();
			}
			return evaluator.unionInvertedSet(extractedLow, extractedHigh);
		}

		Interval base = connectedInterval(baseSet);
		Interval power = connectedInterval(powerSet);

		double low = powLow(base.getLow(), power.getLow());
		double high = powHigh(base.getHigh(), power.getHigh());

		if (Double.isNaN(low) || Double.isNaN(high)) {
			return empty();
		}

		if (high < low) {
			return connected(high, low);
		}

		return connected(low, high);
	}
}
