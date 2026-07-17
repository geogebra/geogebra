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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.jupiter.api.Test;

class IntervalArithmeticTest {
	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	void testMultiplicationEmpty() {
		assertTrue(evaluator.multiply(new Interval(), interval(1, 1)).isUndefined());
	}

	@Test
	void testMultiplicationPositiveWithPositive() {
		assertEquals(interval(2, 6),
				evaluator.multiply(interval(1, 2), interval(2, 3)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				evaluator.multiply(interval(1, POSITIVE_INFINITY), interval(4, 6)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				evaluator.multiply(interval(1, POSITIVE_INFINITY),
						interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	void testMultiplicationPositiveWithNegative() {
		assertEquals(interval(-6, -2),
				evaluator.multiply(interval(1, 2), interval(-3, -2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				evaluator.multiply(interval(1, POSITIVE_INFINITY), interval(-3, -2)));
	}

	@Test
	void testMultiplicationPositiveWithMixed() {
		assertEquals(interval(-4, 6),
				evaluator.multiply(interval(1, 2), interval(-2, 3)));

		assertEquals(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				evaluator.multiply(interval(1, POSITIVE_INFINITY), interval(-2, 3)));
	}

	@Test
	void testMultiplicationPositiveWithZero() {
		assertEquals(interval(0, 0),
				evaluator.multiply(interval(1, 2), interval(0, 0)));

		assertEquals(interval(0, 0),
				evaluator.multiply(interval(1, POSITIVE_INFINITY), interval(0, 0)));
	}

	@Test
	void testMultiplicationNegativeWithPositive() {
		assertEquals(interval(-6, -2),
				evaluator.multiply(interval(-3, -2), interval(1, 2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				evaluator.multiply(interval(-3, -2), interval(1, POSITIVE_INFINITY)));
	}

	@Test
	void testMultiplicationNegativeWithNegative() {
		assertEquals(interval(2, 6),
				evaluator.multiply(interval(-2, -1), interval(-3, -2)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				evaluator.multiply(interval(NEGATIVE_INFINITY, -1), interval(-6, -4)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				evaluator.multiply(interval(NEGATIVE_INFINITY, -1),
						interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	void testMultiplicationNegativeWithMixed() {
		assertEquals(interval(-6, 4),
				evaluator.multiply(interval(-2, -1), interval(-2, 3)));

		assertEquals(IntervalConstants.whole(),
				evaluator.multiply(interval(NEGATIVE_INFINITY, -1), interval(-2, 3)));
	}

	@Test
	void testMultiplicationNegativeWithZero() {
		assertEquals(zero(), evaluator.multiply(interval(-2, -1), zero()));
		assertEquals(zero(), evaluator.multiply(interval(NEGATIVE_INFINITY, -1), zero()));
	}

	@Test
	void testMultiplicationMixedWithPositive() {
		assertEquals(interval(-4, 6),
				evaluator.multiply(interval(-2, 3), interval(1, 2)));

		assertEquals(whole(),
				evaluator.multiply(interval(-2, 3), interval(1, POSITIVE_INFINITY)));
	}

	@Test
	void testMultiplicationMixedWithNegative() {
		assertEquals(interval(-6, 4),
				evaluator.multiply(interval(-2, 3), interval(-2, -1)));

		assertEquals(whole(),
				evaluator.multiply(interval(-2, 3), interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	void testMultiplicationMixedWithMixed() {
		assertEquals(interval(-8, 12),
				evaluator.multiply(interval(-2, 3), interval(-1, 4)));

		assertEquals(whole(),
				evaluator.multiply(interval(NEGATIVE_INFINITY, 3),
						interval(-1, POSITIVE_INFINITY)));
	}

	@Test
	void testMultiplicationMixedWithZero() {
		assertEquals(zero(), evaluator.multiply(interval(-2, 1), zero()));
		assertEquals(zero(), evaluator.multiply(interval(NEGATIVE_INFINITY, 1), zero()));
	}

	@Test
	void testMultiplicationZeroWithAny() {
		assertEquals(zero(), evaluator.multiply(zero(), interval(-2, 1)));
		assertEquals(zero(), evaluator.multiply(zero(), interval(-2, -1)));
		assertEquals(zero(), evaluator.multiply(zero(), interval(1, 2)));
		assertEquals(zero(), evaluator.multiply(zero(), interval(0, 0)));
	}
}
