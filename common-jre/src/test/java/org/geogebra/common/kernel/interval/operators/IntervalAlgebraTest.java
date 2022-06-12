package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.fmod;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.pow;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sin;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTest;
import org.junit.Assert;
import org.junit.Test;

public class IntervalAlgebraTest {

	@Test
	public void testFmod() {
		Interval n = fmod(IntervalTest.interval(5.3, 5.3), IntervalTest.interval(2, 2));
		assertTrue(n.almostEqual(IntervalTest.interval(1.3, 1.3)));

		n = fmod(IntervalTest.interval(5, 7), IntervalTest.interval(2, 3));
		assertTrue(n.almostEqual(IntervalTest.interval(2, 5)));

		n = fmod(IntervalTest.interval(18.5, 18.5), IntervalTest.interval(4.2, 4.2));
		assertTrue(n.almostEqual(IntervalTest.interval(1.7, 1.7)));

		n = fmod(IntervalTest.interval(-10, -10), IntervalTest.interval(3, 3));
		assertTrue(n.almostEqual(IntervalTest.interval(-1, -1)));

		n = fmod(new Interval(), IntervalConstants.undefined());
		assertTrue(n.isUndefined());

		n = fmod(IntervalTest.interval(2, 2), IntervalTest.interval(2, 2));
		assertTrue(n.almostEqual(zero()));
	}

	@Test
	public void testMultiplicativeInverse() {
		assertTrue(IntervalTest.interval(1, 1).almostEqual(
				IntervalTest.interval(1, 1).multiplicativeInverse()));

		assertTrue(IntervalTest.interval(1 / 6.0, 1 / 2.0).almostEqual(
				IntervalTest.interval(2, 6).multiplicativeInverse()));

		assertTrue(IntervalTest.interval(-1 / 2.0, -1 / 6.0).almostEqual(
				IntervalTest.interval(-6, -2).multiplicativeInverse()));
	}

