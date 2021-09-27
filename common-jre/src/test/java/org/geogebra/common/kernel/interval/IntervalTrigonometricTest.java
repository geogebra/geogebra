package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
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
		assertEquals(IntervalConstants.one(), interval(0, 0).getEvaluate().cos());
		assertEquals(interval(0, 1), interval(0, PI / 2).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(-PI, PI).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(0, (3 * PI) / 2).getEvaluate().cos());
		assertEquals(interval(-1, 0), interval(PI, (3 * PI) / 2).getEvaluate().cos());
		assertEquals(interval(-1, -1), interval(-PI, -PI).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(-PI, PI).getEvaluate().cos());
		assertEquals(zero(), interval(PI / 2, PI / 2).getEvaluate().cos());
		assertEquals(zero(), interval(-PI / 2, -PI / 2).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(-2 * PI, PI).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(-3 * PI / 2, PI).getEvaluate().cos());
		assertEquals(interval(-1, 0), interval(PI / 2, PI).getEvaluate().cos());
		assertEquals(interval(-1, 1), interval(-PI / 2, PI).getEvaluate().cos());
		assertEquals(interval(-1, 0), interval(PI / 2, PI).getEvaluate().cos());
		assertEquals(interval(-1, 1), IntervalConstants.whole().getEvaluate().cos());
	}

	@Test
	public void testCosWithInfinity() {
		assertEquals(interval(-1, 1),
				interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).getEvaluate().cos());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).getEvaluate().cos().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).getEvaluate().cos().isEmpty());
	}

	@Test
	public void testSin() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().sin());
		assertEquals(interval(0, 1), interval(0, PI / 2).getEvaluate().sin());
		assertEquals(interval(-1, 1), interval(0, 3 * PI / 2).getEvaluate().sin());
		assertEquals(interval(-1, 0), interval(PI, 3 * PI / 2).getEvaluate().sin());
		assertEquals(interval(0, 0), interval(-PI, -PI).getEvaluate().sin());
		assertEquals(interval(1, 1), interval(PI / 2, PI / 2).getEvaluate().sin());
		assertEquals(interval(-1, -1), interval(-PI / 2, -PI / 2).getEvaluate().sin());
		assertEquals(interval(-1, 0), interval(-PI, 0).getEvaluate().sin());
		assertEquals(interval(0, 1), interval(-2 * PI, -3 * PI / 2).getEvaluate().sin());
		double p = 2 * PI;
		assertEquals(interval(0, 1), interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2).getEvaluate().sin());
	}

	@Test
	public void testSinWithInfinity() {
		assertEquals(interval(-1, 1),
				interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).getEvaluate().sin());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).getEvaluate().sin().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).getEvaluate().sin().isEmpty());
	}

	@Test
	public void testTan() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().tan());
		assertEquals(interval(0, 0), interval(PI, PI).getEvaluate().tan());
		assertEquals(interval(0, 0), interval(-PI, -PI).getEvaluate().tan());
		assertEquals(interval(-1, 1), interval(-PI / 4, PI / 4).getEvaluate().tan());
		assertEquals(interval(-1, 1), interval(-9 * PI / 4, -7 * PI / 4).getEvaluate().tan());
		assertEquals(interval(-1, 1), interval(7 * PI / 4, 9 * PI / 4).getEvaluate().tan());
		assertEquals(whole(),
				interval(PI / 2, PI / 2).getEvaluate().tan());
		assertEquals(whole(),
				interval(5 * PI / 2, 5 * PI / 2).getEvaluate().tan());
		assertEquals(whole(),
				interval(-5 * PI / 2, -5 * PI / 2).getEvaluate().tan());
		assertEquals(whole(),
				interval(0, PI / 2).getEvaluate().tan());
		assertEquals(interval(0.16767801556, 0.18877817478),
				interval(-2.975460122699386, -2.955010224948875).getEvaluate().tan());
	}

	@Test
	public void testTanWithInfinity() {
		assertEquals(undefined(), IntervalConstants.whole().getEvaluate().tan());
	}

	@Test
	public void testAsin() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().asin());
		assertEquals(interval(-1.57079633, 1.57079633), interval(-1, 1).getEvaluate().asin());
		assertEquals(interval(-1.57079633, 1.57079633), interval(-10, 10).getEvaluate().asin());
		assertTrue(interval(-10, -10).getEvaluate().asin().isEmpty());
	}

	@Test
	public void testAcos() {
		assertEquals(interval(0, 0), interval(1, 1).getEvaluate().acos());
		assertEquals(interval(0, PI / 2), interval(0, 1).getEvaluate().acos());
		assertEquals(interval(0, PI), interval(-1, 1).getEvaluate().acos());
		assertEquals(interval(0, PI), interval(-10, 10).getEvaluate().acos());
		assertTrue(interval(-10, -10).getEvaluate().acos().isEmpty());
	}

	@Test
	public void testAtan() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().atan());
		assertEquals(interval(-0.785398163, 0.785398163), interval(-1, 1).getEvaluate().atan());
	}

	@Test
	public void testSinh() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().sinh());
		assertEquals(interval(-3.62686040785, 3.62686040785), interval(-2, 2).getEvaluate().sinh());
	}

	@Test
	public void testCosh() {
		assertEquals(interval(1, 1), interval(0, 0).getEvaluate().cosh());
		assertEquals(interval(1, 3.76219569108), interval(-2, 2).getEvaluate().cosh());
		assertEquals(interval(3.76219569108, 3.76219569108), interval(-2, -2).getEvaluate().cosh());
		assertEquals(interval(3.76219569108, 3.76219569108), interval(2, 2).getEvaluate().cosh());

	}

	@Test
	public void testTanh() {
		assertEquals(interval(0, 0), interval(0, 0).getEvaluate().tanh());
		assertEquals(interval(-0.99932929973, 0.99932929973),
				interval(-4, 4).getEvaluate().tanh());
		assertEquals(interval(-1, 1),
				interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).getEvaluate().tanh());
	}

	@Test
	public void testSinLnXNegative() {
		assertEquals(IntervalConstants.empty(),
				new Interval(interval(-15, 0).getEvaluate().log()).getEvaluate().sin());
	}

	@Test
	public void testSinUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().sin());
	}

	@Test
	public void testCosUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().cos());
	}

	@Test
	public void testTanUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().tan());
	}

	@Test
	public void testCscUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().csc());
	}

	@Test
	public void testSecUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().sec());
	}

	@Test
	public void testSinhUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().sinh());
	}

	@Test
	public void testCoshUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().cosh());
	}

	@Test
	public void testAsinUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().asin());
	}

	@Test
	public void testAcosUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().acos());
	}

	@Test
	public void testAtanUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().getEvaluate().atan());
	}
}
