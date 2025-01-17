package org.geogebra.common.kernel.interval.operators;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.apache.commons.math3.util.FastMath.nextAfter;
import static org.geogebra.common.kernel.interval.IntervalConstants.aroundZero;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class IntervalMiscTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();
	
	@Test
	public void testExp() {
		assertEquals(interval(0.3678794411714423, 2.7182818284590455),
				evaluator.exp(interval(-1, 1)));
		assertEquals(interval(0.04978706836786394, 20.08553692318767),
				evaluator.exp(interval(-3, 3)));
	}

	@Test
	public void testLog() {
		assertEquals(interval(0, 0), evaluator.log(interval(1, 1)));
		assertEquals(interval(0, 3), evaluator.log(interval(1, Math.exp(3))));
		assertEquals(IntervalConstants.undefined(),
				evaluator.log(interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testLog10() {
		assertEquals(interval(0, 0), evaluator.log10(interval(1, 1)));
		assertEquals(interval(0, 1), evaluator.log10(interval(1, 10)));
		assertEquals(interval(0, 2), evaluator.log10(interval(1, 100)));
	}

	@Test
	public void testLog2() {
		assertEquals(interval(0, 0), evaluator.log2(interval(1, 1)));
		assertEquals(interval(0, 1), evaluator.log2(interval(1, 2)));
		assertEquals(interval(0, 3), evaluator.log2(interval(1, 8)));
	}

	@Test
	public void testLogB() {
		assertEquals(interval(0, 0), evaluator.logBase(interval(3, 3),
				interval(1, 1)));
		assertEquals(interval(2, 3), evaluator.logBase(interval(2, 2),
				interval(4, 8)));
		assertEquals(interval(2, 4), evaluator.logBase(interval(2, 4),
				interval(16, 16)));
		assertEquals(undefined(), evaluator.logBase(interval(1, 1),
				interval(3, 3)));
	}

	@Test
	public void testHull() {
		assertEquals(interval(-1, 7),
				evaluator.hull(interval(-1, 1), interval(5, 7)));
		assertEquals(interval(-1, 1),
				evaluator.hull(interval(-1, 1), new Interval(undefined())));
		assertEquals(interval(-1, 1),
				evaluator.hull(new Interval(undefined()), interval(-1, 1)));
		assertTrue(evaluator.hull(undefined(), undefined()).isUndefined());
	}

	@Test
	public void testIntersection() {
		assertTrue(evaluator.intersect(interval(-1, 1), interval(5, 7)).isUndefined());
		assertTrue(evaluator.intersect(interval(-1, 1), undefined()).isUndefined());
		assertEquals(interval(0, 1),
				evaluator.intersect(interval(-1, 1), interval(0, 7)));
	}

	@Test
	public void testUnion() {
		assertEquals(interval(1, 4),
				evaluator.union(interval(1, 3), interval(2, 4)));
	}

	@Test
	public void testNonOverlappingUnionShouldBeEmpty() {
		assertEquals(undefined(), evaluator.union(interval(1, 2), interval(3, 4)));
	}

	@Test
	public void testDifference() {
		assertEquals(interval(3, 4),
				evaluator.difference(interval(3, 5), interval(4, 6)));

		assertEquals(interval(5, 6),
				evaluator.difference(interval(4, 6), interval(3, 5)));

		assertEquals(interval(4, 6),
				evaluator.difference(interval(4, 6), interval(8, 9)));

		Interval diff = evaluator.difference(interval(0, 3), interval(0, 1));
		assertTrue(diff.getLow() > 1 && diff.getHigh() == 3);

		diff = evaluator.difference(interval(0, 3), interval(1, 3));
		assertTrue(diff.getLow() == 0 && diff.getHigh() < 1);

		assertTrue(evaluator.difference(interval(0, 3), interval(0, 3))
				.isUndefined());

		assertEquals(interval(0, 1),
				evaluator.difference(interval(0, 1), undefined()));

		assertTrue(evaluator.difference(interval(0, 1), whole()).isUndefined());

		assertTrue(evaluator.difference(interval(0, POSITIVE_INFINITY),
				interval(0, POSITIVE_INFINITY)).isUndefined());
		assertTrue(evaluator.difference(interval(NEGATIVE_INFINITY, 0),
						interval(NEGATIVE_INFINITY, 0)).isUndefined());
		assertTrue(evaluator.difference(interval(NEGATIVE_INFINITY, 0), whole()).isUndefined());
		assertTrue(evaluator.difference(whole(), whole()).isUndefined());

		diff = evaluator.difference(interval(3, nextAfter(5, NEGATIVE_INFINITY)), interval(4, 6));
		assertTrue(diff.getLow() == 3 && diff.getHigh() < 4);

		assertEquals(interval(5, 6),
				evaluator.difference(interval(4, 6), interval(3, nextAfter(5, NEGATIVE_INFINITY))));
	}

	@Test()
	public void testEmptyDifference() {
		assertEquals(undefined(), evaluator.difference(interval(1, 4), interval(2, 3)));
	}

	@Test
	public void testAbs() {
		assertEquals(interval(0, 1), evaluator.abs(interval(-1, 1)));
		assertEquals(interval(2, 3), evaluator.abs(interval(-3, -2)));
		assertEquals(interval(2, 3), evaluator.abs(interval(2, 3)));
	}

	@Test
	public void testAbs1() {
		assertEquals(interval(4, POSITIVE_INFINITY), evaluator.abs(invertedInterval(-4, 5)));
		assertEquals(interval(5, POSITIVE_INFINITY), evaluator.abs(invertedInterval(-8, 5)));
	}

	@Test
	public void testMax() {
		assertEquals(interval(5, 7), Interval.max(interval(-1, 1),
				interval(5, 7)));
		assertEquals(interval(-1, 1),
				Interval.max(undefined(), interval(-1, 1)));
		assertEquals(interval(-1, 1),
				Interval.max(interval(-1, 1), undefined()));
	}

	@Test
	public void testMin() {
		assertEquals(interval(-1, 1),
				Interval.min(interval(-1, 1), interval(5, 7)));
	}

	@Test
	public void testLogXInverseAzZero() {
		Interval x = aroundZero();
		Interval xInverse = evaluator.multiplicativeInverse(x);
		assertEquals(interval(9.210340371976182, POSITIVE_INFINITY), evaluator.log(xInverse));
	}

	@Test
	public void testZeroDividedByLnAroundOne() {
		assertEquals(zero(), evaluator.divide(zero(), evaluator.log(around(0.985375))));
		assertEquals(zero(), evaluator.divide(zero(), evaluator.log(around(1.015625))));
	}

	// APPS-4683
	@Test
	public void testZeroDivLnX() {
		Interval x1 = interval(0.9895833333333334, 1.0);
		Interval x2 = interval(0.9999999999999999, 1.0104166666666665);
		Interval log1 = evaluator.log(x1);
		Interval log2 = evaluator.log(x2);
		Interval div1 = evaluator.divide(zero(), log1);
		Interval div2 = evaluator.divide(zero(), log2);
		assertTrue(div1.isZero() && div2.isZero());
	}

	@Test
	public void lnInverseMultiplyZeroNegativeShouldBeUndefined() {
		Interval x = interval(NEGATIVE_INFINITY, -IntervalConstants.PRECISION);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(undefined(), multiply);
	}

	@Test
	public void lnInverseMultiplyZeroPositiveShouldBeZero() {
		Interval x = interval(0, POSITIVE_INFINITY);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(zero(), multiply);
	}

	@Test
	public void lnInverseMultiplyZeroAroundOneShouldBeZero() {
		Interval x = interval(1.0, 1.015625);
		Interval log = evaluator.log(x);
		Interval inverse = evaluator.inverse(log);
		Interval multiply = evaluator.multiply(zero(), inverse);
		assertEquals(zero(), multiply);
	}
}