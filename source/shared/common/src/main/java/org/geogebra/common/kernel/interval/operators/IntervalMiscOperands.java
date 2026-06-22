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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalSet.empty;
import static org.geogebra.common.kernel.interval.IntervalSet.inverted;
import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.halfOpenLeft;
import static org.geogebra.common.kernel.interval.IntervalSetOps.halfOpenRight;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;
import static org.geogebra.common.kernel.interval.operators.RMath.hasOverflow;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;

public class IntervalMiscOperands {

	private final IntervalNodeEvaluator evaluator;

	/**
	 * Creates the helper for non-arithmetic interval operations that still depend
	 * on the shared evaluator for composed interval behavior.
	 *
	 * @param evaluator evaluator used for dependent interval operations
	 */
	public IntervalMiscOperands(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * Returns the exponential image of an interval set.
	 *
	 * @param set input set
	 * @return {@code exp(set)}
	 */
	public IntervalSet exp(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isOverflow()) {
			return overflow();
		}

		if (set.isInverted()) {
			return connected(0, POSITIVE_INFINITY);
		}

		if (!set.isConnected()) {
			return empty();
		}

		Interval interval = connectedInterval(set);
		double expLow = Math.exp(interval.getLow());
		double expHigh = Math.exp(interval.getHigh());

		if (isUnderflow(expLow) || isUnderflow(expHigh)) {
			return overflow();
		}
		double prev = lowerExpBound(expLow);
		double next = RMath.next(expHigh);

		if (hasOverflow(prev) || hasOverflow(next)) {
			return overflow();
		}

		return connected(prev, next);
	}

	private static double lowerExpBound(double expLow) {
		double previous = RMath.prev(expLow);
		return previous == 0 ? expLow : previous;
	}

	private static boolean isUnderflow(double expLow) {
		return expLow == 0 && Double.isFinite(expLow);
	}

	/**
	 * Returns the natural logarithm image of an interval set.
	 *
	 * @param set input set
	 * @return {@code log(set)}
	 */
	public IntervalSet log(IntervalSet set) {
		if (set.isOverflow()) {
			return overflow();
		}

		if (set.isEmpty()) {
			return empty();
		}

		if (set.isInverted()) {
			return evaluator.computeUnaryInverted(set, this::log);
		}

		if (set.isWhole()) {
			return whole();
		}

		Interval interval = connectedInterval(set);
		double high = interval.getHigh();
		if (high >= 0) {
			double low = interval.getLow();
			if (interval.isExactSingleton() && interval.isOne()) {
				return connected(0, 0);
			}
			return connected(low <= 0 ? NEGATIVE_INFINITY : RMath.prev(Math.log(low)),
					RMath.next(Math.log(high)));
		}

		return IntervalSetOps.empty();
	}

	/**
	 * Returns the base-2 logarithm image of an interval set.
	 *
	 * @param set input set
	 * @return {@code log_2(set)}
	 */
	public IntervalSet log2(IntervalSet set) {
		if (set.isOverflow()) {
			return overflow();
		}

		if (!set.isEmpty()) {
			IntervalSet logExp2 = evaluator.logSet(connected(2, 2));
			return evaluator.divideSet(log(set), logExp2);
		}

		return set;
	}

	/**
	 * Returns the base-10 logarithm image of an interval set.
	 *
	 * @param set input set
	 * @return {@code log_10(set)}
	 */
	public IntervalSet log10(IntervalSet set) {
		if (set.isOverflow()) {
			return overflow();
		}

		if (!set.isEmpty()) {
			IntervalSet logExp10 = evaluator.logSet(connected(10, 10));
			return evaluator.divideSet(log(set), logExp10);
		}

		return set;
	}

	/**
	 * Returns the hull of two interval sets.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the smallest interval set returned by the hull operation that
	 *         contains both inputs
	 */
	public IntervalSet hull(IntervalSet set1, IntervalSet set2) {
		if (set1.isOverflow() || set2.isOverflow()) {
			return overflow();
		}

		if (set1.isEmpty() && set2.isEmpty()) {
			return empty();
		} else if (set1.isEmpty()) {
			return set2;
		} else if (set2.isEmpty()) {
			return set1;
		} else if (set1.isWhole() || set2.isWhole()
				|| set1.isInverted() || set2.isInverted()) {
			return whole();
		} else {
			Interval interval1 = connectedInterval(set1);
			Interval interval2 = connectedInterval(set2);
			return connected(Math.min(interval1.getLow(), interval2.getLow()),
					Math.max(interval1.getHigh(), interval2.getHigh()));
		}
	}

	/**
	 * Returns the intersection of two interval sets.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the common part of the two sets
	 */
	public IntervalSet intersect(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return empty();
		}

		if (set1.isOverflow() || set2.isOverflow()) {
			return overflow();
		}

		if (set1.isWhole()) {
			return set2;
		}

		if (set2.isWhole()) {
			return set1;
		}

		if (set1.isInverted() && set2.isInverted()) {
			Interval gap1 = invertedGap(set1);
			Interval gap2 = invertedGap(set2);
			double low1 = gap1.getLow();
			double low2 = gap2.getLow();
			double high1 = gap1.getHigh();
			double high2 = gap2.getHigh();
			if (Math.max(low1, low2) <= Math.min(high1, high2)) {
				return inverted(Math.min(low1, low2), Math.max(high1, high2));
			}
			return empty();
		}

		if (set1.isInverted()) {
			Interval gap = invertedGap(set1);
			return difference(set2, connected(gap.getLow(), gap.getHigh()));
		}

		if (set2.isInverted()) {
			Interval gap = invertedGap(set2);
			return difference(set1, connected(gap.getLow(), gap.getHigh()));
		}