	@Test
	public void testMultiplicativeInverseResultInfinityAbs() {
		Interval actual = IntervalTest.interval(-6, 0).multiplicativeInverse();
		assertEquals(actual.getLow(), Double.NEGATIVE_INFINITY, 0);
		assertEquals(actual.getHigh(), -1.0 / 6.0, PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultAbsInfinity() {
		Interval actual = IntervalTest.interval(0, 2).multiplicativeInverse();
		assertEquals(actual.getHigh(), Double.POSITIVE_INFINITY, 0);
		assertEquals(actual.getLow(), 1.0 / 2.0, PRECISION);
	}

	@Test
	public void testMultiplicativeInverseResultInverted() {
		Assert.assertEquals(invertedInterval(-0.16666666666666669, 0.5000000000000001),
				IntervalTest.interval(-6, 2).multiplicativeInverse());
	}

	@Test
	public void testPowOne() {
		Interval interval = IntervalOperands.pow(IntervalTest.interval(Math.exp(-1), Math.exp(1)), 1);
		Assert.assertEquals(IntervalTest.interval(0.36787944117144233, 2.718281828459045),
				interval);
	}

	@Test
	public void testPowThree() {
		Interval interval = IntervalOperands.pow(IntervalTest.interval(Math.exp(-1), Math.exp(1)), 3);
		Assert.assertEquals(IntervalTest.interval(0.049787068367863944, 20.085536923187668), interval);
	}

	@Test
	public void testZeroPowerOfZero() {
		assertTrue(pow(zero(), 0).isUndefined());
	}

	@Test
	public void testPowerOfZero() {
		assertTrue(IntervalTest.interval(1, 1).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-321, 123), 0)));
	}

	@Test
	public void testNegativePowerOfEven() {
		Assert.assertEquals(
				IntervalTest.interval(4), IntervalOperands.pow(IntervalTest.interval(-2), 2));
		Assert.assertEquals(IntervalTest.interval(4), IntervalOperands.pow(IntervalTest.interval(-2), 1 + 1. / 3 + 1. / 3 + 1. / 3));
	}

	@Test
	public void testNegativePowerOfOdd() {
		assertTrue(IntervalTest.interval(-8, -8).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-2, -2), 3)));
	}

	@Test
	public void testMixedPowerOfEven() {
		assertTrue(IntervalTest.interval(0, 4).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-2, 2), 2)));
	}

	@Test
	public void testMixedPowerOfOdd() {
		assertTrue(IntervalTest.interval(-2, 2).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-2, 2), 1)));
	}

	@Test
	public void testPositivePowerOfs() {
		assertTrue(IntervalTest.interval(1, 1).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(1, 1), 1)));
		assertTrue(IntervalTest.interval(1, 1).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(1, 1), 5)));
		assertTrue(IntervalTest.interval(1, 25).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(1, 5), 2)));
		assertTrue(IntervalTest.interval(4, 25).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(2, 5), 2)));
	}

	@Test
	public void testEmptyPowerOf() {
		assertTrue(pow(new Interval(), 4).isUndefined());
	}

	@Test
	public void testPowerOfIntervals() {
		assertTrue(IntervalTest.interval(4, 25).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(2, 5), IntervalTest.interval(2, 2))));
	}

	@Test
	public void testPowerOfNegatives() {
		assertTrue(IntervalTest.interval(1 / 4.0, 1 / 4.0).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(2, 2), -2)));

		assertTrue(IntervalTest.interval(1 / 9.0, 1 / 4.0).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(2, 3), -2)));

		assertTrue(IntervalTest.interval(1 / 9.0, 1 / 4.0).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-3, -2), -2)));

		assertTrue(IntervalTest.interval(1 / 27.0, 1 / 8.0).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(2, 3), -3)));

		assertTrue(IntervalTest.interval(-1 / 8.0, -1 / 27.0).almostEqual(
				IntervalOperands.pow(IntervalTest.interval(-3, -2), -3)));
	}

	@Test
	public void testNegativePowersOfPositive()  {
		Assert.assertEquals(IntervalTest.interval(Double.NEGATIVE_INFINITY, 1 / 4.0).invert(),
				IntervalOperands.pow(IntervalTest.interval(0, 2), -2));
	}

	@Test
	public void testSpecialPowerOfCases() {
		Interval interval = IntervalOperands.pow(IntervalTest.interval(0, 1), -2);
		Assert.assertEquals(IntervalTest.interval(Double.NEGATIVE_INFINITY, 1).invert(), interval);
		Interval halfOpen = IntervalOperands.pow(IntervalTest.interval(0, 1).halfOpenLeft(), -2);
		Assert.assertEquals(IntervalTest.interval(Double.NEGATIVE_INFINITY, 1).invert(), halfOpen);
		assertEquals(1, halfOpen.getHigh(), PRECISION);
	}

	@Test
	public void testSqrt() {
		assertTrue(IntervalTest.interval(2, 3).almostEqual(sqrt(IntervalTest.interval(4, 9))));
		assertTrue(IntervalTest.interval(0, 3).almostEqual(sqrt(IntervalTest.interval(-4, 9))));
		assertTrue(sqrt(IntervalTest.interval(-9, -4)).isUndefined());
	}

	@Test
	public void testSqrtSinEmpty() {
		assertTrue(sqrt(sin(IntervalTest.interval(4, 5))).isUndefined());
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(sqrt(IntervalTest.interval(1, 2)), IntervalOperands.pow(
				IntervalTest.interval(1, 2), 0.5));
	}

	@Test
	public void testBaseLessThanOne() {
		Assert.assertEquals(
				IntervalTest.interval(0.5), IntervalOperands.pow(IntervalTest.interval(0.5), one()));
		Assert.assertEquals(
				IntervalTest.interval(0.25, 0.5), IntervalOperands.pow(IntervalTest.interval(0.5), IntervalTest.interval(1, 2)));
	}

	@Test
	public void testTwoOnXInverse() {
		assertEquals(undefined(), IntervalOperands.pow(IntervalTest.interval(2),
				IntervalConstants.aroundZero().multiplicativeInverse()));

		assertEquals(undefined(), IntervalOperands.pow(IntervalTest.interval(2),
				IntervalTest.interval(-2.9351521213527576E-15, 0.019999999999997065)
						.multiplicativeInverse()));
	}
}
