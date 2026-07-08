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

/**
 * Explicit semantic wrapper for interval topology.
 *
 * <p>{@link Kind#EMPTY} represents an empty or undefined real domain.
 * Unbounded real results are represented by {@link Kind#CONNECTED},
 * {@link Kind#INVERTED}, or {@link Kind#WHOLE} with infinite bounds where
 * needed. {@link Kind#OVERFLOW} is reserved for numeric overflow that cannot be
 * represented safely; recoverable expression-level compositions should avoid
 * producing it before they reach this topology layer.
 */
public final class IntervalSet {

	/**
	 * Interval topology kind.
	 */
	public enum Kind {
		EMPTY,
		CONNECTED,
		INVERTED,
		OVERFLOW,
		WHOLE;
	}

	private final Kind kind;
	private final Interval interval;

	private IntervalSet(Kind kind, Interval interval) {
		this.kind = kind;
		this.interval = interval;
	}

	/**
	 * @return an empty interval set.
	 */
	public static IntervalSet empty() {
		return new IntervalSet(Kind.EMPTY, null);
	}

	/**
	 * @return the whole real line.
	 */
	public static IntervalSet whole() {
		return new IntervalSet(Kind.WHOLE, null);
	}

	/**
	 * @param interval connected interval payload
	 * @return a connected interval set
	 */
	public static IntervalSet connected(Interval interval) {
		return new IntervalSet(Kind.CONNECTED, validatedCopy(interval, "connected"));
	}

	/**
	 * @param low lower bound
	 * @param high upper bound
	 * @return a connected interval set
	 */
	public static IntervalSet connected(double low, double high) {
		validateBounds(low, high, "connected");
		return new IntervalSet(Kind.CONNECTED, new Interval(low, high));
	}

	/**
	 * @param gap excluded middle interval
	 * @return an inverted interval set
	 */
	public static IntervalSet inverted(Interval gap) {
		return new IntervalSet(Kind.INVERTED, validatedCopy(gap, "inverted"));
	}

	/**
	 * @param low lower bound of the excluded gap
	 * @param high upper bound of the excluded gap
	 * @return an inverted interval set
	 */
	public static IntervalSet inverted(double low, double high) {
		validateBounds(low, high, "inverted");
		return new IntervalSet(Kind.INVERTED, new Interval(low, high));
	}

	/**
	 * @return an overflown interval set
	 */
	public static IntervalSet overflow() {
		return new IntervalSet(Kind.OVERFLOW, null);
	}

	private static void validateBounds(double low, double high, String type) {
		if (low > high) {
			throw new IllegalArgumentException(type + " interval requires low <= high");
		}
	}

	private static Interval validatedCopy(Interval interval, String type) {
		if (interval == null) {
			throw new IllegalArgumentException(type + " interval must not be null");
		}
		if (interval.isUndefined()) {
			throw new IllegalArgumentException(type + " interval must not be undefined");
		}
		return new Interval(interval);
	}

	/**
	 * @return topology kind
	 */
	public Kind kind() {
		return kind;
	}

	/**
	 * @return whether this is empty
	 */
	public boolean isEmpty() {
		return kind == Kind.EMPTY;
	}

	/**
	 * @return whether this is whole
	 */
	public boolean isWhole() {
		return kind == Kind.WHOLE;
	}

	/**
	 * @return whether this is connected
	 */
	public boolean isConnected() {
		return kind == Kind.CONNECTED;
	}

	/**
	 * @return whether this is inverted
	 */
	public boolean isInverted() {
		return kind == Kind.INVERTED;
	}

	/**
	 * @return whether this is overflowing.
	 */
	public boolean isOverflow() {
		return kind == Kind.OVERFLOW;
	}

	/**
	 * @return payload interval copy, or null if none
	 */
	public Interval interval() {
		return interval == null ? null : new Interval(interval);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof IntervalSet set)) {
			return false;
		}
		return kind == set.kind && Objects.equals(interval, set.interval);
	}

	@Override
	public int hashCode() {
		return Objects.hash(kind, interval);
	}

	@Override
	public String toString() {
		return interval == null ? "IntervalSet{" + kind + "}"
				: "IntervalSet{" + kind + ": " + interval.toShortString() + "}";
	}

	/**
	 *
	 * @return legacy inverted interval
	 */
	public Interval toInvertedInterval() {
		return Interval.legacyInverted(interval.getLow(), interval.getHigh());
	}
}
