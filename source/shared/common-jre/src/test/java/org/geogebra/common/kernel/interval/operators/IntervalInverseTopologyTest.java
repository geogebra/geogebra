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

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.jupiter.api.Test;

class IntervalInverseTopologyTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	void inverseOfZeroIsUndefined() {
		assertEquals(undefined(), evaluator.inverse(interval(0)));
	}

	@Test
	void inverseOfUndefinedIsUndefined() {
		assertEquals(undefined(), evaluator.inverse(undefined()));
	}

	@Test
	void inverseOfNearZeroUsesPublicZeroWithDeltaGuard() {
		Interval interval = interval(1E-7);

		assertTrue(interval.isZeroWithDelta(1E-6));
		assertEquals(undefined(), evaluator.inverse(interval));
	}

	@Test
	void inverseOfInvertedIntervalMatchesUnionOfReciprocalRays() {
		Interval inverted = legacyInverted(1, 2);
		Interval leftRay = interval(Double.NEGATIVE_INFINITY, 1);
		Interval rightRay = interval(2, Double.POSITIVE_INFINITY);

		Interval actual = evaluator.inverse(inverted);
		Interval expected =
				evaluator.union(evaluator.inverse(leftRay), evaluator.inverse(rightRay));

		assertEquals(expected, actual);
	}

	@Test
	void inverseOfConnectedIntervalExcludingZeroStaysConnected() {
		assertTrue(evaluator.inverse(interval(2, 6)).isPositive());
	}

	@Test
	void inverseOfWholeCurrentlyStaysWhole() {
		Interval actual = evaluator.inverse(whole());
		assertEquals(legacyInverted(0, 0), actual);
	}

	@Test
	void inverseSetMatchesLegacyInverseForConnectedInput() {
		Interval input = interval(2, 6);

		assertEquals(evaluator.inverse(input), toLegacy(evaluator.inverseSet(fromLegacy(input))));
	}
}
