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
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IntervalSetOpsTest {

	@Test
	void constructionHelpersMirrorIntervalSetKinds() {
		assertAll(
				() -> assertEquals(IntervalSet.Kind.EMPTY, IntervalSetOps.empty().kind()),
				() -> assertEquals(IntervalSet.Kind.WHOLE, IntervalSetOps.whole().kind()),
				() -> assertEquals(IntervalSet.Kind.CONNECTED,
						IntervalSetOps.connected(1, 2).kind()),
				() -> assertEquals(IntervalSet.Kind.CONNECTED,
						IntervalSetOps.connected(new Interval(1, 2)).kind()),
				() -> assertEquals(IntervalSet.Kind.INVERTED,
						IntervalSetOps.inverted(1, 2).kind()));
	}

	@Test
	void payloadBearingConstructorsRejectInvalidInput() {
		assertAll(
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.connected((Interval) null)),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.inverted((Interval) null)),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.connected(undefined())),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.inverted(undefined())));
	}

	@Test
	void legacyBridgeRejectsNull() {
		assertAll(
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.fromLegacy(null)),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.toLegacy(null)));
	}

	@Test
	void extractionReturnsConnectedAndInvertedPayloads() {
		Interval connected = IntervalSetOps.connectedInterval(IntervalSetOps.connected(1, 2));
		Interval invertedGap = IntervalSetOps.invertedGap(IntervalSetOps.inverted(3, 4));

		assertAll(
				() -> assertEquals(new Interval(1, 2), connected),
				() -> assertEquals(new Interval(3, 4), invertedGap));
	}

	@Test
	void extractionReturnsDefensiveCopies() {
		IntervalSet connectedSet = IntervalSetOps.connected(1, 2);
		IntervalSet invertedSet = IntervalSetOps.inverted(3, 4);
		Interval connected = IntervalSetOps.connectedInterval(connectedSet);
		Interval invertedGap = IntervalSetOps.invertedGap(invertedSet);

		connected.set(10, 20);
		invertedGap.set(30, 40);

		assertAll(
				() -> assertEquals(new Interval(1, 2),
						IntervalSetOps.connectedInterval(connectedSet)),
				() -> assertEquals(new Interval(3, 4),
						IntervalSetOps.invertedGap(invertedSet)));
	}

	@Test
	void extractionRejectsWrongKinds() {
		assertAll(
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.connectedInterval(IntervalSetOps.inverted(1, 2))),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.connectedInterval(IntervalSetOps.empty())),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.connectedInterval(IntervalSetOps.whole())),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.invertedGap(IntervalSetOps.connected(1, 2))),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.invertedGap(IntervalSetOps.empty())),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.invertedGap(IntervalSetOps.whole())));
	}

	@Test
	void toLegacyMapsAllKinds() {
		Interval connected = IntervalSetOps.toLegacy(IntervalSetOps.connected(1, 2));
		Interval inverted = IntervalSetOps.toLegacy(IntervalSetOps.inverted(3, 4));
		Interval whole = IntervalSetOps.toLegacy(IntervalSetOps.whole());
		Interval empty = IntervalSetOps.toLegacy(IntervalSetOps.empty());

		assertAll(
				() -> assertEquals(new Interval(1, 2), connected),
				() -> assertEquals(IntervalSetOps.connected(1, 2),
						IntervalSetOps.fromLegacy(connected)),
				() -> assertEquals(3, inverted.getLow()),
				() -> assertEquals(4, inverted.getHigh()),
				() -> assertEquals(IntervalSetOps.inverted(3, 4),
						IntervalSetOps.fromLegacy(inverted)),
				() -> assertTrue(whole.isWhole()),
				() -> assertTrue(empty.isUndefined()));
	}

	@Test
	void fromLegacyMirrorsAdapterSemantics() {
		Interval inverted = legacyInverted(1, 2);

		assertAll(
				() -> assertEquals(IntervalSet.connected(1, 2),
						IntervalSetOps.fromLegacy(new Interval(1, 2))),
				() -> assertEquals(IntervalSet.inverted(1, 2),
						IntervalSetOps.fromLegacy(inverted)),
				() -> assertEquals(IntervalSet.whole(),
						IntervalSetOps.fromLegacy(IntervalConstants.whole())),
				() -> assertEquals(IntervalSet.empty(),
						IntervalSetOps.fromLegacy(IntervalConstants.undefined())));
	}

	@Test
	void isZeroOnlyMatchesConnectedZeroSingleton() {
		assertAll(
				() -> assertTrue(IntervalSetOps.isZero(IntervalSetOps.connected(0, 0))),
				() -> assertFalse(IntervalSetOps.isZero(IntervalSetOps.connected(-1, 1))),
				() -> assertFalse(IntervalSetOps.isZero(IntervalSetOps.inverted(1, 2))),
				() -> assertFalse(IntervalSetOps.isZero(IntervalSetOps.whole())),
				() -> assertFalse(IntervalSetOps.isZero(IntervalSetOps.empty())),
				() -> assertFalse(IntervalSetOps.isZero(null)));
	}

	@Test
	void hasZeroUsesSetSemantics() {
		assertAll(
				() -> assertTrue(IntervalSetOps.hasZero(IntervalSetOps.connected(0, 0))),
				() -> assertTrue(IntervalSetOps.hasZero(IntervalSetOps.connected(-1, 2))),
				() -> assertFalse(IntervalSetOps.hasZero(IntervalSetOps.connected(1, 2))),
				() -> assertTrue(IntervalSetOps.hasZero(IntervalSetOps.whole())),
				() -> assertFalse(IntervalSetOps.hasZero(IntervalSetOps.empty())),
				() -> assertTrue(IntervalSetOps.hasZero(IntervalSetOps.inverted(2, 5))),
				() -> assertFalse(IntervalSetOps.hasZero(IntervalSetOps.inverted(-1, 1))),
				() -> assertFalse(IntervalSetOps.hasZero(null)));
	}

	@Test
	void leftAndRightRayFromInvertedUseGapBounds() {
		IntervalSet inverted = IntervalSetOps.inverted(2, 5);

		assertAll(
				() -> assertEquals(IntervalSetOps.connected(Double.NEGATIVE_INFINITY, 2),
						IntervalSetOps.leftRayFromInverted(inverted)),
				() -> assertEquals(IntervalSetOps.connected(5, Double.POSITIVE_INFINITY),
						IntervalSetOps.rightRayFromInverted(inverted)));
	}

	@Test
	void leftAndRightRayFromInvertedRejectInvalidInput() {
		assertAll(
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.leftRayFromInverted(null)),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.rightRayFromInverted(null)),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.leftRayFromInverted(IntervalSetOps.connected(1, 2))),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.rightRayFromInverted(IntervalSetOps.connected(1, 2))),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.leftRayFromInverted(IntervalSetOps.empty())),
				() -> assertThrows(IllegalArgumentException.class,
						() -> IntervalSetOps.rightRayFromInverted(IntervalSetOps.whole())));
	}

	@Test
	void signPredicatesOnlyMatchConnectedStrictlyPositiveOrNegativeSets() {
		assertAll(
				() -> assertTrue(IntervalSetOps.isPositive(IntervalSetOps.connected(1, 2))),
				() -> assertFalse(IntervalSetOps.isPositive(IntervalSetOps.connected(0, 2))),
				() -> assertFalse(IntervalSetOps.isPositive(IntervalSetOps.inverted(1, 2))),
				() -> assertFalse(IntervalSetOps.isPositive(null)));
	}

}
