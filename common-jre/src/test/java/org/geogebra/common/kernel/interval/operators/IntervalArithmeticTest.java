package org.geogebra.common.kernel.interval.operators;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.multiply;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalTest;
import org.junit.Assert;
import org.junit.Test;

public class IntervalArithmeticTest {

	@Test
	public void testMultiplicationEmpty() {
		assertTrue(multiply(new Interval(), IntervalTest.interval(1, 1)).isUndefined());
	}

	@Test
	public void testMultiplicationPositiveWithPositive() {
		Assert.assertEquals(IntervalTest.interval(2, 6),
				multiply(IntervalTest.interval(1, 2), IntervalTest.interval(2, 3)));

		Assert.assertEquals(IntervalTest.interval(4, POSITIVE_INFINITY),
				multiply(IntervalTest.interval(1, POSITIVE_INFINITY), IntervalTest.interval(4, 6)));

		Assert.assertEquals(IntervalTest.interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				multiply(IntervalTest.interval(1, POSITIVE_INFINITY),
						IntervalTest.interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationPositiveWithNegative() {
		Assert.assertEquals(IntervalTest.interval(-6, -2),
				multiply(IntervalTest.interval(1, 2), IntervalTest.interval(-3, -2)));

		Assert.assertEquals(IntervalTest.interval(NEGATIVE_INFINITY, -2),
				multiply(IntervalTest.interval(1, POSITIVE_INFINITY), IntervalTest.interval(-3, -2)));
	}

	@Test
	public void testMultiplicationPositiveWithMixed() {
		Assert.assertEquals(IntervalTest.interval(-4, 6),
				multiply(IntervalTest.interval(1, 2), IntervalTest.interval(-2, 3)));

		Assert.assertEquals(IntervalTest.interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				multiply(IntervalTest.interval(1, POSITIVE_INFINITY), IntervalTest.interval(-2, 3)));
	}

	@Test
	public void testMultiplicationPositiveWithZero() {
		Assert.assertEquals(IntervalTest.interval(0, 0),
				multiply(IntervalTest.interval(1, 2), IntervalTest.interval(0, 0)));

		Assert.assertEquals(IntervalTest.interval(0, 0),
				multiply(IntervalTest.interval(1, POSITIVE_INFINITY), IntervalTest.interval(0, 0)));
	}

	@Test
	public void testMultiplicationNegativeWithPositive() {
		Assert.assertEquals(IntervalTest.interval(-6, -2),
				multiply(IntervalTest.interval(-3, -2), IntervalTest.interval(1, 2)));

		Assert.assertEquals(IntervalTest.interval(NEGATIVE_INFINITY, -2),
				multiply(IntervalTest.interval(-3, -2), IntervalTest.interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithNegative() {
		Assert.assertEquals(IntervalTest.interval(2, 6),
				multiply(IntervalTest.interval(-2, -1), IntervalTest.interval(-3, -2)));

		Assert.assertEquals(IntervalTest.interval(4, POSITIVE_INFINITY),
				multiply(IntervalTest.interval(NEGATIVE_INFINITY, -1), IntervalTest.interval(-6, -4)));

		Assert.assertEquals(IntervalTest.interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				multiply(IntervalTest.interval(NEGATIVE_INFINITY, -1),
						IntervalTest.interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithMixed() {
		Assert.assertEquals(IntervalTest.interval(-6, 4),
				multiply(IntervalTest.interval(-2, -1), IntervalTest.interval(-2, 3)));

		Assert.assertEquals(IntervalConstants.whole(),
				multiply(IntervalTest.interval(NEGATIVE_INFINITY, -1), IntervalTest.interval(-2, 3)));
	}

	@Test
	public void testMultiplicationNegativeWithZero() {
		assertEquals(zero(), multiply(IntervalTest.interval(-2, -1), zero()));
		assertEquals(zero(), multiply(IntervalTest.interval(NEGATIVE_INFINITY, -1), zero()));
	}

	@Test
	public void testMultiplicationMixedWithPositive() {
		Assert.assertEquals(IntervalTest.interval(-4, 6),
				multiply(IntervalTest.interval(-2, 3), IntervalTest.interval(1, 2)));

		assertEquals(whole(),
				multiply(IntervalTest.interval(-2, 3), IntervalTest.interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithNegative() {
		Assert.assertEquals(IntervalTest.interval(-6, 4),
				multiply(IntervalTest.interval(-2, 3), IntervalTest.interval(-2, -1)));

		assertEquals(whole(),
				multiply(IntervalTest.interval(-2, 3), IntervalTest.interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testMultiplicationMixedWithMixed() {
		Assert.assertEquals(IntervalTest.interval(-8, 12),
				multiply(IntervalTest.interval(-2, 3), IntervalTest.interval(-1, 4)));

		assertEquals(whole(),
				multiply(IntervalTest.interval(NEGATIVE_INFINITY, 3), IntervalTest.interval(-1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithZero() {
		assertEquals(zero(), multiply(IntervalTest.interval(-2, 1), zero()));
		assertEquals(zero(), multiply(IntervalTest.interval(NEGATIVE_INFINITY, 1), zero()));
	}

	@Test
	public void testMultiplicationZeroWithAny() {
		assertEquals(zero(), multiply(zero(), IntervalTest.interval(-2, 1)));
		assertEquals(zero(), multiply(zero(), IntervalTest.interval(-2, -1)));
		assertEquals(zero(), multiply(zero(), IntervalTest.interval(1, 2)));
		assertEquals(zero(), multiply(zero(), IntervalTest.interval(0, 0)));
	}
	}