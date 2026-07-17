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
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.jupiter.api.Test;

class AbsOperandTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	void testPositiveIntervals() {
		assertEquals(interval(0, 1E12), abs(interval(0, 1E12)));
	}

	private Interval abs(Interval value) {
		return evaluator.abs(value);
	}

	@Test
	void testNegativeIntervals() {
		assertEquals(interval(1E-234, 1E234), abs(interval(-1E234, -1E-234)));
	}

	@Test
	void testZero() {
		assertEquals(zero(), abs(zero()));
	}

	@Test
	void testUndefined() {
		assertEquals(undefined(), abs(undefined()));
	}

	@Test
	void testWhole() {
		assertEquals(interval(0, Double.POSITIVE_INFINITY), abs(whole()));
	}

	@Test
	void testNegativeInfinity() {
		assertEquals(positiveInfinity(), abs(negativeInfinity()));
	}

	@Test
	void testMixedInvertedIntervals() {
		assertEquals(interval(100, Double.POSITIVE_INFINITY), abs(legacyInverted(-100, 100)));
	}

	@Test
	void testNegativeInvertedIntervals() {
		assertEquals(interval(0, Double.POSITIVE_INFINITY),
				abs(legacyInverted(-200, -100)));
	}
}
