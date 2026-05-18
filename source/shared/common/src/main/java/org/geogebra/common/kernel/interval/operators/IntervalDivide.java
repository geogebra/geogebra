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

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.legacyInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.kernel.interval.LegacyIntervalAdapter;

/**
 * Divides interval values using the interval engine's set semantics.
 *
 * <p>The main contract lives in {@link #computeSet(IntervalSet, IntervalSet)}.
 * The legacy {@link Interval} entry point preserves the older API by converting
 * at the boundary.
 */
public class IntervalDivide {

	private final IntervalNodeEvaluator evaluator;

	/**
	 * Creates a divider that can delegate union-based compatibility cases back to
	 * the shared interval evaluator.
	 *
	 * @param evaluator evaluator used for dependent interval operations
	 */
	public IntervalDivide(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * Divide intervals.
	 *
	 * @param divisor interval to divide by.
	 * @return the result of interval divided by divisor.
	 */
	public Interval compute(Interval numerator, Interval divisor) {
		if (numerator.isWhole() && divisor.isUndefined()) {
			return whole();
		}
		if (numerator.isUndefined() || divisor.isUndefined()) {
			return undefined();
		}
		return toLegacy(computeSet(fromLegacy(numerator), fromLegacy(divisor)));
	}

	/**
	 * Divides one interval set by another under the interval engine's set semantics.
	 *
	 * <p>The result may be connected, inverted, whole, or empty depending on the
	 * divisor topology and on whether zero belongs to the divisor.
	 *
	 * @param numerator numerator set
	 * @param divisor divisor set
	 * @return the quotient set {@code numerator / divisor}
	 */
	public IntervalSet computeSet(IntervalSet numerator, IntervalSet divisor) {
		if (numerator.isEmpty() || divisor.isEmpty()) {
			return empty();
		}

		if (numerator.isWhole()) {
			return IntervalSetOps.whole();
		}

		if (numerator.isInverted()) {
			return divideInvertedNumerator(numerator, divisor);
		}

		if (divisor.isInverted()) {
			Interval leftResult = toLegacy(computeSet(numerator, leftRayFromInverted(divisor)));
			Interval rightResult = toLegacy(computeSet(numerator, rightRayFromInverted(divisor)));
			return fromLegacy(evaluator.union(leftResult, rightResult));
		}

		return divideConnectedLike(numerator, divisor);
	}

	private IntervalSet divideInvertedNumerator(IntervalSet numerator, IntervalSet divisor) {
		IntervalSet leftResult = computeSet(leftRayFromInverted(numerator), divisor);
		IntervalSet rightResult = computeSet(rightRayFromInverted(numerator), divisor);
		IntervalSet union = evaluator.unionSet(leftResult, rightResult);
		if (!union.isEmpty()) {
			return union;
		}

		if (leftResult.isConnected() && rightResult.isConnected()) {
			Interval leftInterval = connectedInterval(leftResult);
			Interval rightInterval = connectedInterval(rightResult);
			if (leftInterval.getHigh() <= rightInterval.getLow()) {
				return IntervalSet.inverted(leftInterval.getHigh(), rightInterval.getLow());
			}
			if (rightInterval.getHigh() <= leftInterval.getLow()) {
				return IntervalSet.inverted(rightInterval.getHigh(), leftInterval.getLow());
			}
		}

		return evaluator.unionInvertedSet(leftResult, rightResult);
	}

	private IntervalSet divideConnectedLike(IntervalSet numerator, IntervalSet divisor) {
		if (divisor.isEmpty()) {
			return empty();
		}

		Interval legacyNumerator = toLegacy(numerator);
		Interval legacyDivisor = divisor.isConnected()
				? connectedInterval(divisor) : toLegacy(divisor);

		if (legacyDivisor.hasZero()) {
			return divideByZeroContainingDivisor(legacyNumerator, legacyDivisor);
		}

		if (legacyNumerator.isUndefined() || legacyDivisor.isUndefined()) {
			return empty();
		}

		if (legacyNumerator.isPositiveInfinity()) {
			return fromLegacy(divideSingletonPositiveInfinity(legacyDivisor));
		}

		if (legacyNumerator.isNegative()) {
			return fromLegacy(divideNegativeBy(legacyNumerator, legacyDivisor));
		} else if (legacyNumerator.isPositive()) {
			return fromLegacy(dividePositiveBy(legacyNumerator, legacyDivisor));
		}

		return fromLegacy(divideMixedBy(legacyNumerator, legacyDivisor));
	}

	private IntervalSet divideByZeroContainingDivisor(Interval numerator, Interval divisor) {
		if (numerator.hasZero()) {
			return IntervalSetOps.whole();
		}

		if (divisor.isZeroWithDelta(0)) {
			return empty();
		}

		if (numerator.isNegative()) {
			return fromLegacy(divideNegativeBy(numerator, divisor));
		}

		if (numerator.isPositive()) {
			return fromLegacy(dividePositiveBy(numerator, divisor));
		}

		throw new IllegalStateException("Unhandled zero-containing divisor case: "
				+ numerator + " / " + divisor);
	}

	private Interval divideSingletonPositiveInfinity(Interval divisor) {
		if (divisor.isPositiveInfinity() || divisor.isNegativeInfinity()) {
			return zero();
		}

		if (divisor.isPositive()) {
			return positiveInfinity();
		} else if (divisor.isNegative()) {
			return negativeInfinity();
		}

		return whole();
	}

	private Interval divideNegativeBy(Interval numerator, Interval divisor) {
		if (divisor.isZero()) {
			return numerator.isSingleton() ? negativeInfinity() : undefined();
		}

		if (divisor.isNegative()) {
			return divideNegativeByNegative(numerator, divisor);
		}

		if (divisor.highEquals(0)) {
			return divideNegativeByNegativeWithZeroAsHigh(prev(numerator.getHigh()
					/ divisor.getLow()));
		}

		if (hasZeroInBetween(divisor)) {
			return legacyInverted(next(numerator.getHigh() / divisor.getHigh()),
					prev(numerator.getHigh() / divisor.getLow()));
		}

		if (divisor.lowEquals(0)) {
			return new Interval(Double.NEGATIVE_INFINITY,
					next(numerator.getHigh() / divisor.getHigh()));
		}

		if (divisor.getLow() > 0) {
			return divideNegativeByPositive(numerator, divisor);
		}
		return undefined();
	}

	private Interval divideNegativeByPositive(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.POSITIVE_INFINITY)) {
			return new Interval(prev(numerator.getLow() / divisor.getLow()), 0);
		}
		return new Interval(prev(numerator.getLow() / divisor.getLow()),
				next(numerator.getHigh() / divisor.getHigh()));
	}

	private Interval dividePositiveBy(Interval numerator, Interval divisor) {
		if (divisor.isZero()) {
			return 1 / divisor.getLow() > 0 ? positiveInfinity() : negativeInfinity();
		}

		if (divisor.highEquals(0)) {
			return new Interval(Double.NEGATIVE_INFINITY,
					next(numerator.getLow() / divisor.getLow()));
		}
		if (hasZeroInBetween(divisor)) {
			return legacyInverted(next(numerator.getLow() / divisor.getLow()), prev(
					numerator.getLow() / divisor.getHigh()));
		}
		if (divisor.lowEquals(0)) {
			return dividePositiveByNegativeWithZeroAsHigh(numerator.getLow(), divisor.getHigh());
		}

		if (divisor.isPositive()) {
			return dividePositiveByPositive(numerator, divisor);
		}

		if (divisor.isNegative()) {
			if (numerator.highEquals(Double.POSITIVE_INFINITY)) {
				return new Interval(Double.NEGATIVE_INFINITY,
						next(numerator.getLow() / divisor.getLow()));
			}
			return new Interval(prev(numerator.getHigh() / divisor.getHigh()),
					next(numerator.getLow() / divisor.getLow()));
		}
		return undefined();
	}

	private Interval dividePositiveByNegativeWithZeroAsHigh(double a1, double b2) {
		return new Interval(prev(a1 / b2), Double.POSITIVE_INFINITY);
	}

	private Interval divideNegativeByNegativeWithZeroAsHigh(double low) {
		return new Interval(low, Double.POSITIVE_INFINITY);
	}

	private Interval divideNegativeByNegative(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.NEGATIVE_INFINITY)) {
			return new Interval(0, next(numerator.getLow() / divisor.getHigh()));
		}
		return new Interval(prev(numerator.getHigh() / divisor.getLow()),
				next(numerator.getLow() / divisor.getHigh()));
	}

	private Interval dividePositiveByPositive(Interval numerator, Interval divisor) {
		if (divisor.lowEquals(Double.POSITIVE_INFINITY)) {
			return new Interval(prev(numerator.getLow() / divisor.getLow()), 0);
		}
		return new Interval(prev(numerator.getLow() / divisor.getHigh()),
				next(numerator.getHigh() / divisor.getLow()));
	}

	// just for the notation of the paper
	static double prev(double v) {
		return v;
	}

	static double next(double v) {
		return v;
	}

	private Interval divideMixedBy(Interval numerator, Interval divisor) {
		if (divisor.isNegative()) {
			double low = prev(numerator.getHigh() / divisor.getHigh());
			double high = next(numerator.getLow() / divisor.getHigh());
			return LegacyIntervalAdapter.toIntervalSet(numerator).isInverted()
					? legacyInverted(low, high)
					: new Interval(low, high);
		}

		if (divisor.isPositive()) {
			if (numerator.lowEquals(Double.NEGATIVE_INFINITY)) {
				if (divisor.highEquals(Double.POSITIVE_INFINITY)) {
					return new Interval(Double.NEGATIVE_INFINITY,
							next(numerator.getHigh() / divisor.getLow()));
				}
				return new Interval(Double.NEGATIVE_INFINITY,
						next(numerator.getHigh() / divisor.getHigh()));
			}

			double low = prev(numerator.getLow() / divisor.getLow());
			double high = next(numerator.getHigh() / divisor.getLow());
			return LegacyIntervalAdapter.toIntervalSet(numerator).isInverted()
					? legacyInverted(low, high)
					: new Interval(low, high);
		}
		return undefined();
	}

	private static boolean hasZeroInBetween(final Interval interval) {
		return interval.containsExclusive(0);
	}
}
