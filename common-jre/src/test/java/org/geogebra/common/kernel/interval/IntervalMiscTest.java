package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.apache.commons.math3.util.FastMath.nextAfter;
import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalMiscTest {

	@Test
	public void testExp() {
		assertEquals(interval(0.36787944117, 2.71828182846), interval(-1, 1).getEvaluate().exp());
		assertEquals(interval(0.04978706836, 20.0855369232), interval(-3, 3).getEvaluate().exp());
	}

	@Test
	public void testLog() {
		assertEquals(interval(0, 0), interval(1, 1).getEvaluate().log());
		assertEquals(interval(0, 3), interval(1, Math.exp(3)).getEvaluate().log());
		assertEquals(IntervalConstants.empty(),
				interval(NEGATIVE_INFINITY, -1).getEvaluate().log());
	}

	@Test
	public void testLog10() {
		assertEquals(interval(0, 0), interval(1, 1).getEvaluate().log10());
		assertEquals(interval(0, 1), interval(1, 10).getEvaluate().log10());
		assertEquals(interval(0, 2), interval(1, 100).getEvaluate().log10());
	}

	@Test
	public void testLog2() {
		assertEquals(interval(0, 0), interval(1, 1).getEvaluate().log2());
		assertEquals(interval(0, 1), interval(1, 2).getEvaluate().log2());
		assertEquals(interval(0, 3), interval(1, 8).getEvaluate().log2());
	}

	@Test
	public void testHull() {
		assertEquals(interval(-1, 7),
				interval(-1, 1).getEvaluate().hull(interval(5, 7)));
		assertEquals(interval(-1, 1),
				interval(-1, 1).getEvaluate().hull(new Interval(empty())));
		assertEquals(interval(-1, 1),
				new Interval(empty()).getEvaluate().hull(interval(-1, 1)));
		assertTrue(empty().getEvaluate().hull(empty()).isEmpty());
	}

	@Test
	public void testIntersection() {
		assertTrue(interval(-1, 1).getEvaluate().intersect(interval(5, 7)).isEmpty());
		assertTrue(interval(-1, 1).getEvaluate().intersect(empty()).isEmpty());
		assertEquals(interval(0, 1),
				interval(-1, 1).getEvaluate().intersect(interval(0, 7)));
	}

	@Test
	public void testUnion() throws IntervalsNotOverlapException {
		assertEquals(interval(1, 4),
				interval(1, 3).getEvaluate().union(interval(2, 4)));
	}

	@Test(expected = IntervalsNotOverlapException.class)
	public void testUnionWithException() throws IntervalsNotOverlapException {
		interval(1, 2).getEvaluate().union(interval(3, 4));
	}

	@Test
	public void testDifference() throws IntervalsDifferenceException {
		assertEquals(interval(3, 4),
				interval(3, 5).getEvaluate().difference(interval(4, 6)));

		assertEquals(interval(5, 6),
				interval(4, 6).getEvaluate().difference(interval(3, 5)));

		assertEquals(interval(4, 6),
				interval(4, 6).getEvaluate().difference(interval(8, 9)));

		Interval diff = interval(0, 3).getEvaluate().difference(interval(0, 1));
		assertTrue(diff.getLow() > 1 && diff.getHigh() == 3);

		diff = interval(0, 3).getEvaluate().difference(interval(1, 3));
		assertTrue(diff.getLow() == 0 && diff.getHigh() < 1);

		assertTrue(interval(0, 3).getEvaluate().difference(interval(0, 3))
				.isEmpty());

		assertEquals(interval(0, 1),
				interval(0, 1).getEvaluate().difference(empty()));

		assertTrue(interval(0, 1).getEvaluate().difference(whole()).isEmpty());

		assertTrue(interval(0, POSITIVE_INFINITY)
				.getEvaluate().difference(interval(0, POSITIVE_INFINITY)).isEmpty());
		assertTrue(interval(NEGATIVE_INFINITY, 0)
				.getEvaluate().difference(interval(NEGATIVE_INFINITY, 0)).isEmpty());
		assertTrue(interval(NEGATIVE_INFINITY, 0)
				.getEvaluate().difference(whole()).isEmpty());
		assertTrue(whole().getEvaluate().difference(whole()).isEmpty());

		diff = interval(3, nextAfter(5, NEGATIVE_INFINITY))
				.getEvaluate().difference(interval(4, 6));
		assertTrue(diff.getLow() == 3 && diff.getHigh() < 4);

		assertEquals(interval(5, 6),
				interval(4, 6)
						.getEvaluate().difference(interval(3, nextAfter(5, NEGATIVE_INFINITY))));
	}

	@Test(expected = IntervalsDifferenceException.class)
	public void testDifferenceException() throws IntervalsDifferenceException {
		interval(1, 4).getEvaluate().difference(interval(2, 3));
	}

	@Test
	public void testAbs() {
		assertEquals(interval(0, 1), interval(-1, 1).getEvaluate().abs());
		assertEquals(interval(2, 3), interval(-3, -2).getEvaluate().abs());
		assertEquals(interval(2, 3), interval(2, 3).getEvaluate().abs());
		assertEquals(undefined(), undefined().getEvaluate().abs());
	}

	@Test
	public void testMax() {
		assertEquals(interval(5, 7), Interval.max(interval(-1, 1),
				interval(5, 7)));
		assertEquals(interval(-1, 1),
				Interval.max(empty(), interval(-1, 1)));
		assertEquals(interval(-1, 1),
				Interval.max(interval(-1, 1), empty()));
	}

	@Test
	public void testMin() {
		assertEquals(interval(-1, 1),
				Interval.min(interval(-1, 1), interval(5, 7)));
	}
}