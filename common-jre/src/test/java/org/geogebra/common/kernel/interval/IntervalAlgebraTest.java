package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalAlgebraTest {

	@Test
	public void testFmod() {
		Interval n = interval(5.3, 5.3).fmod(interval(2, 2));
		assertTrue(n.almostEqual(interval(1.3, 1.3)));

		n = interval(5, 7).fmod(interval(2, 3));
		assertTrue(n.almostEqual(interval(2, 5)));

		n = interval(18.5, 18.5).fmod(interval(4.2, 4.2));
		assertTrue(n.almostEqual(interval(1.7, 1.7)));

		n = interval(-10, -10).fmod(interval(3, 3));
		assertTrue(n.almostEqual(interval(-1, -1)));

		n = new Interval().fmod(IntervalConstants.empty());
		assertTrue(n.isEmpty());

		n = interval(2, 2).fmod(interval(2, 2));
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
	public void testMultiplicativeInverseZero() {
		assertEquals(whole().invert(), zero().multiplicativeInverse());
	}

	@Test
	public void testDoubleMultiplicativeInverseZero() {
		assertEquals(whole().invert().uninvert(),
				zero().multiplicativeInverse().multiplicativeInverse());
	}

	@Test
	public void testPowOne() {
		Interval interval = interval(Math.exp(-1), Math.exp(1)).pow(1);
		assertTrue(interval.almostEqual(interval(0.36787944117, 2.71828182846)));
	}

	@Test
	public void testPowThree() {
		Interval interval = interval(Math.exp(-1), Math.exp(1)).pow(3);
		assertTrue(interval.almostEqual(interval(0.04978706836, 20.0855369232)));
	}

	@Test
	public void testZeroPowerOfZero() {
		assertTrue(zero().pow(0).isEmpty());
	}

	@Test
	public void testPowerOfZero() {
		assertTrue(interval(1, 1).almostEqual(interval(-321, 123).pow(0)));
	}

	@Test
	public void testNegativePowerOfEven() {
		assertTrue(interval(4, 4).almostEqual(interval(-2, -2).pow(2)));
	}

	@Test
	public void testNegativePowerOfOdd() {
		assertTrue(interval(-8, -8).almostEqual(interval(-2, -2).pow(3)));
	}

	@Test
	public void testMixedPowerOfEven() {
		assertTrue(interval(0, 4).almostEqual(interval(-2, 2).pow(2)));
	}

	@Test
	public void testMixedPowerOfOdd() {
		assertTrue(interval(-2, 2).almostEqual(interval(-2, 2).pow(1)));
	}

	@Test
	public void testPositivePowerOfs() {
		assertTrue(interval(1, 1).almostEqual(interval(1, 1).pow(1)));
		assertTrue(interval(1, 1).almostEqual(interval(1, 1).pow(5)));
		assertTrue(interval(1, 25).almostEqual(interval(1, 5).pow(2)));
		assertTrue(interval(4, 25).almostEqual(interval(2, 5).pow(2)));
	}

	@Test
	public void testEmptyPowerOf() {
		assertTrue(new Interval().pow(4).isEmpty());
	}

	@Test
	public void testPowerOfIntervals() {
		assertTrue(interval(4, 25).almostEqual(interval(2, 5).pow(interval(2, 2))));
	}

	@Test
	public void testPowerOfNotSingletonInterval() {
		assertTrue(interval(2, 5).pow(interval(1, 5)).isEmpty());
	}

	@Test
	public void testPowerOfNegatives() {
		assertTrue(interval(1 / 4.0, 1 / 4.0).almostEqual(
				interval(2, 2).pow(-2)));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				interval(2, 3).pow(-2)));

		assertTrue(interval(1 / 9.0, 1 / 4.0).almostEqual(
				interval(-3, -2).pow(-2)));

		assertTrue(interval(1 / 27.0, 1 / 8.0).almostEqual(
				interval(2, 3).pow(-3)));

		assertTrue(interval(-1 / 8.0, -1 / 27.0).almostEqual(
				interval(-3, -2).pow(-3)));
	}

	@Test
	public void testSpecialPowerOfCases() {
		Interval interval = interval(0, 1).pow(-2);
		assertTrue(interval.getLow() < 1);
		assertTrue(Math.abs(interval.getLow() - 1) < 1E-7);

		Interval halfOpen = interval(0, 1).halfOpenLeft().pow(-2);
		assertTrue(halfOpen.getLow() < 1);
		assertTrue(Math.abs(halfOpen.getLow() - 1) < 1E-7);
		assertEquals(Double.POSITIVE_INFINITY, halfOpen.getHigh(), 0);

	}
}
