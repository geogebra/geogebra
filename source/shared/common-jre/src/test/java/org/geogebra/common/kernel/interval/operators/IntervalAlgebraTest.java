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

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.aroundZero;
import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalAlgebraTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	public void testFmod() {
		Interval n = fmod(interval(5.3, 5.3), interval(2, 2));
		assertTrue(n.almostEqual(interval(1.3, 1.3), 1E-7));

		n = fmod(interval(5, 7), interval(2, 3));
		assertTrue(n.almostEqual(interval(2, 5), 1E-7));

		n = fmod(interval(18.5, 18.5), interval(4.2, 4.2));
		assertTrue(n.almostEqual(interval(1.7, 1.7), 1E-7));

		n = fmod(interval(-10, -10), interval(3, 3));
		assertTrue(n.almostEqual(interval(-1, -1), 1E-7));

		n = fmod(new Interval(), IntervalConstants.undefined());
		assertTrue(n.isUndefined());

		n = fmod(interval(2, 2), interval(2, 2));
		assertTrue(n.almostEqual(zero(), 1E-7));
	}

	private Interval fmod(Interval base, Interval mod) {
		evaluator.fmod(base, mod);
		return base;
	}

	@Test
	public void testMultiplicativeInverse() {
		assertTrue(interval(1, 1).almostEqual(
				evaluator.inverse(one()), 1E-7));

		assertTrue(interval(1 / 6.0, 1 / 2.0).almostEqual(
				evaluator.inverse(interval(2, 6)), 1E-7));

		assertTrue(interval(-1 / 2.0, -1 / 6.0).almostEqual(
				evaluator.inverse(interval(-6, -2)), 1E-7));
	}

	@Test
	public void testMultiplicativeInverseResultInfinityAbs() {
		Interval actual = evaluator.inverse(interval(-6, 0));
		assertEquals(actual.getLow(), Double.NEGATIVE_INFINITY, 0);
		assertEquals(actual.getHigh(), -1.0 / 6.0, PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultAbsInfinity() {
		Interval actual = evaluator.inverse(interval(0, 2));
		assertEquals(actual.getHigh(), Double.POSITIVE_INFINITY, 0);
		assertEquals(actual.getLow(), 1.0 / 2.0, PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultInverted() {
		assertEquals(invertedInterval(-0.16666666666666669, 0.5000000000000001),
				evaluator.inverse(interval(-6, 2)));
	}

	@Test
	public void testPowOne() {
		Interval interval = evaluator.pow(interval(Math.exp(-1), Math.exp(1)), 1);
		assertEquals(interval(0.36787944117144233, 2.718281828459045),
				interval);
	}

	@Test
	public void testPowThree() {
		Interval interval = evaluator.pow(interval(Math.exp(-1), Math.exp(1)), 3);
		assertEquals(interval(0.049787068367863944, 20.085536923187668), interval);
	}

	@Test
	public void testZeroPowerOfZero() {
		assertTrue(evaluator.pow(zero(), 0).isUndefined());
	}

	@Test
	public void testPowerOfZero() {
		assertTrue(interval(1, 1).almostEqual(
				evaluator.pow(interval(-321, 123), 0), 1E-7));
	}

	@Test
	public void testNegativePowerOfEven() {
		assertEquals(
				interval(4), evaluator.pow(interval(-2), 2));
		assertEquals(interval(4),
				evaluator.pow(interval(-2), 1 + 1. / 3 + 1. / 3 + 1. / 3));
	}

	@Test
	public void testNegativePowerOfOdd() {
		assertTrue(interval(-8, -8).almostEqual(
				evaluator.pow(interval(-2, -2), 3), 1E-7));
	}

	@Test
	public void testMixedPowerOfEven() {
		assertTrue(interval(0, 4).almostEqual(
				evaluator.pow(interval(-2, 2), 2), 1E-7));
	}

	@Test
	public void testMixedPowerOfOdd() {
		assertTrue(interval(-2, 2).almostEqual(
				evaluator.pow(interval(-2, 2), 1), 1E-7));
	}

	@Test
	public void testPositivePowerOfs() {
		assertEquals(one(), evaluator.pow(one(), 1));
		assertEquals(one(), evaluator.pow(one(), 5));
		assertEquals(interval(1, 25), evaluator.pow(interval(1, 5), 2));
		assertEquals(interval(4, 25), evaluator.pow(interval(2, 5), 2));
	}

	@Test
	public void testEmptyPowerOf() {
		assertTrue(evaluator.pow(new Interval(), 4).isUndefined());
	}

	@Test
	public void testPowerOfIntervals() {
		assertTrue(interval(4, 25).almostEqual(
				evaluator.pow(interval(2, 5), interval(2, 2)), 1E-7));
	}

	@Test
	public void testPowerOfNegatives() {
		assertTrue(interval(1 / 4.0, 1 / 4.0).almostEqual(
				evaluator.pow(interval(2, 2), -2), 1E-7));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				evaluator.pow(interval(2, 3), -2), 1E-7));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				evaluator.pow(interval(-3, -2), -2), 1E-7));

		assertTrue(interval(1 / 27.0, 1 / 8.0).almostEqual(
				evaluator.pow(interval(2, 3), -3), 1E-7));

		assertTrue(interval(-1 / 8.0, -1 / 27.0).almostEqual(
				evaluator.pow(interval(-3, -2), -3), 1E-7));
	}

	@Test
	public void testNegativePowersOfPositive()  {
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1 / 4.0).invert(),
				evaluator.pow(interval(0, 2), -2));
	}

	@Test
	public void testSpecialPowerOfCases() {
		Interval interval = evaluator.pow(interval(0, 1), -2);
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1).invert(), interval);
		Interval halfOpen = evaluator.pow(interval(0, 1).halfOpenLeft(), -2);
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1).invert(), halfOpen);
		assertEquals(1, halfOpen.getHigh(), PRECISION);
	}

	@Test
	public void testSqrt() {
		assertTrue(interval(2, 3).almostEqual(evaluator.sqrt(interval(4, 9)), 1E-7));
		assertTrue(interval(0, 3).almostEqual(evaluator.sqrt(interval(-4, 9)), 1E-7));
		assertTrue(evaluator.sqrt(interval(-9, -4)).isUndefined());
	}

	@Test
	public void testSqrtSinEmpty() {
		assertTrue(evaluator.sqrt(evaluator.sin(interval(4, 5))).isUndefined());
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(evaluator.sqrt(interval(1, 2)), evaluator.pow(
				interval(1, 2), 0.5));
	}

	@Test
	public void testBaseLessThanOne() {
		assertEquals(
				interval(0.5), evaluator.pow(interval(0.5), one()));
		assertEquals(
				interval(0.25, 0.5),
				evaluator.pow(interval(0.5), interval(1, 2)));
	}

	@Ignore
	@Test
	public void testTwoOnXInverse() {
		Interval invert = interval(0, Double.POSITIVE_INFINITY).invert();
		assertEquals(invert, evaluator.pow(interval(2),
				evaluator.inverse(aroundZero())));

		assertEquals(invert, evaluator.pow(interval(2),
				evaluator.inverse(interval(-2.9351521213527576E-15, 0.019999999999997065))));
	}
}
