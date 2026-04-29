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
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LegacyIntervalSemanticsTest {

	@Test
	void invalidConstructorInputBecomesUndefined() {
		assertTrue(new Interval(2, 1).isUndefined());
	}

	@Test
	void undefinedConstantIsUndefined() {
		assertTrue(undefined().isUndefined());
	}

	@Test
	void wholeLegacyIntervalIsWholeAndNotSingleton() {
		Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		assertAll(
				() -> assertTrue(interval.isWhole()),
				() -> assertFalse(interval.isSingleton()));
	}

	@Test
	void infinitySingletonsAndSemiInfiniteIntervalsAreNotWhole() {
		assertAll(
				() -> assertFalse(new Interval(Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY).isWhole()),
				() -> assertFalse(new Interval(Double.NEGATIVE_INFINITY,
						Double.NEGATIVE_INFINITY).isWhole()),
				() -> assertFalse(new Interval(Double.NEGATIVE_INFINITY, 5).isWhole()),
				() -> assertFalse(new Interval(7, Double.POSITIVE_INFINITY).isWhole()));
	}

	@Test
	void hasInfinityMatchesCurrentLegacyBehavior() {
		assertAll(
				() -> assertTrue(new Interval(Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY).hasInfinity()),
				() -> assertTrue(new Interval(Double.NEGATIVE_INFINITY, 5).hasInfinity()),
				() -> assertTrue(new Interval(7, Double.POSITIVE_INFINITY).hasInfinity()),
				() -> assertTrue(new Interval(Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY).hasInfinity()),
				() -> assertTrue(new Interval(Double.NEGATIVE_INFINITY,
						Double.NEGATIVE_INFINITY).hasInfinity()),
				() -> assertFalse(undefined().hasInfinity()));
	}

	@Test
	void signClassificationMatchesCurrentLegacyBehavior() {
		assertAll(
				() -> assertTrue(new Interval(1, 2).isPositive()),
				() -> assertFalse(new Interval(0, 2).isPositive()),
				() -> assertTrue(new Interval(0, 2).isPositiveWithZero()),
				() -> assertFalse(new Interval(-1, 2).isPositiveWithZero()),
				() -> assertTrue(new Interval(-2, -1).getHigh() < 0),
				() -> assertFalse(new Interval(-2, 0).getHigh() < 0),
				() -> assertTrue(new Interval(-2, 0).isNegativeWithZero()),
				() -> assertFalse(new Interval(-2, 1).isNegativeWithZero()));
	}

	@Test
	void wholeClassificationMatchesCurrentLegacyBoundRules() {
		assertAll(
				() -> assertTrue(new Interval(Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY).isWhole()),
				() -> assertFalse(new Interval(Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY).isWhole()),
				() -> assertFalse(new Interval(Double.NEGATIVE_INFINITY,
						Double.NEGATIVE_INFINITY).isWhole()));
	}
}
