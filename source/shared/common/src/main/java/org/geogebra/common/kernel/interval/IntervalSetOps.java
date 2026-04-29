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

package org.geogebra.common.kernel.interval;

import java.util.Objects;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.interval.operators.RMath;
import org.geogebra.common.util.DoubleUtil;

/**
 * Semantic helpers for working with {@link IntervalSet} topology.
 *
 * <p>This class exposes small, explicit operations that either:
 * <ul>
 * <li>construct a specific {@link IntervalSet} topology,</li>
 * <li>read a topology-specific payload under a clear precondition, or</li>
 * <li>cross the legacy {@link Interval} boundary deliberately.</li>
 * </ul>
 *
 * <p>The methods here should describe what interval topology means, not hide it.
 */
public final class IntervalSetOps {

	public static final double INTERVAL_PRECISION = Kernel.MAX_PRECISION;

	private IntervalSetOps() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @return the empty interval set, representing no real values
	 */
	public static IntervalSet empty() {
		return IntervalSet.empty();
	}

	/**
	 * @return the whole real line
	 */
	public static IntervalSet whole() {
		return IntervalSet.whole();
	}

	/**
	 * Constructs a connected interval set with the given bounds.
	 *
	 * @param low lower bound
	 * @param high upper bound
	 * @return the connected set {@code [low, high]}
	 */
	public static IntervalSet connected(double low, double high) {
		return IntervalSet.connected(low, high);
	}

	/**
	 * @return the connected singleton set {@code {0}}
	 */
	public static IntervalSet zero() {
		return IntervalSet.connected(0, 0);
	}

	/**
	 * @return the connected singleton set {@code {1}}
	 */
	public static IntervalSet one() {
		return IntervalSet.connected(1, 1);
	}

	/**
	 * Wraps a connected legacy interval as a connected {@link IntervalSet}.
	 *
	 * @param interval connected interval payload
	 * @return the equivalent connected interval set
	 */
	public static IntervalSet connected(Interval interval) {
		return IntervalSet.connected(interval);
	}

	/**
	 * Constructs an inverted interval set.
	 *
	 * @param low lower end of the excluded gap
	 * @param high upper end of the excluded gap
	 * @return the set {@code (-inf, low] U [high, +inf)}
	 */
	public static IntervalSet inverted(double low, double high) {
		return IntervalSet.inverted(low, high);
	}

	/**
	 * Wraps a legacy inverted payload as an inverted {@link IntervalSet}.
	 *
	 * @param interval gap payload of an inverted set
	 * @return the equivalent inverted interval set
	 */
	public static IntervalSet inverted(Interval interval) {
		return IntervalSet.inverted(interval);
	}

	/**
	 * Converts a legacy interval into the equivalent {@link IntervalSet} topology.
	 *
	 * @param interval legacy interval representation
	 * @return the corresponding interval set
	 */
	public static IntervalSet fromLegacy(Interval interval) {
		return LegacyIntervalAdapter.toIntervalSet(interval);
	}

	/**
	 * Converts an {@link IntervalSet} into the legacy interval representation.
	 *
	 * @param set interval set to convert
	 * @return the legacy interval carrying the same topology as far as the legacy model allows
	 */
	public static Interval toLegacy(IntervalSet set) {
		return LegacyIntervalAdapter.toLegacyInterval(set);
	}

	/**
	 * Returns the payload of a connected interval set.
	 *
	 * @param set connected interval set
	 * @return the connected interval payload
	 * @throws IllegalArgumentException if {@code set} is not connected
	 */
	public static Interval connectedInterval(IntervalSet set) {
		validateSet(set, IntervalSet.Kind.CONNECTED, "connected interval");
		return set.interval();
	}

	/**
	 * Returns the excluded gap payload of an inverted interval set.
	 *
	 * @param set inverted interval set
	 * @return the finite gap whose complement defines the inverted set
	 * @throws IllegalArgumentException if {@code set} is not inverted
	 */
	public static Interval invertedGap(IntervalSet set) {
		validateSet(set, IntervalSet.Kind.INVERTED, "inverted gap");
		return set.interval();
	}

	/**
	 * Constructs a legacy inverted interval payload.
	 *
	 * <p>This is a boundary helper for legacy compatibility. It does not create an
	 * {@link IntervalSet}; it creates the legacy interval encoding for
	 * {@code (-inf, low] U [high, +inf)}.
	 *
	 * @param low lower end of the excluded gap
	 * @param high upper end of the excluded gap
	 * @return legacy inverted interval payload
	 */
	public static Interval legacyInverted(double low, double high) {
		return LegacyIntervalAdapter.legacyInverted(low, high);
	}

	/**
	 * Tests whether the set is the zero singleton within the default interval precision.
	 *
	 * @param set set to test
	 * @return {@code true} iff the set is connected and represents zero within
	 *         {@link #INTERVAL_PRECISION}
	 */
	public static boolean isZero(IntervalSet set) {
		return isZero(set, INTERVAL_PRECISION);
	}

	/**
	 * Tests whether the set is the zero singleton within the given tolerance.
	 *
	 * @param set set to test
	 * @param delta tolerance used by the connected payload zero test
	 * @return {@code true} iff the set is connected and represents zero within {@code delta}
	 */
	public static boolean isZero(IntervalSet set, double delta) {
		return set != null
				&& set.isConnected()
				&& connectedInterval(set).isZeroWithDelta(delta);
	}

