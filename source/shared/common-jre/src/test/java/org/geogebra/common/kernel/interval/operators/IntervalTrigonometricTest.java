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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.test.OrderingComparison;
import org.junit.Test;

public class IntervalTrigonometricTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();
	
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
		long time = System.currentTimeMillis();
		assertEquals(interval(0, 1),
				cos(interval(1000 * PI, 1000 * PI + PI / 2)));
		assertEquals(interval(0, 1),
				cos(interval(-1000 * PI, -1000 * PI + PI / 2)));
		assertThat(System.currentTimeMillis() - time, OrderingComparison.lessThan(100L));
	}

	private Interval cos(Interval value) {
		return evaluator.cos(value);
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
		assertEquals(interval(0, 0), evaluator.sin(interval(0, 0)));
		assertEquals(interval(0, 1), evaluator.sin(interval(0, PI / 2)));
		assertEquals(interval(-1, 1), evaluator.sin(interval(0, 3 * PI / 2)));
		assertEquals(interval(-1, 0), evaluator.sin(interval(PI, 3 * PI / 2)));
		assertEquals(interval(0, 0), evaluator.sin(interval(-PI, -PI)));
		assertEquals(interval(1, 1), evaluator.sin(interval(PI / 2, PI / 2)));
		assertEquals(interval(-1, -1), evaluator.sin(interval(-PI / 2, -PI / 2)));
		assertEquals(interval(-1, 0), evaluator.sin(interval(-PI, 0)));
		assertEquals(interval(0, 1), evaluator.sin(interval(-2 * PI, -3 * PI / 2)));
		double p = 2 * PI;
		assertEquals(interval(0, 1), evaluator.sin(interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2)));
	}

	@Test
	public void testSinWithInfinity() {
		assertEquals(interval(-1, 1),
				evaluator.sin(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY)));
		assertTrue(evaluator.sin(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)).isUndefined());
		assertTrue(evaluator.sin(interval(POSITIVE_INFINITY, POSITIVE_INFINITY)).isUndefined());
	}

	@Test
	public void testTan() {
		assertEquals(interval(0, 0), evaluator.tan(interval(0, 0)));
		assertEquals(interval(0, 0), evaluator.tan(interval(PI, PI)));
		assertEquals(interval(0, 0), evaluator.tan(interval(-PI, -PI)));
		assertEquals(interval(-1, 1), evaluator.tan(interval(-PI / 4, PI / 4)));
		assertEquals(interval(-1, 1), evaluator.tan(interval(-9 * PI / 4, -7 * PI / 4)));
		assertEquals(interval(-1, 1), evaluator.tan(interval(7 * PI / 4, 9 * PI / 4)));
		assertEquals(interval(0.16767801556850204, 0.18877817478678283),
				evaluator.tan(interval(-2.975460122699386, -2.955010224948875)));
	}

	@Test
	public void testAsin() {
		assertEquals(interval(0, 0), evaluator.asin(interval(0, 0)));
		assertEquals(interval(-1.5707963267948966, 1.5707963267948966),
				evaluator.asin(interval(-1, 1)));
		assertEquals(interval(-1.5707963267948966, 1.5707963267948966),
				evaluator.asin(interval(-10, 10)));
		assertTrue(evaluator.asin(interval(-10, -10)).isUndefined());
	}

	@Test
	public void testAcos() {
		assertEquals(interval(0, 0), evaluator.acos(interval(1, 1)));
		assertEquals(interval(0, PI / 2), evaluator.acos(interval(0, 1)));
		assertEquals(interval(0, PI), evaluator.acos(interval(-1, 1)));
		assertEquals(interval(0, PI), evaluator.acos(interval(-10, 10)));
		assertTrue(evaluator.acos(interval(-10, -10)).isUndefined());
	}

	@Test
	public void testAtan() {
		assertEquals(interval(0, 0), evaluator.atan(interval(0, 0)));
		assertEquals(interval(-0.7853981633974484, 0.7853981633974484),
				evaluator.atan(interval(-1, 1)));
	}

	@Test
	public void testSinh() {
		assertEquals(interval(0, 0), evaluator.sinh(interval(0, 0)));
		assertEquals(interval(-3.6268604078470195, 3.6268604078470195),
				evaluator.sinh(interval(-2, 2)));
	}

	@Test
	public void testCosh() {
		assertEquals(interval(1, 1), evaluator.cosh(interval(0, 0)));
		assertEquals(interval(1, 3.762195691083632), evaluator.cosh(interval(-2, 2)));
		assertEquals(interval(3.762195691083632), evaluator.cosh(interval(-2, -2)));
		assertEquals(interval(3.762195691083632), evaluator.cosh(interval(2, 2)));

	}

	@Test
	public void testTanh() {
		assertEquals(interval(0, 0), evaluator.tanh(interval(0, 0)));
		assertEquals(interval(-0.9993292997390671, 0.9993292997390671),
				evaluator.tanh(interval(-4, 4)));
		assertEquals(interval(-1, 1),
				evaluator.tanh(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testSinLnXNegative() {
		assertEquals(IntervalConstants.undefined(),
				new Interval(evaluator.sin(evaluator.log(interval(-15, 0)))));
	}

	@Test
	public void testInvertedSinShouldReturnInMinusOneOneRange() {
		assertEquals(interval(-1, 1), evaluator.sin(invertedInterval(2, 3)));
	}

	@Test
	public void testInvertedCosShouldReturnInMinusOneOneRange() {
		assertEquals(interval(-1, 1), cos(invertedInterval(2, 3)));
	}

	@Test
	public void testInvertedCosLnShouldBeUndefined() {
		assertEquals(undefined(), cos(evaluator.log(evaluator.divide(interval(7), zero()))));
	}

}