		Interval interval1 = connectedInterval(set1);
		Interval interval2 = connectedInterval(set2);
		double low = Math.max(interval1.getLow(), interval2.getLow());
		double high = Math.min(interval1.getHigh(), interval2.getHigh());
		if (low <= high) {
			return connected(low, high);
		}
		return empty();
	}

	/**
	 * Returns the union of two interval sets when that union is representable by
	 * the current set model.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the union of the two sets, or {@link IntervalSet#empty()} when the
	 *         union is not representable as one connected, inverted, whole, or
	 *         empty result
	 */
	public IntervalSet union(IntervalSet set1, IntervalSet set2) {
		if (set1.isOverflow() || set2.isOverflow()) {
			return overflow();
		}

		if (set1.isEmpty()) {
			return set2;
		}

		if (set2.isEmpty()) {
			return set1;
		}

		if (set1.isWhole() || set2.isWhole()) {
			return whole();
		}

		if (set1.isInverted() && set2.isInverted()) {
			Interval gap1 = invertedGap(set1);
			Interval gap2 = invertedGap(set2);
			double low = Math.max(gap1.getLow(), gap2.getLow());
			double high = Math.min(gap1.getHigh(), gap2.getHigh());
			return low <= high ? inverted(low, high) : whole();
		}

		if (set1.isInverted()) {
			return unionInvertedWithConnected(set1, set2);
		}

		if (set2.isInverted()) {
			return unionInvertedWithConnected(set2, set1);
		}

		if (!isOverlap(set1, set2)) {
			return empty();
		}
		Interval interval1 = connectedInterval(set1);
		Interval interval2 = connectedInterval(set2);
		return connected(Math.min(interval1.getLow(), interval2.getLow()),
				Math.max(interval1.getHigh(), interval2.getHigh()));
	}

	/**
	 * Tests whether two connected interval sets overlap.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return {@code true} iff the connected payloads intersect
	 */
	public static boolean isOverlap(IntervalSet set1, IntervalSet set2) {
		if (set1.isOverflow() || set2.isOverflow()) {
			return false;
		}

		if (set1.isEmpty() || set2.isEmpty()) {
			return false;
		}

		Interval interval1 = connectedInterval(set1);
		Interval interval2 = connectedInterval(set2);
		double low1 = interval1.getLow();
		double high1 = interval1.getHigh();
		double low2 = interval2.getLow();
		double high2 = interval2.getHigh();
		return (low1 <= low2 && low2 <= high1)
				|| (low2 <= low1 && low1 <= high2);
	}

	private IntervalSet unionInvertedWithConnected(IntervalSet invertedSet,
			IntervalSet connectedSet) {
		Interval gap = invertedGap(invertedSet);
		Interval interval = connectedInterval(connectedSet);
		double gapLow = gap.getLow();
		double gapHigh = gap.getHigh();
		double low = interval.getLow();
		double high = interval.getHigh();

		if (high < gapLow || low > gapHigh) {
			return invertedSet;
		}

		if (low <= gapLow && high >= gapHigh) {
			return whole();
		}

		if (low <= gapLow) {
			return inverted(high, gapHigh);
		}

		if (high >= gapHigh) {
			return inverted(gapLow, low);
		}

		return empty();
	}

	/**
	 * Returns the set difference of two interval sets.
	 *
	 * @param set1 minuend
	 * @param set2 subtrahend
	 * @return {@code set1 \ set2}, or {@link IntervalSet#empty()} when the current
	 *         set model cannot represent the result as a single value
	 */
	public IntervalSet difference(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isWhole()) {
			return empty();
		}

		if (set1.isOverflow() || set2.isOverflow()) {
			return overflow();
		}

		if (isOverlap(set1, set2)) {
			Interval interval1 = connectedInterval(set1);
			Interval interval2 = connectedInterval(set2);
			double low1 = interval1.getLow();
			double low2 = interval2.getLow();
			double high1 = interval1.getHigh();
			double high2 = interval2.getHigh();
			if (low1 < low2 && high2 < high1) {
				return empty();
			}

			if ((low2 <= low1 && high2 >= high1)
					|| (low2 <= low1 && high2 == POSITIVE_INFINITY)
					|| (high2 >= high1
					&& low2 == NEGATIVE_INFINITY)) {
				return empty();
			}

			if (low2 <= low1) {
				return halfOpenLeft(high2, high1);
			}

			return halfOpenRight(low1, low2);

		}
		return set1;
	}

	/**
	 * Returns the absolute-value image of an interval set.
	 *
	 * @param set input set
	 * @return {@code abs(set)}
	 */
	public IntervalSet abs(IntervalSet set) {
		if (set.isEmpty()) {
			return set;
		}

		if (set.isOverflow()) {
			return overflow();
		}

		if (set.isWhole()) {
			return connected(0, POSITIVE_INFINITY);
		}

		if (set.isInverted()) {
			return absInverted(set);
		}

		Interval interval = connectedInterval(set);
		if (interval.getLow() >= 0) {
			return set;
		}

		if (interval.getHigh() <= 0) {
			return connected(-interval.getHigh(), -interval.getLow());
		}

		return connected(0, Math.max(-interval.getLow(), interval.getHigh()));
	}

	private static IntervalSet absInverted(IntervalSet set) {
		if (!set.isInverted()) {
			throw new IllegalArgumentException("Set is not INVERTED");
		}
		Interval gap = invertedGap(set);
		double low = gap.getLow();
		double high = gap.getHigh();

			if (low >= 0 || high <= 0) {
				return connected(0, POSITIVE_INFINITY);
			} else {
				return connected(Math.min(-low, high), POSITIVE_INFINITY);
			}
	}
}
