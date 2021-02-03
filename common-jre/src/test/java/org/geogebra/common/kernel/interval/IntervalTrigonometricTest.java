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
		assertEquals(IntervalConstants.one(), interval(0, 0).cos());
		assertEquals(interval(0, 1), interval(0, PI / 2).cos());
		assertEquals(interval(-1, 1), interval(-PI, PI).cos());
		assertEquals(interval(-1, 1), interval(0, (3 * PI) / 2).cos());
		assertEquals(interval(-1, 0), interval(PI, (3 * PI) / 2).cos());
		assertEquals(interval(-1, -1), interval(-PI, -PI).cos());
		assertEquals(interval(-1, 1), interval(-PI, PI).cos());
		assertEquals(zero(), interval(PI / 2, PI / 2).cos());
		assertEquals(zero(), interval(-PI / 2, -PI / 2).cos());
		assertEquals(interval(-1, 1), interval(-2 * PI, PI).cos());
		assertEquals(interval(-1, 1), interval(-3 * PI / 2, PI).cos());
		assertEquals(interval(-1, 0), interval(PI / 2, PI).cos());
		assertEquals(interval(-1, 1), interval(-PI / 2, PI).cos());
		assertEquals(interval(-1, 0), interval(PI / 2, PI).cos());
		assertEquals(interval(-1, 1), IntervalConstants.whole().cos());
	}

	@Test
	public void testCosWithInfinity() {
		assertEquals(interval(-1, 1),
				interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).cos());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).cos().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).cos().isEmpty());
	}

	@Test
	public void testSin() {
		assertEquals(interval(0, 0), interval(0, 0).sin());
		assertEquals(interval(0, 1), interval(0, PI / 2).sin());
		assertEquals(interval(-1, 1), interval(0, 3 * PI / 2).sin());
		assertEquals(interval(-1, 0), interval(PI, 3 * PI / 2).sin());
		assertEquals(interval(0, 0), interval(-PI, -PI).sin());
		assertEquals(interval(1, 1), interval(PI / 2, PI / 2).sin());
		assertEquals(interval(-1, -1), interval(-PI / 2, -PI / 2).sin());
		assertEquals(interval(-1, 0), interval(-PI, 0).sin());
		assertEquals(interval(0, 1), interval(-2 * PI, -3 * PI / 2).sin());
		double p = 2 * PI;
		assertEquals(interval(0, 1), interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2).sin());
	}

	@Test
	public void testSinWithInfinity() {
		assertEquals(interval(-1, 1), interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).sin());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).sin().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).sin().isEmpty());
	}

	@Test
	public void testTan() {
		assertEquals(interval(0, 0), interval(0, 0).tan());
		assertEquals(interval(0, 0), interval(PI, PI).tan());
		assertEquals(interval(0, 0), interval(-PI, -PI).tan());
		assertEquals(interval(-1, 1), interval(-PI / 4, PI / 4).tan());
		assertEquals(interval(-1, 1), interval(-9 * PI / 4, -7 * PI / 4).tan());
		assertEquals(interval(-1, 1), interval(7 * PI / 4, 9 * PI / 4).tan());
		assertEquals(whole(),
				interval(PI / 2, PI / 2).tan());
		assertEquals(whole(),
				interval(5 * PI / 2, 5 * PI / 2).tan());
		assertEquals(whole(),
				interval(-5 * PI / 2, -5 * PI / 2).tan());
		assertEquals(whole(),
				interval(0, PI / 2).tan());
		assertEquals(interval(0.16767801556, 0.18877817478),
				interval(-2.975460122699386, -2.955010224948875).tan());
	}

	@Test
	public void testTanWithInfinity() {
		assertEquals(undefined(), IntervalConstants.whole().tan());
	}

	@Test
	public void testAsin() {
		assertEquals(interval(0, 0), interval(0, 0).asin());
		assertEquals(interval(-1.57079633, 1.57079633), interval(-1, 1).asin());
		assertEquals(interval(-1.57079633, 1.57079633), interval(-10, 10).asin());
		assertTrue(interval(-10, -10).asin().isEmpty());
	}

	@Test
	public void testAcos() {
		assertEquals(interval(0, 0), interval(1, 1).acos());
		assertEquals(interval(0, PI / 2), interval(0, 1).acos());
		assertEquals(interval(0, PI), interval(-1, 1).acos());
		assertEquals(interval(0, PI), interval(-10, 10).acos());
		assertTrue(interval(-10, -10).acos().isEmpty());
	}

	@Test
	public void testAtan() {
		assertEquals(interval(0, 0), interval(0, 0).atan());
		assertEquals(interval(-0.785398163, 0.785398163), interval(-1, 1).atan());
	}

	@Test
	public void testSinh() {
		assertEquals(interval(0, 0), interval(0, 0).sinh());
		assertEquals(interval(-3.62686040785, 3.62686040785), interval(-2, 2).sinh());
	}

	@Test
	public void testCosh() {
		assertEquals(interval(1, 1), interval(0, 0).cosh());
		assertEquals(interval(1, 3.76219569108), interval(-2, 2).cosh());
		assertEquals(interval(3.76219569108, 3.76219569108), interval(-2, -2).cosh());
		assertEquals(interval(3.76219569108, 3.76219569108), interval(2, 2).cosh());

	}

	@Test
	public void testTanh() {
		assertEquals(interval(0, 0), interval(0, 0).tanh());
		assertEquals(interval(-0.99932929973, 0.99932929973),
				interval(-4, 4).tanh());
		assertEquals(interval(-1, 1), interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).tanh());
	}

	@Test
	public void testSinLnXNegative() {
		assertEquals(IntervalConstants.empty(), new Interval(interval(-15, 0).log()).sin());
	}

	@Test
	public void testSinUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().sin());
	}

	@Test
	public void testCosUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().cos());
	}

	@Test
	public void testTanUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().tan());
	}

	@Test
	public void testCscUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().csc());
	}

	@Test
	public void testSecUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().sec());
	}

	@Test
	public void testSinhUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().sinh());
	}

	@Test
	public void testCoshUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().cosh());
	}

	@Test
	public void testAsinUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().asin());
	}

	@Test
	public void testAcosUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().acos());
	}

	@Test
	public void testAtanUndefinedShouldReturnUndefined() {
		assertEquals(undefined(), undefined().atan());
	}
}
