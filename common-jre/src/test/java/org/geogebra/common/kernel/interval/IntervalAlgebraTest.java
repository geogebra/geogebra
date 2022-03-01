package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalOperands.fmod;
import static org.geogebra.common.kernel.interval.IntervalOperands.pow;
import static org.geogebra.common.kernel.interval.IntervalOperands.sin;
import static org.geogebra.common.kernel.interval.IntervalOperands.sqrt;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalAlgebraTest {

	@Test
	public void testFmod() {
		Interval n = fmod(interval(5.3, 5.3), interval(2, 2));
		assertTrue(n.almostEqual(interval(1.3, 1.3)));

		n = fmod(interval(5, 7), interval(2, 3));
		assertTrue(n.almostEqual(interval(2, 5)));

		n = fmod(interval(18.5, 18.5), interval(4.2, 4.2));
		assertTrue(n.almostEqual(interval(1.7, 1.7)));

		n = fmod(interval(-10, -10), interval(3, 3));
		assertTrue(n.almostEqual(interval(-1, -1)));

		n = fmod(new Interval(), IntervalConstants.undefined());
		assertTrue(n.isUndefined());

		n = fmod(interval(2, 2), interval(2, 2));
		assertTrue(n.almostEqual(zero()));
	}

	@Test
	public void testMultiplicativeInverse() {
		assertTrue(interval(1, 1).almostEqual(
				interval(1, 1).multiplicativeInverse()));

		assertTrue(interval(1 / 6.0, 1 / 2.0).almostEqual(
				interval(2, 6).multiplicativeInverse()));

		assertTrue(interval(-1 / 2.0, -1 / 6.0).almostEqual(
				interval(-6, -2).multiplicativeInverse()));
	}

	@Test
	public void testMultiplicativeInverseResultInfinityAbs() {
		Interval actual = interval(-6, 0).multiplicativeInverse();
		assertEquals(actual.getLow(), Double.NEGATIVE_INFINITY, 0);
		assertEquals(actual.getHigh(), -1.0 / 6.0, 1E-7);
	}

	@Test
	public void testMultiplicativeInverseResultAbsInfinity() {
		Interval actual = interval(0, 2).multiplicativeInverse();
		assertEquals(actual.getHigh(), Double.POSITIVE_INFINITY, 0);
		assertEquals(actual.getLow(), 1.0 / 2.0, 1E-7);
	}

	@Test
	public void testMultiplicativeInverseResultInverted() {
		assertEquals(invertedInterval(-0.16666666666666669, 0.5000000000000001),
				interval(-6, 2).multiplicativeInverse());
	}

	@Test
	public void testPowOne() {
		Interval interval = pow(interval(Math.exp(-1), Math.exp(1)), 1);
		assertTrue(interval.almostEqual(interval(0.36787944117, 2.71828182846)));
	}

	@Test
	public void testPowThree() {
		Interval interval = pow(interval(Math.exp(-1), Math.exp(1)), 3);
		assertTrue(interval.almostEqual(interval(0.04978706836, 20.0855369232)));
	}

	@Test
	public void testZeroPowerOfZero() {
		assertTrue(pow(zero(), 0).isUndefined());
	}

	@Test
	public void testPowerOfZero() {
		assertTrue(interval(1, 1).almostEqual(pow(interval(-321, 123), 0)));
	}

	@Test
	public void testNegativePowerOfEven() {
		assertEquals(interval(4), pow(interval(-2), 2));
		assertEquals(interval(4), pow(interval(-2), 1 + 1. / 3 + 1. / 3 + 1. / 3));
	}

	@Test
	public void testNegativePowerOfOdd() {
		assertTrue(interval(-8, -8).almostEqual(pow(interval(-2, -2), 3)));
	}

	@Test
	public void testMixedPowerOfEven() {
		assertTrue(interval(0, 4).almostEqual(pow(interval(-2, 2), 2)));
	}

	@Test
	public void testMixedPowerOfOdd() {
		assertTrue(interval(-2, 2).almostEqual(pow(interval(-2, 2), 1)));
	}

	@Test
	public void testPositivePowerOfs() {
		assertTrue(interval(1, 1).almostEqual(pow(interval(1, 1), 1)));
		assertTrue(interval(1, 1).almostEqual(pow(interval(1, 1), 5)));
		assertTrue(interval(1, 25).almostEqual(pow(interval(1, 5), 2)));
		assertTrue(interval(4, 25).almostEqual(pow(interval(2, 5), 2)));
	}

	@Test
	public void testEmptyPowerOf() {
		assertTrue(pow(new Interval(), 4).isUndefined());
	}

	@Test
	public void testPowerOfIntervals() {
		assertTrue(interval(4, 25).almostEqual(pow(interval(2, 5), interval(2, 2))));
	}

	@Test
	public void testPowerOfNotSingletonInterval() {
		assertTrue(pow(interval(2, 5), interval(1, 5)).isUndefined());
	}

	@Test
	public void testPowerOfNegatives() {
		assertTrue(interval(1 / 4.0, 1 / 4.0).almostEqual(
				pow(interval(2, 2), -2)));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				pow(interval(2, 3), -2)));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				pow(interval(-3, -2), -2)));

		assertTrue(interval(1 / 27.0, 1 / 8.0).almostEqual(
				pow(interval(2, 3), -3)));

		assertTrue(interval(-1 / 8.0, -1 / 27.0).almostEqual(
				pow(interval(-3, -2), -3)));
	}

	@Test
	public void testNegativePowersOfPositive()  {
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1 / 4.0).invert(),
				pow(interval(0, 2), -2));
	}

	@Test
	public void testSpecialPowerOfCases() {
		Interval interval = pow(interval(0, 1), -2);
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1).invert(), interval);
		Interval halfOpen = pow(interval(0, 1).halfOpenLeft(), -2);
		assertEquals(interval(Double.NEGATIVE_INFINITY, 1).invert(), halfOpen);
		assertEquals(1, halfOpen.getHigh(), 1E-7);
	}

	@Test
	public void testSqrt() {
		assertTrue(interval(2, 3).almostEqual(sqrt(interval(4, 9))));
		assertTrue(interval(0, 3).almostEqual(sqrt(interval(-4, 9))));
		assertTrue(sqrt(interval(-9, -4)).isUndefined());
	}

	@Test
	public void testSqrtSinEmpty() {
		assertTrue(sqrt(sin(interval(4, 5))).isUndefined());
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(sqrt(interval(1, 2)), pow(interval(1, 2), 0.5));
	}
}
