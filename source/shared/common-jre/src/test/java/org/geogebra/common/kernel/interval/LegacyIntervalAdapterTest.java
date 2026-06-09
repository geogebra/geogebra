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

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LegacyIntervalAdapterTest {

	@Test
	public void toIntervalSetRejectsNull() {
		assertThrows(IllegalArgumentException.class,
				() -> LegacyIntervalAdapter.toIntervalSet(null));
	}

	@Test
	public void toLegacyIntervalRejectsNull() {
		assertThrows(IllegalArgumentException.class,
				() -> LegacyIntervalAdapter.toLegacyInterval(null));
	}

	@Test
	public void legacyUndefinedMapsToEmpty() {
		assertEquals(IntervalSet.empty(), LegacyIntervalAdapter.toIntervalSet(undefined()));
	}

	@Test
	public void legacyWholeMapsToWhole() {
		assertEquals(IntervalSet.whole(), LegacyIntervalAdapter.toIntervalSet(whole()));
	}

	@Test
	public void finiteLegacyIntervalMapsToConnected() {
		assertEquals(IntervalSet.connected(1, 2),
				LegacyIntervalAdapter.toIntervalSet(new Interval(1, 2)));
	}

	@Test
	public void singletonLegacyIntervalMapsToConnected() {
		assertEquals(IntervalSet.connected(3, 3),
				LegacyIntervalAdapter.toIntervalSet(new Interval(3, 3)));
	}

	@Test
	public void semiInfiniteLegacyIntervalsMapToConnected() {
		assertEquals(IntervalSet.connected(Double.NEGATIVE_INFINITY, 5),
				LegacyIntervalAdapter.toIntervalSet(new Interval(Double.NEGATIVE_INFINITY, 5)));
		assertEquals(IntervalSet.connected(7, Double.POSITIVE_INFINITY),
				LegacyIntervalAdapter.toIntervalSet(new Interval(7, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void infinitySingletonsMapToConnected() {
		assertEquals(IntervalSet.connected(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
				LegacyIntervalAdapter.toIntervalSet(positiveInfinity()));
		assertEquals(IntervalSet.connected(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
				LegacyIntervalAdapter.toIntervalSet(negativeInfinity()));
	}

	@Test
	public void emptyMapsBackToLegacyUndefined() {
		assertTrue(LegacyIntervalAdapter.toLegacyInterval(IntervalSet.empty()).isUndefined());
	}

	@Test
	public void wholeMapsBackToLegacyWhole() {
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(IntervalSet.whole());
		assertAll(
				() -> assertTrue(legacy.isWhole()),
				() -> assertEquals(IntervalSet.whole(),
						LegacyIntervalAdapter.toIntervalSet(legacy)));
	}

	@Test
	public void connectedMapsBackToNonInvertedLegacyInterval() {
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(IntervalSet.connected(1, 2));
		assertAll(
				() -> assertEquals(new Interval(1, 2), legacy),
				() -> assertEquals(IntervalSet.connected(1, 2),
						LegacyIntervalAdapter.toIntervalSet(legacy)));
	}

	@Test
	public void invertedMapsBackToLegacyInvertedInterval() {
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(IntervalSet.inverted(1, 2));
		assertAll(
				() -> assertEquals(1, legacy.getLow()),
				() -> assertEquals(2, legacy.getHigh()),
				() -> assertEquals(IntervalSet.inverted(1, 2),
						LegacyIntervalAdapter.toIntervalSet(legacy)));
	}

	@Test
	public void mutatingLegacySourceDoesNotAffectMappedIntervalSet() {
		Interval source = new Interval(1, 2);
		IntervalSet set = LegacyIntervalAdapter.toIntervalSet(source);

		source.set(10, 20);

		assertEquals(IntervalSet.connected(1, 2), set);
	}

	@Test
	public void mutatingLegacyResultDoesNotAffectSourceIntervalSet() {
		IntervalSet source = IntervalSet.connected(1, 2);
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(source);

		legacy.set(10, 20);

		assertEquals(new Interval(1, 2), source.interval());
	}

	@Test
	public void connectedWholeIntervalRoundTripsToWhole() {
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(
				IntervalSet.connected(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
		assertAll(
				() -> assertEquals(new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
						legacy),
				() -> assertEquals(IntervalSet.whole(),
						LegacyIntervalAdapter.toIntervalSet(legacy)));
	}

	@Test
	public void legacyRoundTripPreservesPrecisionSensitiveZeroBehavior() {
		Interval legacy = new Interval(1E-13, 1E-13);
		legacy.setPrecision(0);

		Interval roundTripped = LegacyIntervalAdapter.toLegacyInterval(
				LegacyIntervalAdapter.toIntervalSet(legacy));

		assertAll(
				() -> assertEquals(legacy, roundTripped),
				() -> assertFalse(roundTripped.isZero()));
	}

	@Test
	void overflowIntervalCollapsesToUndefined() {
		Interval legacy = LegacyIntervalAdapter.toLegacyInterval(
				IntervalSet.overflow());
		assertEquals(undefined(), legacy);

	}
}
