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

import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;
import static org.geogebra.common.kernel.interval.IntervalSetOps.isZero;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.zero;
import static org.geogebra.common.kernel.interval.operators.RMath.hasOverflow;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;

/**
 * Multiplication of intervals.
 */
public class IntervalMultiply {

	/**
	 * Multiplies two interval sets under the interval engine's set semantics.
	 *
	 * <p>Connected inputs are handled natively. Exact zero singletons and inverted
	 * sets scaled by finite singletons are handled explicitly. The remaining
	 * non-connected combinations fall back to the legacy compatibility path.
	 *
	 * @param set1 left factor
	 * @param set2 right factor
	 * @return the product set {@code set1 * set2}
	 */
	public IntervalSet computeSet(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return empty();
		}

		if (set1.isOverflow() || set2.isOverflow()) {
			return overflow();
		}

		if (isExactZeroSingleton(set1) || isExactZeroSingleton(set2)) {
			return zero();
		}

		if (set1.isConnected() && set2.isConnected()) {
			return multiplyConnectedSet(set1, set2);
		}

		if (set1.isInverted() && isFiniteSingleton(set2)) {
			return scaleInverted(set1, singletonValue(set2));
		}

		if (set2.isInverted() && isFiniteSingleton(set1)) {
			return scaleInverted(set2, singletonValue(set1));
		}

		return legacyMultiplyFallback(set1, set2);
	}

	private boolean isExactZeroSingleton(IntervalSet set) {
		return isZero(set, 0);
	}

	private boolean isFiniteSingleton(IntervalSet set) {
		return set.isConnected() && connectedInterval(set).isExactSingleton()
				&& Double.isFinite(connectedInterval(set).getLow());
	}

	private double singletonValue(IntervalSet set) {
		return connectedInterval(set).getLow();
	}

	private IntervalSet scaleInverted(IntervalSet set, double factor) {
		if (factor == 0) {
			return zero();
		}
		Interval gap = invertedGap(set);
		if (factor > 0) {
			return inverted(gap.getLow() * factor, gap.getHigh() * factor);
		}

		return inverted(gap.getHigh() * factor, gap.getLow() * factor);
	}

	private IntervalSet multiplyConnectedSet(IntervalSet set1, IntervalSet set2) {
		Interval interval = connectedInterval(set1);
		Interval other = connectedInterval(set2);
		double ac = multiplyBound(interval.getLow(), other.getLow());
		double ad = multiplyBound(interval.getLow(), other.getHigh());
		double bc = multiplyBound(interval.getHigh(), other.getLow());
		double bd = multiplyBound(interval.getHigh(), other.getHigh());
		if (hasGeneratedOverflow(ac, interval.getLow(), other.getLow())
				|| hasGeneratedOverflow(ad, interval.getLow(), other.getHigh())
				|| hasGeneratedOverflow(bc, interval.getHigh(), other.getLow())
				|| hasGeneratedOverflow(bd, interval.getHigh(), other.getHigh())) {
			return overflow();
		}
		double low = Math.min(Math.min(prev(ac), prev(ad)), Math.min(prev(bc), prev(bd)));
		double high = Math.max(Math.max(next(ac), next(ad)), Math.max(next(bc), next(bd)));

		if (Double.isInfinite(low) && low < 0 && Double.isInfinite(high) && high > 0) {
			return IntervalSet.whole();
		}

		return connected(low, high);
	}

	private boolean hasGeneratedOverflow(double result, double factor1, double factor2) {
		return hasOverflow(result) && Double.isFinite(factor1) && Double.isFinite(factor2);
	}

	private double multiplyBound(double a, double b) {
		if ((a == 0 && Double.isInfinite(b)) || (b == 0 && Double.isInfinite(a))) {
			return 0;
		}
		return a * b;
	}

	private IntervalSet legacyMultiplyFallback(IntervalSet set1, IntervalSet set2) {
		return fromLegacy(legacyMultiply(toLegacy(set1), toLegacy(set2)));
	}

	private Interval legacyMultiply(Interval interval, Interval other) {
		if (interval.isWhole() || other.isWhole()) {
			return org.geogebra.common.kernel.interval.IntervalConstants.whole();
		}

		if (interval.isNegativeWithZero()) {
			return legacyMulNegativeWithZeroAnd(interval, other);
		}

		if (isZeroInBetween(interval)) {
			return legacyMulIsZeroInBetween(interval, other);
		}

		if (interval.isPositiveWithZero()) {
			return legacyMulPositiveWithZeroAnd(interval, other);
		}

		return org.geogebra.common.kernel.interval.IntervalConstants.undefined();
	}

	private Interval legacyMulPositiveWithZeroAnd(Interval interval, Interval other) {
		if (other.isNegativeWithZero()) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getLow() * other.getHigh()));
		}

		if (other.isPositiveWithZero()) {
			return new Interval(prev(interval.getLow() * other.getLow()), next(
					interval.getHigh() * other.getHigh()));
		}

		if (isZeroInBetween(other)) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getHigh() * other.getHigh()));
		}
		return org.geogebra.common.kernel.interval.IntervalConstants.undefined();
	}

	private Interval legacyMulIsZeroInBetween(Interval interval, Interval other) {
		if (isZeroInBetween(other)) {
			return new Interval(Math.min(prev(interval.getLow() * other.getHigh()), prev(
					interval.getHigh() * other.getLow())),
					Math.max(next(interval.getLow() * other.getLow()), next(
							interval.getHigh() * other.getHigh())));
		}

		if (other.isNegativeWithZero()) {
			return new Interval(prev(interval.getHigh() * other.getLow()), next(
					interval.getLow() * other.getLow()));

		}

		if (other.isPositiveWithZero()) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getHigh() * other.getHigh()));
		}
		return org.geogebra.common.kernel.interval.IntervalConstants.undefined();
	}

	private boolean isZeroInBetween(Interval interval) {
		return interval.containsExclusive(0);
	}

	private Interval legacyMulNegativeWithZeroAnd(Interval interval, Interval other) {
		if (other.getHigh() <= 0) {
			return new Interval(prev(interval.getHigh() * other.getHigh()), next(
					interval.getLow() * other.getLow()));
		}

		if (isZeroInBetween(other)) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getLow() * other.getLow()));
		}

		if (other.getLow() >= 0) {
			return new Interval(prev(interval.getLow() * other.getHigh()), next(
					interval.getHigh() * other.getLow()));
		}

		if (other.lowEquals(Double.NEGATIVE_INFINITY) && other.getHigh() <= 0) {
			return new Interval(prev(interval.getHigh() * other.getHigh()),
					Double.POSITIVE_INFINITY);
		}

		return org.geogebra.common.kernel.interval.IntervalConstants.undefined();
	}

	double next(double v) {
		return v;
	}

	double prev(double v) {
		return v;
	}
}