	/**
	 * Tests whether zero belongs to the represented set.
	 *
	 * @param set set to test
	 * @return {@code true} iff {@code 0 is the element of set}
	 */
	public static boolean hasZero(IntervalSet set) {
		if (set == null || set.isEmpty()) {
			return false;
		}

		if (set.isWhole()) {
			return true;
		}

		if (set.isConnected()) {
			return connectedInterval(set).contains(0);
		}

		Interval gap = invertedGap(set);
		return 0 < gap.getLow() || 0 > gap.getHigh();
	}

	private static void validateSet(IntervalSet set, IntervalSet.Kind expectedKind, String value) {
		if (Objects.requireNonNull(set).kind() != expectedKind) {
			throw new IllegalArgumentException(value + " requires kind " + expectedKind);
		}
	}

	/**
	 * Returns the left ray represented by an inverted interval set.
	 *
	 * @param set inverted interval set
	 * @return {@code (-inf, gap.low]}
	 * @throws IllegalArgumentException if {@code set} is not inverted
	 */
	public static IntervalSet leftRayFromInverted(IntervalSet set) {
		if (set == null) {
			throw new IllegalArgumentException("set must not be null");
		}
		if (!set.isInverted()) {
			throw new IllegalArgumentException("set must be inverted");
		}

		Interval gap = invertedGap(set);
		return connected(Double.NEGATIVE_INFINITY, gap.getLow());
	}

	/**
	 * Returns the right ray represented by an inverted interval set.
	 *
	 * @param set inverted interval set
	 * @return {@code [gap.high, +inf)}
	 * @throws IllegalArgumentException if {@code set} is not inverted
	 */
	public static IntervalSet rightRayFromInverted(IntervalSet set) {
		if (set == null) {
			throw new IllegalArgumentException("set must not be null");
		}
		if (!set.isInverted()) {
			throw new IllegalArgumentException("set must be inverted");
		}

		Interval gap = invertedGap(set);
		return connected(gap.getHigh(), Double.POSITIVE_INFINITY);
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the set is connected and strictly positive
	 */
	public static boolean isPositive(IntervalSet set) {
		return set != null
				&& set.isConnected()
				&& connectedInterval(set).isPositive();
	}

	/**
	 * @param set connected set to test
	 * @return {@code true} iff the connected payload is a singleton
	 */
	public static boolean isSingleton(IntervalSet set) {
		return set.isConnected() && connectedInterval(set).isSingleton();
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the set is the connected singleton {@code {1}}
	 */
	public static boolean isOne(IntervalSet set) {
		return set.isConnected() && connectedInterval(set).isOne();
	}

	/**
	 * Constructs a connected interval whose left bound is excluded by moving it
	 * outward to the next representable double.
	 *
	 * @param a left bound to exclude
	 * @param b right bound to keep
	 * @return an interval approximating {@code (a, b]}
	 */
	public static IntervalSet halfOpenLeft(double a, double b) {
		return connected(RMath.next(a), b);
	}

	/**
	 * Constructs a connected interval whose right bound is excluded by moving it
	 * outward to the previous representable double.
	 *
	 * @param a left bound to keep
	 * @param b right bound to exclude
	 * @return an interval approximating {@code [a, b)}
	 */
	public static IntervalSet halfOpenRight(double a, double b) {
		return connected(a, RMath.prev(b));
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the set is a connected infinite singleton
	 */
	public static boolean isInfiniteSingleton(IntervalSet set) {
		return set != null
				&& set.isConnected()
				&& (connectedInterval(set).isPositiveInfinity()
				|| connectedInterval(set).isNegativeInfinity());
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the represented set contains either {@code -inf} or {@code +inf}
	 */
	public static boolean hasInfinity(IntervalSet set) {
		if (set.isEmpty()) {
			return false;
		}

		if (set.isInverted() || set.isWhole()) {
			return true;
		}

		Interval interval = connectedInterval(set);
		return DoubleUtil.isEqual(interval.getLow(), Double.NEGATIVE_INFINITY)
				|| DoubleUtil.isEqual(interval.getHigh(), Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns the additive inverse of the set.
	 *
	 * <p>This currently crosses the legacy boundary for compatibility.
	 *
	 * @param set set to negate
	 * @return the set {@code -set}
	 */
	public static IntervalSet negative(IntervalSet set) {
		return fromLegacy(toLegacy(set).negative());
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the legacy-compatible interpretation is non-negative
	 */
	public static boolean isPositiveWithZero(IntervalSet set) {
		return toLegacy(set).isPositiveWithZero();
	}

	/**
	 * Reconstructs an inverted interval set from the two connected outer rays that bound its gap.
	 *
	 * @param left left connected result ending at the lower gap boundary
	 * @param right right connected result starting at the upper gap boundary
	 * @return the inverted set whose excluded gap lies between the two results
	 */
	public static IntervalSet invertedGapFromSeparatedResults(IntervalSet left, IntervalSet right) {
		Interval leftInterval = connectedInterval(left);
		Interval rightInterval = connectedInterval(right);
		return inverted(leftInterval.getHigh(), rightInterval.getLow());
	}

	/**
	 * @param set set to test
	 * @return {@code true} iff the set is connected and its bounds are exactly equal
	 */
	public static boolean isExactSingleton(IntervalSet set) {
		if (!set.isConnected()) {
			return false;
		}
		Interval interval = connectedInterval(set);
		return DoubleUtil.isEqual(interval.getLow(), interval.getHigh(), 0);
	}
}
