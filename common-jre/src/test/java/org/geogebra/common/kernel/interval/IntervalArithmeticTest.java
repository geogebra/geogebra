package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalArithmeticTest {

	@Test
	public void testAddition() {
		assertEquals(interval(-2, 2),
				interval(-1, 1).add(interval(-1, 1)));
		assertEquals(interval(-1, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).add(interval(0, 1)));
	}

	@Test
	public void testSubtraction() {
		assertEquals(interval(-2, 2),
				interval(-1, 1).subtract(interval(-1, 1)));

		assertEquals(interval(2, 5),
				interval(5, 7).subtract(interval(2, 3)));

		assertEquals(interval(-2, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).subtract(interval(0, 1)));
	}

	@Test
	public void testMultiplicationEmpty() {
		assertTrue(new Interval().multiply(interval(1, 1)).isEmpty());
	}

	@Test
	public void testMultiplicationPositiveWithPositive() {
		assertEquals(interval(2, 6),
				interval(1, 2).multiply(interval(2, 3)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY).multiply(interval(4, 6)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY)
						.multiply(interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationPositiveWithNegative() {
		assertEquals(interval(-6, -2),
				interval(1, 2).multiply(interval(-3, -2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				interval(1, POSITIVE_INFINITY).multiply(interval(-3, -2)));
	}

	@Test
	public void testMultiplicationPositiveWithMixed() {
		assertEquals(interval(-4, 6),
				interval(1, 2).multiply(interval(-2, 3)));

		assertEquals(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY).multiply(interval(-2, 3)));
	}

	@Test
	public void testMultiplicationPositiveWithZero() {
		assertEquals(interval(0, 0),
				interval(1, 2).multiply(interval(0, 0)));

		assertEquals(interval(0, 0),
				interval(1, POSITIVE_INFINITY).multiply(interval(0, 0)));
	}

	@Test
	public void testMultiplicationNegativeWithPositive() {
		assertEquals(interval(-6, -2),
				interval(-3, -2).multiply(interval(1, 2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				interval(-3, -2).multiply(interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithNegative() {
		assertEquals(interval(2, 6),
				interval(-2, -1).multiply(interval(-3, -2)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				interval(NEGATIVE_INFINITY, -1).multiply(interval(-6, -4)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				interval(NEGATIVE_INFINITY, -1)
						.multiply(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithMixed() {
		assertEquals(interval(-6, 4),
				interval(-2, -1).multiply(interval(-2, 3)));

		assertEquals(IntervalConstants.whole(),
				interval(NEGATIVE_INFINITY, -1).multiply(interval(-2, 3)));
	}

	@Test
	public void testMultiplicationNegativeWithZero() {
		assertEquals(zero(), interval(-2, -1).multiply(zero()));
		assertEquals(zero(), interval(NEGATIVE_INFINITY, -1).multiply(zero()));
	}

	@Test
	public void testMultiplicationMixedWithPositive() {
		assertEquals(interval(-4, 6),
				interval(-2, 3).multiply(interval(1, 2)));

		assertEquals(whole(), interval(-2, 3).multiply(interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithNegative() {
		assertEquals(interval(-6, 4),
				interval(-2, 3).multiply(interval(-2, -1)));

		assertEquals(whole(),
				interval(-2, 3).multiply(interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testMultiplicationMixedWithMixed() {
		assertEquals(interval(-8, 12),
				interval(-2, 3).multiply(interval(-1, 4)));

		assertEquals(whole(),
				interval(NEGATIVE_INFINITY, 3).multiply(interval(-1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithZero() {
		assertEquals(zero(), interval(-2, 1).multiply(zero()));
		assertEquals(zero(), interval(NEGATIVE_INFINITY, 1).multiply(zero()));
	}

	@Test
	public void testMultiplicationZeroWithAny() {
		assertEquals(zero(), zero().multiply(interval(-2, 1)));
		assertEquals(zero(), zero().multiply(interval(-2, -1)));
		assertEquals(zero(), zero().multiply(interval(1, 2)));
		assertEquals(zero(), zero().multiply(interval(0, 0)));
	}
	}