package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalOperands.acos;
import static org.geogebra.common.kernel.interval.IntervalOperands.asin;
import static org.geogebra.common.kernel.interval.IntervalOperands.atan;
import static org.geogebra.common.kernel.interval.IntervalOperands.cos;
import static org.geogebra.common.kernel.interval.IntervalOperands.cosh;
import static org.geogebra.common.kernel.interval.IntervalOperands.divide;
import static org.geogebra.common.kernel.interval.IntervalOperands.log;
import static org.geogebra.common.kernel.interval.IntervalOperands.sin;
import static org.geogebra.common.kernel.interval.IntervalOperands.sinh;
import static org.geogebra.common.kernel.interval.IntervalOperands.tan;
import static org.geogebra.common.kernel.interval.IntervalOperands.tanh;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalTrigonometricTest {

	@Test
	public void testPiTwice() {
		Interval interval = IntervalConstants.piTwice();
		assertArrayEquals(new double[]{PI_TWICE_LOW, PI_TWICE_HIGH},
				interval.toArray(), 0);
	}

	@Test
	public void testCos() {
		assertEquals(IntervalConstants.one(), cos(interval(0, 0)));
		assertEquals(interval(0, 1), cos(interval(0, PI / 2)));
		assertEquals(interval(-1, 1), cos(interval(-PI, PI)));
		assertEquals(interval(-1, 1), cos(interval(0, (3 * PI) / 2)));
		assertEquals(interval(-1, 0), cos(interval(PI, (3 * PI) / 2)));
		assertEquals(interval(-1, -1), cos(interval(-PI, -PI)));
		assertEquals(interval(-1, 1), cos(interval(-PI, PI)));
		assertEquals(zero(), cos(interval(PI / 2, PI / 2)));
		assertEquals(zero(), cos(interval(-PI / 2, -PI / 2)));
		assertEquals(interval(-1, 1), cos(interval(-2 * PI, PI)));
		assertEquals(interval(-1, 1), cos(interval(-3 * PI / 2, PI)));
		assertEquals(interval(-1, 0), cos(interval(PI / 2, PI)));
		assertEquals(interval(-1, 1), cos(interval(-PI / 2, PI)));
		assertEquals(interval(-1, 0), cos(interval(PI / 2, PI)));
		assertEquals(interval(-1, 1), cos(whole()));
	}

	@Test
	public void testCosWithInfinity() {
		assertEquals(interval(-1, 1),
				cos(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY)));
		assertEquals(interval(-1, 1),
				cos(interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
		assertEquals(interval(-1, 1),
				cos(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	public void testSin() {
		assertEquals(interval(0, 0), sin(interval(0, 0)));
		assertEquals(interval(0, 1), sin(interval(0, PI / 2)));
		assertEquals(interval(-1, 1), sin(interval(0, 3 * PI / 2)));
		assertEquals(interval(-1, 0), sin(interval(PI, 3 * PI / 2)));
		assertEquals(interval(0, 0), sin(interval(-PI, -PI)));
		assertEquals(interval(1, 1), sin(interval(PI / 2, PI / 2)));
		assertEquals(interval(-1, -1), sin(interval(-PI / 2, -PI / 2)));
		assertEquals(interval(-1, 0), sin(interval(-PI, 0)));
		assertEquals(interval(0, 1), sin(interval(-2 * PI, -3 * PI / 2)));
		double p = 2 * PI;
		assertEquals(interval(0, 1), sin(interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2)));
	}

	@Test
	public void testSinWithInfinity() {
		assertEquals(interval(-1, 1),
				sin(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY)));
		assertTrue(sin(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)).isUndefined());
		assertTrue(sin(interval(POSITIVE_INFINITY, POSITIVE_INFINITY)).isUndefined());
	}

	@Test
	public void testTan() {
		assertEquals(interval(0, 0), tan(interval(0, 0)));
		assertEquals(interval(0, 0), tan(interval(PI, PI)));
		assertEquals(interval(0, 0), tan(interval(-PI, -PI)));
		assertEquals(interval(-1, 1), tan(interval(-PI / 4, PI / 4)));
		assertEquals(interval(-1, 1), tan(interval(-9 * PI / 4, -7 * PI / 4)));
		assertEquals(interval(-1, 1), tan(interval(7 * PI / 4, 9 * PI / 4)));
		assertEquals(interval(0.16767801556, 0.18877817478),
				tan(interval(-2.975460122699386, -2.955010224948875)));
	}

	@Test
	public void testAsin() {
		assertEquals(interval(0, 0), asin(interval(0, 0)));
		assertEquals(interval(-1.57079633, 1.57079633), asin(interval(-1, 1)));
		assertEquals(interval(-1.57079633, 1.57079633), asin(interval(-10, 10)));
		assertTrue(asin(interval(-10, -10)).isUndefined());
	}

	@Test
	public void testAcos() {
		assertEquals(interval(0, 0), acos(interval(1, 1)));
		assertEquals(interval(0, PI / 2), acos(interval(0, 1)));
		assertEquals(interval(0, PI), acos(interval(-1, 1)));
		assertEquals(interval(0, PI), acos(interval(-10, 10)));
		assertTrue(acos(interval(-10, -10)).isUndefined());
	}

	@Test
	public void testAtan() {
		assertEquals(interval(0, 0), atan(interval(0, 0)));
		assertEquals(interval(-0.785398163, 0.785398163), atan(interval(-1, 1)));
	}

	@Test
	public void testSinh() {
		assertEquals(interval(0, 0), sinh(interval(0, 0)));
		assertEquals(interval(-3.62686040785, 3.62686040785), sinh(interval(-2, 2)));
	}

	@Test
	public void testCosh() {
		assertEquals(interval(1, 1), cosh(interval(0, 0)));
		assertEquals(interval(1, 3.76219569108), cosh(interval(-2, 2)));
		assertEquals(interval(3.76219569108, 3.76219569108), cosh(interval(-2, -2)));
		assertEquals(interval(3.76219569108, 3.76219569108), cosh(interval(2, 2)));

	}

	@Test
	public void testTanh() {
		assertEquals(interval(0, 0), tanh(interval(0, 0)));
		assertEquals(interval(-0.99932929973, 0.99932929973),
				tanh(interval(-4, 4)));
		assertEquals(interval(-1, 1),
				tanh(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testSinLnXNegative() {
		assertEquals(IntervalConstants.undefined(),
				new Interval(sin(log(interval(-15, 0)))));
	}

	@Test
	public void testInvertedSinShouldReturnInMinusOneOneRange() {
		assertEquals(interval(-1, 1), sin(invertedInterval(2, 3)));
	}

	@Test
	public void testInvertedCosShouldReturnInMinusOneOneRange() {
		assertEquals(interval(-1, 1), cos(invertedInterval(2, 3)));
	}

	@Test
	public void testInvertedCosLnShouldReturnInMinusOneOneRange() {
		assertEquals(interval(-1, 1), cos(log(divide(interval(7), zero()))));
	}

}
