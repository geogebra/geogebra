package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.nthRoot;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.pow;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sin;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalRootTest {

	@Test
	public void testSqrtPositive() {
		assertEquals(interval(2, 3), 
				sqrt(interval(4, 9)));
	}

	@Test
	public void testSqrtMixed() {
		assertEquals(interval(0, 3), 
				sqrt(interval(-4, 9)));
	}

	@Test
	public void testSqrtNegative() {
		assertEquals(undefined(), sqrt(interval(-9, -4)));
		assertEquals(zero(), sqrt(interval(0, 0)));
		assertEquals(interval(0, 1), sqrt(interval(0, 1)));
	}

	@Ignore
	@Test
	public void testNthRootInNegativeInterval() {
		assertEquals(interval(-2, 2), nthRoot(interval(-8, 8), 3));
		assertEquals(interval(0.5, Double.POSITIVE_INFINITY),
				nthRoot(interval(-8, 8), -3));
	}

	@Ignore
	@Test
	public void testNthRoot() {
		assertEquals(undefined(), nthRoot(interval(-27, -8), -3));
		assertEquals(undefined(), nthRoot(interval(-27, -8), 2));
		assertEquals(interval(0, 3), nthRoot(interval(-4, 9), 2));
		assertEquals(interval(-3, 2), nthRoot(interval(-27, 8), 3));
		assertEquals(interval(2, 3), nthRoot(interval(4, 9), 2));
		assertEquals(interval(2, 3), nthRoot(interval(8, 27), 3));
		assertEquals(interval(2, 2), nthRoot(interval(8, 8), 3));
	}

	@Test
	public void testNthRootWithNegativeN() {
		assertEquals(interval(-3),
				nthRoot(interval(-27), interval(3)));
		assertEquals(interval(-3, -2),
				nthRoot(interval(-27, -8), interval(3)));
	}

	@Test
	public void testSqrtSinUndef() {
		assertTrue(sqrt(sin(interval(4, 5))).isUndefined());
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(sqrt(interval(1, 2)), pow(interval(1, 2), 0.5));
	}

	@Test
	public void testEvenNRootWithInvertedXAroundZero() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		assertEquals(interval(2.2360679774998005, Double.POSITIVE_INFINITY),
				nthRoot(x.multiplicativeInverse(), 2));
	}

	@Test
	public void testOddNRootWithInvertedXAroundZero() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		assertEquals(interval(-78669.43188987061, 1.7099759466767028).invert(),
				nthRoot(x.multiplicativeInverse(), 3));
	}
}