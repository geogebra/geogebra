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

/**
 * Adapter between legacy interval semantics and explicit interval topology.
 */
public final class LegacyIntervalAdapter {

	private LegacyIntervalAdapter() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Convert legacy interval semantics to explicit interval topology.
	 *
	 * @param interval legacy interval
	 * @return explicit interval set
	 */
	public static IntervalSet toIntervalSet(Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException("interval must not be null");
		}

		if (interval.isUndefined()) {
			return IntervalSet.empty();
		}

		if (isLegacyInverted(interval)) {
			return IntervalSet.inverted(interval.getLow(), interval.getHigh());
		}

		if (interval.isWhole()) {
			return IntervalSet.whole();
		}

		return IntervalSet.connected(new Interval(interval));
	}

	/**
	 * Convert explicit interval topology back to legacy interval semantics.
	 *
	 * @param set explicit interval set
	 * @return legacy interval
	 */
	public static Interval toLegacyInterval(IntervalSet set) {
		if (set == null) {
			throw new IllegalArgumentException("interval set must not be null");
		}

		return switch (set.kind()) {
			case EMPTY, OVERFLOW -> IntervalConstants.undefined();
			case WHOLE -> IntervalConstants.whole();
			case CONNECTED -> set.interval();
			case INVERTED -> set.toInvertedInterval();
			default ->
					throw new IllegalStateException("Unsupported interval set kind: " + set.kind());
		};
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
		return Interval.legacyInverted(low, high);
	}

	static boolean isLegacyInverted(Interval interval) {
		return interval != null && interval.hasLegacyInversionFlag();
	}
}
