package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.shouldEqual;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalTrigonometricTest {

	@Test
	public void testPiTwice() {
		Interval interval = IntervalConstants.piTwice();
		assertArrayEquals(new double[] {PI_TWICE_LOW, PI_TWICE_HIGH},
				interval.toArray(), 0);
	}

	@Test
	public void testCos() {
		shouldEqual(IntervalConstants.one(), interval(0, 0).cos());
		shouldEqual(interval(0, 1), interval(0, PI / 2).cos());
		shouldEqual(interval(-1, 1), interval(-PI, PI).cos());
		shouldEqual(interval(-1, 1), interval(0, (3 * PI) / 2).cos());
		shouldEqual(interval(-1, 0), interval(PI, (3 * PI) / 2).cos());
		shouldEqual(interval(-1, -1), interval(-PI, -PI).cos());
		shouldEqual(interval(-1, 1), interval(-PI, PI).cos());
		shouldEqual(zero(), interval(PI / 2, PI / 2).cos());
		shouldEqual(zero(), interval(-PI / 2, -PI / 2).cos());
		shouldEqual(interval(-1, 1), interval(-2 * PI, PI).cos());
		shouldEqual(interval(-1, 1), interval(-3 * PI / 2, PI).cos());
		shouldEqual(interval(-1, 0), interval(PI / 2, PI).cos());
		shouldEqual(interval(-1, 1), interval(-PI / 2, PI).cos());
		shouldEqual(interval(-1, 0), interval(PI / 2, PI).cos());
	}

	@Test
	public void testCosWithInfinity() {
		shouldEqual(interval(-1, 1),
				interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).cos());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).cos().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).cos().isEmpty());
	}

	@Test
	public void testSin() {
		shouldEqual(interval(0, 0), interval(0, 0).sin());
		shouldEqual(interval(0, 1), interval(0, PI / 2).sin());
		shouldEqual(interval(-1, 1), interval(0, 3 * PI / 2).sin());
		shouldEqual(interval(-1, 0), interval(PI, 3 * PI / 2).sin());
		shouldEqual(interval(0, 0), interval(-PI, -PI).sin());
		shouldEqual(interval(1, 1), interval(PI / 2, PI / 2).sin());
		shouldEqual(interval(-1, -1), interval(-PI / 2, -PI / 2).sin());
		shouldEqual(interval(-1, 0), interval(-PI, 0).sin());
		shouldEqual(interval(0, 1), interval(-2 * PI, -3 * PI / 2).sin());
		double p = 2 * PI;
		shouldEqual(interval(0, 1), interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2).sin());
	}

	@Test
	public void testSinWithInfinity() {
		shouldEqual(interval(-1, 1), interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).sin());
		assertTrue(interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY).sin().isEmpty());
		assertTrue(interval(POSITIVE_INFINITY, POSITIVE_INFINITY).sin().isEmpty());
	}

	@Test
	public void testTan() {
		shouldEqual(interval(0, 0), interval(0, 0).tan());
		shouldEqual(interval(0, 0), interval(PI, PI).tan());
		shouldEqual(interval(0, 0), interval(-PI, -PI).tan());
		shouldEqual(interval(-1, 1), interval(-PI / 4, PI / 4).tan());
		shouldEqual(interval(-1, 1), interval(-9 * PI / 4, -7 * PI / 4).tan());
		shouldEqual(interval(-1, 1), interval(7 * PI / 4, 9 * PI / 4).tan());
		shouldEqual(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(PI / 2, PI / 2).tan());
		shouldEqual(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(5 * PI / 2, 5 * PI / 2).tan());
		shouldEqual(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(-5 * PI / 2, -5 * PI / 2).tan());
		shouldEqual(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				interval(0, PI / 2).tan());
		shouldEqual(interval(0.16767801556, 0.18877817478),
				interval(-2.975460122699386, -2.955010224948875).tan());
	}

	@Test
	public void testTanWithInfinity() {
		assertTrue(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).tan().isWhole());
		assertTrue(interval(POSITIVE_INFINITY, NEGATIVE_INFINITY).tan().isEmpty());
	}

	@Test
	public void testAsin() {
		shouldEqual(interval(0, 0), interval(0, 0).asin());
		shouldEqual(interval(-1.57079633, 1.57079633), interval(-1, 1).asin());
		shouldEqual(interval(-1.57079633, 1.57079633), interval(-10, 10).asin());
		assertTrue(interval(-10, -10).asin().isEmpty());
	}

	@Test
	public void testAcos() {
		shouldEqual(interval(0, 0), interval(1, 1).acos());
		shouldEqual(interval(0, PI / 2), interval(0, 1).acos());
		shouldEqual(interval(0, PI), interval(-1, 1).acos());
		shouldEqual(interval(0, PI), interval(-10, 10).acos());
		assertTrue(interval(-10, -10).acos().isEmpty());
	}

	@Test
	public void testAtan() {
		shouldEqual(interval(0, 0), interval(0, 0).atan());
		shouldEqual(interval(-0.785398163, 0.785398163), interval(-1, 1).atan());
	}

	@Test
	public void testSinh() {
		shouldEqual(interval(0, 0), interval(0, 0).sinh());
		shouldEqual(interval(-3.62686040785, 3.62686040785), interval(-2, 2).sinh());
	}

	@Test
	public void testCosh() {
		shouldEqual(interval(1, 1), interval(0, 0).cosh());
		shouldEqual(interval(1, 3.76219569108), interval(-2, 2).cosh());
		shouldEqual(interval(3.76219569108, 3.76219569108), interval(-2, -2).cosh());
		shouldEqual(interval(3.76219569108, 3.76219569108), interval(2, 2).cosh());

	}

	@Test
	public void testTanh() {
		shouldEqual(interval(0, 0), interval(0, 0).tanh());
		shouldEqual(interval(-0.99932929973, 0.99932929973),
				interval(-4, 4).tanh());
		shouldEqual(interval(-1, 1), interval(NEGATIVE_INFINITY, POSITIVE_INFINITY).tanh());
	}
}
