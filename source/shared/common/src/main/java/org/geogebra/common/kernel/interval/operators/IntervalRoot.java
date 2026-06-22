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

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.util.DoubleUtil;

public class IntervalRoot {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalRoot(IntervalNodeEvaluator evaluator) {

		this.evaluator = evaluator;
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, Interval other) {
		if (!other.isSingleton()) {
			return undefined();
		}

		double power = other.getLow();
		return compute(interval, power);
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, double n) {
		return toLegacy(computeSet(fromLegacy(interval), n));
	}

	IntervalSet computeSet(IntervalSet set, double n) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isOverflow()) {
			return overflow();
		}

		if (isPositiveEven(n)) {
			return positiveEvenRoot(set, n);
		}

		if (set.isInverted()) {
			if (isOdd(n)) {
				return inverted(compute(invertedGap(set), n));
			}

			return evaluator.unionInvertedSet(computeSet(leftRayFromInverted(set), n),
					computeSet(rightRayFromInverted(set), n));
		}

		double power = 1 / n;
		if (isPositiveOdd(n)) {
			Interval interval = connectedInterval(set);
			return IntervalSet.connected(oddFractionPower(interval.getLow(), power),
					oddFractionPower(interval.getHigh(), power));
		}
		IntervalSet result = evaluator.powSet(set, power);
		if (result.isConnected()) {
			Interval interval1 = connectedInterval(result);
			return connected(Math.abs(interval1.getLow()) < interval1.precision
							? 0
							: interval1.getLow(),
					Math.abs(interval1.getHigh()) < interval1.precision
							? 0
							: interval1.getHigh());

		}
		return result;
	}

	private IntervalSet positiveEvenRoot(IntervalSet set, double n) {
		if (set.isWhole()) {
			return connected(0, Double.POSITIVE_INFINITY);
		}

		if (set.isInverted()) {
			return positiveEvenRootOfInverted(set, n);
		}

		return positiveEvenRootOfConnected(connectedInterval(set), n);
	}

	private IntervalSet positiveEvenRootOfConnected(Interval interval, double n) {
		if (interval.getHigh() < 0) {
			return empty();
		}

		return connected(rootLow(Math.max(0, interval.getLow()), n),
				rootHigh(interval.getHigh(), n));
	}

	private IntervalSet positiveEvenRootOfInverted(IntervalSet set, double n) {
		Interval gap = invertedGap(set);
		if (gap.getHigh() <= 0) {
			return connected(0, Double.POSITIVE_INFINITY);
		}

		IntervalSet right = connected(rootLow(gap.getHigh(), n), Double.POSITIVE_INFINITY);
		if (gap.getLow() < 0) {
			return right;
		}

		double leftHigh = rootHigh(gap.getLow(), n);
		double rightLow = rootLow(gap.getHigh(), n);
		return leftHigh >= rightLow
				? connected(0, Double.POSITIVE_INFINITY)
				: inverted(leftHigh, rightLow);
	}

	private double rootLow(double value, double n) {
		if (value == 0 || Double.isInfinite(value)) {
			return value;
		}
		return RMath.prev(Math.pow(value, 1 / n));
	}

	private double rootHigh(double value, double n) {
		if (value == 0 || Double.isInfinite(value)) {
			return value;
		}
		return RMath.next(Math.pow(value, 1 / n));
	}

	private double oddFractionPower(double x, double power) {
		double fractionPower = Math.pow(Math.abs(x), power);
		return x > 0
				? Math.max(IntervalConstants.PRECISION, fractionPower)
				: Math.min(-IntervalConstants.PRECISION, -fractionPower);
	}

	private boolean isPositiveOdd(double n) {
		return n > 0 && isOdd(n);
	}

	private boolean isPositiveEven(double n) {
		return n > 0 && DoubleUtil.isInteger(n) && ((int) n) % 2 == 0;
	}

	private boolean isOdd(double n) {
		return DoubleUtil.isInteger(n) && ((int) n) % 2 != 0;
	}
}
