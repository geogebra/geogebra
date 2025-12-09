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
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalRootTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	public void testSqrtPositive() {
		assertEquals(interval(2, 3),
				evaluator.sqrt(interval(4, 9)));
	}

	@Test
	public void testSqrtMixed() {
		assertEquals(interval(0, 3),
				evaluator.sqrt(interval(-4, 9)));
	}

	@Test
	public void testSqrtNegative() {
		assertEquals(undefined(), evaluator.sqrt(interval(-9, -4)));
		assertEquals(zero(), evaluator.sqrt(interval(0, 0)));
		assertEquals(interval(0, 1), evaluator.sqrt(interval(0, 1)));
	}

	@Ignore
	@Test
	public void testNthRootInNegativeInterval() {
		assertEquals(interval(-2, 2), evaluator.nthRoot(interval(-8, 8), 3));
		assertEquals(interval(0.5, Double.POSITIVE_INFINITY),
				evaluator.nthRoot(interval(-8, 8), -3));
	}

	@Ignore
	@Test
	public void testNthRoot() {
		assertEquals(undefined(), evaluator.nthRoot(interval(-27, -8), -3));
		assertEquals(undefined(), evaluator.nthRoot(interval(-27, -8), 2));
		assertEquals(interval(0, 3), evaluator.nthRoot(interval(-4, 9), 2));
		assertEquals(interval(-3, 2), evaluator.nthRoot(interval(-27, 8), 3));
		assertEquals(interval(2, 3), evaluator.nthRoot(interval(4, 9), 2));
		assertEquals(interval(2, 3), evaluator.nthRoot(interval(8, 27), 3));
		assertEquals(interval(2, 2), evaluator.nthRoot(interval(8, 8), 3));
	}

	@Test
	public void testNthRootWithNegativeN() {
		assertEquals(interval(-3),
				evaluator.nthRoot(interval(-27), interval(3)));
		assertEquals(interval(-3, -2),
				evaluator.nthRoot(interval(-27, -8), interval(3)));
	}

	@Test
	public void testSqrtSinUndef() {
		assertTrue(evaluator.sqrt(evaluator.sin(interval(4, 5))).isUndefined());
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(evaluator.sqrt(interval(1, 2)),
				evaluator.pow(interval(1, 2), 0.5));
	}

	@Test
	public void testEvenNRootWithInvertedXAroundZero() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		assertEquals(interval(2.2360679774998005, Double.POSITIVE_INFINITY),
				evaluator.nthRoot(evaluator.inverse(x), 2));
	}

	@Test
	public void testOddNRootWithInvertedXAroundZero() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		assertEquals(interval(-78669.43188987061, 1.7099759466767028).invert(),
				evaluator.nthRoot(evaluator.inverse(x), 3));
	}
}