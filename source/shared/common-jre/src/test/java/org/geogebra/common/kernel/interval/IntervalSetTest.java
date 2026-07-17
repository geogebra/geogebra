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

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IntervalSetTest {

	@Test
	void emptyHasNoPayload() {
		IntervalSet set = IntervalSet.empty();

		assertAll(
				() -> assertEquals(IntervalSet.Kind.EMPTY, set.kind()),
				() -> assertTrue(set.isEmpty()),
				() -> assertFalse(set.isWhole()),
				() -> assertFalse(set.isConnected()),
				() -> assertFalse(set.isInverted()),
				() -> assertFalse(set.isOverflow()),
				() -> assertNull(set.interval()));
	}

	@Test
	void wholeHasNoPayload() {
		IntervalSet set = IntervalSet.whole();

		assertAll(
				() -> assertEquals(IntervalSet.Kind.WHOLE, set.kind()),
				() -> assertFalse(set.isEmpty()),
				() -> assertTrue(set.isWhole()),
				() -> assertFalse(set.isConnected()),
				() -> assertFalse(set.isInverted()),
				() -> assertFalse(set.isOverflow()),
				() -> assertNull(set.interval()));
	}

	@Test
	void overflowHasNoPayload() {
		IntervalSet set = IntervalSet.overflow();

		assertAll(
				() -> assertEquals(IntervalSet.Kind.OVERFLOW, set.kind()),
				() -> assertFalse(set.isEmpty()),
				() -> assertFalse(set.isWhole()),
				() -> assertFalse(set.isConnected()),
				() -> assertFalse(set.isInverted()),
				() -> assertTrue(set.isOverflow()),
				() -> assertNull(set.interval()));
	}

	@Test
	void connectedCopiesPayload() {
		IntervalSet set = IntervalSet.connected(new Interval(1, 2));
		assertAll(
				() -> assertEquals(IntervalSet.Kind.CONNECTED, set.kind()),
				() -> assertTrue(set.isConnected()),
				() -> assertEquals(new Interval(1, 2), set.interval())
		);
	}

	@Test
	void singletonConnectedIsAllowed() {
		IntervalSet set = IntervalSet.connected(3, 3);
		assertEquals(new Interval(3, 3), set.interval());
	}

	@Test
	void connectedMayUseInfiniteBounds() {
		assertEquals(new Interval(Double.NEGATIVE_INFINITY, 5),
				IntervalSet.connected(Double.NEGATIVE_INFINITY, 5).interval());
		assertEquals(new Interval(7, Double.POSITIVE_INFINITY),
				IntervalSet.connected(7, Double.POSITIVE_INFINITY).interval());
	}

	@Test
	void connectedWholeIntervalRemainsConnected() {
		IntervalSet set = IntervalSet.connected(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		assertAll(
				() -> assertEquals(IntervalSet.Kind.CONNECTED, set.kind()),
				() -> assertFalse(set.isWhole()),
				() -> assertEquals(new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
				set.interval())
		);
	}

	@Test
	void invertedCopiesGapPayload() {
		IntervalSet set = IntervalSet.inverted(new Interval(1, 2));

		assertAll(
				() -> assertEquals(IntervalSet.Kind.INVERTED, set.kind()),
				() -> assertTrue(set.isInverted()),
				() -> assertEquals(new Interval(1, 2), set.interval())
		);
	}

	@Test
	void connectedNullRejected() {
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.connected(null));
	}

	@Test
	void invertedNullRejected() {
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.inverted(null));
	}

	@Test
	void connectedUndefinedRejected() {
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.connected(undefined()));
	}

	@Test
	void invertedUndefinedRejected() {
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.inverted(undefined()));
	}

	@Test
	void invalidBoundsRejected() {
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.connected(2, 1));
		assertThrows(IllegalArgumentException.class, () -> IntervalSet.inverted(2, 1));
	}

	@Test
	void equalsAndHashCodeUseKindAndPayloadValue() {
		IntervalSet connected = IntervalSet.connected(1, 2);
		IntervalSet sameConnected = IntervalSet.connected(new Interval(1, 2));
		IntervalSet inverted = IntervalSet.inverted(1, 2);

		assertEquals(connected, sameConnected);
		assertEquals(connected.hashCode(), sameConnected.hashCode());
		assertNotEquals(connected, inverted);
		assertNotEquals(IntervalSet.empty(), IntervalSet.whole());
	}

	@Test
	void sourceIntervalMutationDoesNotAffectStoredValue() {
		Interval source = new Interval(1, 2);
		IntervalSet set = IntervalSet.connected(source);

		source.set(10, 20);

		assertEquals(new Interval(1, 2), set.interval());
	}

	@Test
	void returnedPayloadMutationDoesNotAffectStoredValue() {
		IntervalSet set = IntervalSet.connected(1, 2);
		Interval payload = set.interval();

		payload.set(10, 20);

		assertEquals(new Interval(1, 2), set.interval());
	}

	@Test
	void toStringIsReadable() {
		assertEquals("IntervalSet{EMPTY}", IntervalSet.empty().toString());
		assertEquals("IntervalSet{WHOLE}", IntervalSet.whole().toString());
		assertEquals("IntervalSet{OVERFLOW}", IntervalSet.overflow().toString());
		assertEquals("IntervalSet{CONNECTED: [1.0, 2.0]}",
				IntervalSet.connected(1, 2).toString());
		assertEquals("IntervalSet{INVERTED: [1.0, 2.0]}",
				IntervalSet.inverted(1, 2).toString());
	}
}
