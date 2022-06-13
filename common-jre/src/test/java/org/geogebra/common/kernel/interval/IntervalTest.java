package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.geogebra.common.kernel.interval.operators.RMath;
import org.junit.Test;

public class IntervalTest {

	@Test
	public void testValidInterval() {
		Interval interval = new Interval(1, 2);
		assertTrue(interval.getLow() == 1 && interval.getHigh() == 2);
	}

	@Test
	public void testInvalidIntervals() {
		Interval interval = new Interval(2, 1);
		assertTrue(interval.isUndefined());
	}

	@Test
	public void testAdd() {
		assertEquals(interval(1, 9),
				interval(-3, 2)
						.add(interval(4, 7)));
	}

	/**
	 * Makes an interval.
	 * @param low limit.
	 * @param high limit.
	 * @return the new interval.
	 */
	public static Interval interval(double low, double high) {
		return new Interval(low, high);
	}

	/**
	 * Makes a inverted interval.
	 * @param low limit.
	 * @param high limit.
	 * @return the new, inverted interval.
	 */
	public static Interval invertedInterval(double low, double high) {
		Interval interval = interval(low, high);
		interval.setInverted(true);
		return interval;
	}

	public static Interval interval(double singleton) {
		return new Interval(singleton);
	}

	@Test
	public void testSub() {
		assertEquals(interval(-5, 5),
				interval(-1, 3)
						.subtract(interval(-2, 4)));
	}

	@Test
	public void isWhole() {
		Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		assertTrue(interval.isWhole());
	}

	@Test
	public void testSingleton() {
		Interval interval = new Interval(2);
		assertTrue(interval.isSingleton());
	}

	@Test
	public void testWholeIsNotSingleton() {
		assertFalse(IntervalConstants.whole().isSingleton());
	}

	@Test
	public void testIsZeroWithinPrecision() {
		double delta = RMath.prev(PRECISION);
		assertTrue(interval(0, 0). isZero());
		assertTrue(interval(0, delta). isZero());
		assertTrue(interval(-delta, 0). isZero());
		assertTrue(interval(-delta, delta). isZero());
		assertTrue(interval(delta). isZero());
	}

	@Test
	public void testHasZero() {
		assertTrue(new Interval(-1, 1).hasZero());
		assertTrue(new Interval(0, 1).hasZero());
		assertTrue(new Interval(-1, 0).hasZero());
		assertTrue(whole().hasZero());
		assertTrue(IntervalConstants.zero().hasZero());
	}

	@Test
	public void testHasNotZero() {
		assertFalse(new Interval(2, 6).hasZero());
		assertFalse(new Interval(-2, -0.1).hasZero());
		assertFalse(new Interval(Double.NEGATIVE_INFINITY, -2).hasZero());
		assertFalse(new Interval(1, Double.POSITIVE_INFINITY).hasZero());
	}

	@Test
	public void testEmpty() {
		assertTrue(new Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).isUndefined());
	}

	@Test
	public void testEmptyConstant() {
		assertTrue(undefined().isUndefined());
	}

	@Test
	public void testOverlap() {
		Interval a = new Interval(-1, 1);
		Interval b = new Interval(-0.5, 0.5);
		Interval c = new Interval(0.6, 1.5);
		assertTrue(a.isOverlap(b));
		assertTrue(a.isOverlap(c));
		assertFalse(b.isOverlap(c));
	}

	@Test
	public void testNotOverlapWithEmptyInterval() {
		Interval a = new Interval(-1, 1);
		assertFalse(a.isOverlap(undefined()));
		assertFalse(undefined().isOverlap(a));
	}

	@Test
	public void testNegative() {
		assertEquals(interval(-3, -2), interval(2, 3).negative());
		assertEquals(interval(-2, 1), interval(-1, 2).negative());
		assertEquals(interval(2, 3), interval(-3, -2).negative());
		assertTrue(whole().negative().isWhole());
	}

	@Test
	public void testIntervalToString() {
		assertEquals("Interval [-1.0, 1.0]", interval(-1, 1).toString().trim());
	}

	@Test
	public void testInvertedIntervalToString() {
		assertEquals("Interval [-1.0, 1.0] Inverted",
				interval(-1, 1).invert().toString().trim());
	}

	@Test
	public void testIntervalSingletonToString() {
		assertEquals("Interval [-1.0]", new Interval(-1).toString().trim());
	}

	@Test
	public void testEmptyIntervalToString() {
		assertEquals("Interval []", undefined().toString().trim());
	}

	@Test
	public void testWholeIntervalToString() {
		assertEquals("Interval [-Infinity, Infinity]", whole().toString().trim());
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(1d, 2d),
				interval(1, 2).hashCode());
	}
}
