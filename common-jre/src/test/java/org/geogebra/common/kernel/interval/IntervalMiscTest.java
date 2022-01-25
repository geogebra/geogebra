package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.apache.commons.math3.util.FastMath.nextAfter;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalOperands.abs;
import static org.geogebra.common.kernel.interval.IntervalOperands.difference;
import static org.geogebra.common.kernel.interval.IntervalOperands.exp;
import static org.geogebra.common.kernel.interval.IntervalOperands.hull;
import static org.geogebra.common.kernel.interval.IntervalOperands.intersect;
import static org.geogebra.common.kernel.interval.IntervalOperands.log;
import static org.geogebra.common.kernel.interval.IntervalOperands.log10;
import static org.geogebra.common.kernel.interval.IntervalOperands.log2;
import static org.geogebra.common.kernel.interval.IntervalOperands.union;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalMiscTest {

	@Test
	public void testExp() {
		assertEquals(interval(0.36787944117, 2.71828182846), exp(interval(-1, 1)));
		assertEquals(interval(0.04978706836, 20.0855369232), exp(interval(-3, 3)));
	}

	@Test
	public void testLog() {
		assertEquals(interval(0, 0), log(interval(1, 1)));
		assertEquals(interval(0, 3), log(interval(1, Math.exp(3))));
		assertEquals(IntervalConstants.undefined(),
				log(interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testLog10() {
		assertEquals(interval(0, 0), log10(interval(1, 1)));
		assertEquals(interval(0, 1), log10(interval(1, 10)));
		assertEquals(interval(0, 2), log10(interval(1, 100)));
	}

	@Test
	public void testLog2() {
		assertEquals(interval(0, 0), log2(interval(1, 1)));
		assertEquals(interval(0, 1), log2(interval(1, 2)));
		assertEquals(interval(0, 3), log2(interval(1, 8)));
	}

	@Test
	public void testHull() {
		assertEquals(interval(-1, 7),
				hull(interval(-1, 1), interval(5, 7)));
		assertEquals(interval(-1, 1),
				hull(interval(-1, 1), new Interval(undefined())));
		assertEquals(interval(-1, 1),
				hull(new Interval(undefined()), interval(-1, 1)));
		assertTrue(hull(undefined(), undefined()).isUndefined());
	}

	@Test
	public void testIntersection() {
		assertTrue(intersect(interval(-1, 1), interval(5, 7)).isUndefined());
		assertTrue(intersect(interval(-1, 1), undefined()).isUndefined());
		assertEquals(interval(0, 1),
				intersect(interval(-1, 1), interval(0, 7)));
	}

	@Test
	public void testUnion() {
		assertEquals(interval(1, 4),
				union(interval(1, 3), interval(2, 4)));
	}

	@Test
	public void testNonOverlappingUnionShouldBeEmpty() {
		assertEquals(undefined(), union(interval(1, 2), interval(3, 4)));
	}

	@Test
	public void testDifference() {
		assertEquals(interval(3, 4),
				difference(interval(3, 5), interval(4, 6)));

		assertEquals(interval(5, 6),
				difference(interval(4, 6), interval(3, 5)));

		assertEquals(interval(4, 6),
				difference(interval(4, 6), interval(8, 9)));

		Interval diff = difference(interval(0, 3), interval(0, 1));
		assertTrue(diff.getLow() > 1 && diff.getHigh() == 3);

		diff = difference(interval(0, 3), interval(1, 3));
		assertTrue(diff.getLow() == 0 && diff.getHigh() < 1);

		assertTrue(difference(interval(0, 3), interval(0, 3))
				.isUndefined());

		assertEquals(interval(0, 1),
				difference(interval(0, 1), undefined()));

		assertTrue(difference(interval(0, 1), whole()).isUndefined());

		assertTrue(difference(interval(0, POSITIVE_INFINITY), interval(0, POSITIVE_INFINITY))
				.isUndefined());
		assertTrue(difference(interval(NEGATIVE_INFINITY, 0), interval(NEGATIVE_INFINITY, 0))
				.isUndefined());
		assertTrue(difference(interval(NEGATIVE_INFINITY, 0), whole()).isUndefined());
		assertTrue(difference(whole(), whole()).isUndefined());

		diff = difference(interval(3, nextAfter(5, NEGATIVE_INFINITY)), interval(4, 6));
		assertTrue(diff.getLow() == 3 && diff.getHigh() < 4);

		assertEquals(interval(5, 6),
				difference(interval(4, 6), interval(3, nextAfter(5, NEGATIVE_INFINITY))));
	}

	@Test()
	public void testEmptyDifference() {
		assertEquals(undefined(), difference(interval(1, 4), interval(2, 3)));
	}

	@Test
	public void testAbs() {
		assertEquals(interval(0, 1), abs(interval(-1, 1)));
		assertEquals(interval(2, 3), abs(interval(-3, -2)));
		assertEquals(interval(2, 3), abs(interval(2, 3)));
	}

	@Test
	public void testAbs1() {
		assertEquals(interval(4, POSITIVE_INFINITY), abs(invertedInterval(-4, 5)));
		assertEquals(interval(5, POSITIVE_INFINITY), abs(invertedInterval(-8, 5)));
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
}