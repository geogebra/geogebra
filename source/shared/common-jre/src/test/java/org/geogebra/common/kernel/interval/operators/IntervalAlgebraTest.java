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
import static org.geogebra.common.kernel.interval.IntervalSet.overflow;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.halfOpenLeft;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.LegacyIntervalAdapter.legacyInverted;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalSet;
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
		return evaluator.fmod(base, mod);
	}

	@Test
	public void testFmodSetReturnsWholeRemainderRangeForWholeDividend() {
		assertEquals(connected(-2, 2), evaluator.fmodSet(whole(), connected(2, 2)));
	}

	@Test
	public void testFmodSetReturnsWholeRemainderRangeForInvertedDividend() {
		assertEquals(connected(-2, 2), evaluator.fmodSet(inverted(-1, 1), connected(2, 2)));
	}

	@Test
	public void testFmodOverflow() {
		assertEquals(overflow(), evaluator.fmodSet(connected(1, 1), overflow()));
		assertEquals(overflow(), evaluator.fmodSet(overflow(), connected(1, 1)));
		assertEquals(overflow(), evaluator.fmodSet(overflow(), overflow()));
	}

	@Test
	public void testMultiplicativeInverse() {
		assertTrue(interval(1, 1).almostEqual(
				evaluator.inverse(one()), 1E-7));

		assertTrue(interval(1 / 6.0, 1 / 2.0).almostEqual(
				evaluator.inverse(interval(2, 6)), 1E-7));

		assertTrue(interval(-1 / 2.0, -1 / 6.0).almostEqual(
				evaluator.inverse(interval(-6, -2)), 1E-7));
		assertEquals(overflow(), evaluator.inverseSet(overflow()));
	}

	@Test
	public void testMultiplicativeInverseMatchesInverseNearZero() {
		Interval nearZero = aroundZero();
		assertEquals(evaluator.inverse(nearZero), evaluator.multiplicativeInverse(nearZero));
	}

	@Test
	public void testMultiplicativeInverseResultInfinityAbs() {
		Interval actual = evaluator.inverse(interval(-6, 0));
		assertEquals(Double.NEGATIVE_INFINITY, actual.getLow(), 0);
		assertEquals(-1.0 / 6.0, actual.getHigh(), PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultAbsInfinity() {
		Interval actual = evaluator.inverse(interval(0, 2));
		assertEquals(Double.POSITIVE_INFINITY, actual.getHigh(), 0);
		assertEquals(1.0 / 2.0, actual.getLow(), PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultInverted() {
		assertEquals(legacyInverted(-0.16666666666666669, 0.5000000000000001),
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
	public void testPowerOfZeroIsOne() {
		assertEquals(one(), evaluator.pow(zero(), 0));
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
	public void testEvenPowerOfInvertedCollapsesToConnectedPositive() {
		assertEquals(interval(4, Double.POSITIVE_INFINITY),
				evaluator.pow(legacyInverted(-3, 2), 2));
	}

	@Test
	public void testOddPowerOfInvertedStaysInverted() {
		assertEquals(legacyInverted(-27, 8),
				evaluator.pow(legacyInverted(-3, 2), 3));
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
		assertEquals(inverted(Double.NEGATIVE_INFINITY, 1 / 4.0),
				evaluator.powSet(connected(0, 2), -2));
	}

	@Test
	public void negativeEvenPowerOfConnectedIntervalTouchingZeroReturnsInverted() {
		assertEquals(inverted(Double.NEGATIVE_INFINITY, 1),
				evaluator.powSet(connected(0, 1), -2));
	}

	@Test
	public void negativeEvenPowerOfHalfOpenIntervalTouchingZeroMatchesConnectedContract() {
		assertEquals(inverted(Double.NEGATIVE_INFINITY, 1),
				evaluator.powSet(halfOpenLeft(0, 1), -2));
	}

	@Test
	public void testPowerOverflow() {
		assertEquals(overflow(), evaluator.powSet(overflow(), 10));
		assertEquals(overflow(), evaluator.powSet(connected(1, 2), overflow()));
		assertEquals(overflow(), evaluator.powSet(overflow(), overflow()));
	}

	@Test
	public void testSqrt() {
		assertTrue(interval(2, 3).almostEqual(evaluator.sqrt(interval(4, 9)), 1E-7));
		assertTrue(interval(0, 3).almostEqual(evaluator.sqrt(interval(-4, 9)), 1E-7));
		assertTrue(evaluator.sqrt(interval(-9, -4)).isUndefined());
		assertEquals(overflow(), evaluator.sqrtSet(overflow()));
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
		IntervalSet invert = inverted(0, Double.POSITIVE_INFINITY);
		assertEquals(invert, evaluator.powSet(connected(2, 2),
				evaluator.inverseSet(connected(aroundZero()))));

		assertEquals(invert, evaluator.pow(interval(2),
				evaluator.inverse(interval(-2.9351521213527576E-15, 0.019999999999997065))));
	}

	@Test
	public void testPowerOfPowerOfTwo() {
		Interval two = interval(2, 2);
		Interval square = evaluator.pow(two, 2);
		assertEquals(interval(16), evaluator.pow(interval(-2), square));
		assertEquals(interval(0), evaluator.pow(interval(0), square));
		assertEquals(interval(16), evaluator.pow(interval(2), square));

	}
}
