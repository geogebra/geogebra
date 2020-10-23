package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.shouldEqual;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalArithmeticTest {

	@Test
	public void testAddition() {
		shouldEqual(interval(-2, 2),
				interval(-1, 1).add(interval(-1, 1)));
		shouldEqual(interval(-1, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).add(interval(0, 1)));
	}

	@Test
	public void testSubtraction() {
		shouldEqual(interval(-2, 2),
				interval(-1, 1).subtract(interval(-1, 1)));

		shouldEqual(interval(2, 5),
				interval(5, 7).subtract(interval(2, 3)));

		shouldEqual(interval(-2, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).subtract(interval(0, 1)));
	}

	@Test
	public void testMultiplicationEmpty() {
		assertTrue(new Interval().multiply(interval(1, 1)).isEmpty());
	}

	@Test
	public void testMultiplicationPositiveWithPositive() {
		shouldEqual(interval(2, 6),
				interval(1, 2).multiply(interval(2, 3)));

		shouldEqual(interval(4, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY).multiply(interval(4, 6)));

		shouldEqual(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY)
						.multiply(interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationPositiveWithNegative() {
		shouldEqual(interval(-6, -2),
				interval(1, 2).multiply(interval(-3, -2)));

		shouldEqual(interval(NEGATIVE_INFINITY, -2),
				interval(1, POSITIVE_INFINITY).multiply(interval(-3, -2)));
	}

	@Test
	public void testMultiplicationPositiveWithMixed() {
		shouldEqual(interval(-4, 6),
				interval(1, 2).multiply(interval(-2, 3)));

		shouldEqual(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY).multiply(interval(-2, 3)));
	}

	@Test
	public void testMultiplicationPositiveWithZero() {
		shouldEqual(interval(0, 0),
				interval(1, 2).multiply(interval(0, 0)));

		shouldEqual(interval(0, 0),
				interval(1, POSITIVE_INFINITY).multiply(interval(0, 0)));
	}

	@Test
	public void testMultiplicationNegativeWithPositive() {
		shouldEqual(interval(-6, -2),
				interval(-3, -2).multiply(interval(1, 2)));

		shouldEqual(interval(NEGATIVE_INFINITY, -2),
				interval(-3, -2).multiply(interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithNegative() {
		shouldEqual(interval(2, 6),
				interval(-2, -1).multiply(interval(-3, -2)));

		shouldEqual(interval(4, POSITIVE_INFINITY),
				interval(NEGATIVE_INFINITY, -1).multiply(interval(-6, -4)));

		shouldEqual(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				interval(NEGATIVE_INFINITY, -1)
						.multiply(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithMixed() {
		shouldEqual(interval(-6, 4),
				interval(-2, -1).multiply(interval(-2, 3)));

		shouldEqual(IntervalConstants.whole(),
				interval(NEGATIVE_INFINITY, -1).multiply(interval(-2, 3)));
	}

	@Test
	public void testMultiplicationNegativeWithZero() {
		shouldEqual(zero(), interval(-2, -1).multiply(zero()));
		shouldEqual(zero(), interval(NEGATIVE_INFINITY, -1).multiply(zero()));
	}

	@Test
	public void testMultiplicationMixedWithPositive() {
		shouldEqual(interval(-4, 6),
				interval(-2, 3).multiply(interval(1, 2)));

		shouldEqual(whole(), interval(-2, 3).multiply(interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithNegative() {
		shouldEqual(interval(-6, 4),
				interval(-2, 3).multiply(interval(-2, -1)));

		shouldEqual(whole(),
				interval(-2, 3).multiply(interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testMultiplicationMixedWithMixed() {
		shouldEqual(interval(-8, 12),
				interval(-2, 3).multiply(interval(-1, 4)));

		shouldEqual(whole(),
				interval(NEGATIVE_INFINITY, 3).multiply(interval(-1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithZero() {
		shouldEqual(zero(), interval(-2, 1).multiply(zero()));
		shouldEqual(zero(), interval(NEGATIVE_INFINITY, 1).multiply(zero()));
	}

	@Test
	public void testMultiplicationZeroWithAny() {
		shouldEqual(zero(), zero().multiply(interval(-2, 1)));
		shouldEqual(zero(), zero().multiply(interval(-2, -1)));
		shouldEqual(zero(), zero().multiply(interval(1, 2)));
		shouldEqual(zero(), zero().multiply(interval(0, 0)));
	}
	}