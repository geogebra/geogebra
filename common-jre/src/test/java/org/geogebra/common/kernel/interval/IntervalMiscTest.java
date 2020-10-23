package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.apache.commons.math3.util.FastMath.nextAfter;
import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.shouldEqual;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalMiscTest {

	@Test
	public void testExp() {
		shouldEqual(interval(0.36787944117, 2.71828182846), interval(-1, 1).exp());
		shouldEqual(interval(0.04978706836, 20.0855369232), interval(-3, 3).exp());
	}

	@Test
	public void testLog() {
		shouldEqual(interval(0, 0), interval(1, 1).log());
		shouldEqual(interval(0, 3), interval(1, Math.exp(3)).log());
	}

	@Test
	public void testLog10() {
		shouldEqual(interval(0, 0), interval(1, 1).log10());
		shouldEqual(interval(0, 1), interval(1, 10).log10());
		shouldEqual(interval(0, 2), interval(1, 100).log10());
	}

	@Test
	public void testLog2() {
		shouldEqual(interval(0, 0), interval(1, 1).log2());
		shouldEqual(interval(0, 1), interval(1, 2).log2());
		shouldEqual(interval(0, 3), interval(1, 8).log2());
	}

	@Test
	public void testHull() {
		shouldEqual(interval(-1, 7),
				interval(-1, 1).hull(interval(5, 7)));
		shouldEqual(interval(-1, 1),
				interval(-1, 1).hull(new Interval(empty())));
		shouldEqual(interval(-1 , 1),
				new Interval(empty()).hull(interval(-1, 1)));
		assertTrue(empty().hull(empty()).isEmpty());
	}

	@Test
	public void testIntersection() {
		assertTrue(interval(-1, 1).intersection(interval(5, 7)).isEmpty());
		assertTrue(interval(-1, 1).intersection(empty()).isEmpty());
		shouldEqual(interval(0, 1),
				interval(-1, 1).intersection(interval(0, 7)));
	}

	@Test
	public void testUnion() throws IntervalsNotOverlapException {
		shouldEqual(interval(1, 4),
				interval(1, 3).union(interval(2, 4)));
	}

	@Test(expected = IntervalsNotOverlapException.class)
	public void testUnionWithException() throws IntervalsNotOverlapException {
		interval(1, 2).union(interval(3, 4));
	}

	@Test
	public void testDifference() throws IntervalsDifferenceException {
		shouldEqual(interval(3, 4),
				interval(3, 5).difference(interval(4, 6)));

		shouldEqual(interval(5, 6),
				interval(4, 6).difference(interval(3, 5)));

		shouldEqual(interval(4, 6),
				interval(4, 6).difference(interval(8, 9)));

		Interval diff = interval(0, 3).difference(interval(0, 1));
		assertTrue(diff.getLow() > 1 && diff.getHigh() == 3);

		diff = interval(0, 3).difference(interval(1, 3));
		assertTrue(diff.getLow() == 0 && diff.getHigh() < 1);

		assertTrue(interval(0, 3).difference(interval(0, 3))
				.isEmpty());

		shouldEqual(interval(0, 1),
				interval(0, 1).difference(empty()));

		assertTrue(interval(0, 1).difference(whole()).isEmpty());

		assertTrue(interval(0, POSITIVE_INFINITY)
			.difference(interval(0, POSITIVE_INFINITY)).isEmpty());
		assertTrue(interval(NEGATIVE_INFINITY, 0)
			.difference(interval(NEGATIVE_INFINITY, 0)).isEmpty());
		assertTrue(interval(NEGATIVE_INFINITY, 0)
			.difference(whole()).isEmpty());
		assertTrue(whole().difference(whole()).isEmpty());

		diff = interval(3, nextAfter(5, NEGATIVE_INFINITY))
				.difference(interval(4, 6));
		assertTrue(diff.getLow() == 3 && diff.getHigh() < 4);

		shouldEqual(interval(5, 6),
				interval(4, 6)
						.difference(interval(3, nextAfter(5, NEGATIVE_INFINITY))));
	}

	@Test(expected = IntervalsDifferenceException.class)
	public void testDifferenceException() throws IntervalsDifferenceException {
		interval(1, 4).difference(interval(2, 3));
	}

	@Test
	public void testAbs() {
		shouldEqual(interval(0, 1), interval(-1, 1).abs());
		shouldEqual(interval(2, 3), interval(-3, -2).abs());
		shouldEqual(interval(2, 3), interval(2, 3).abs());
	}

	@Test
	public void testMax() {
		shouldEqual(interval(5, 7), Interval.max(interval(-1, 1),
				interval(5, 7)));
		shouldEqual(interval(-1, 1),
				Interval.max(empty(), interval(-1, 1)));
		shouldEqual(interval(-1, 1),
				Interval.max(interval(-1, 1), empty()));
	}

	@Test
	public void testMin() {
		shouldEqual(interval(-1, 1),
				Interval.min(interval(-1, 1), interval(5, 7)));
	}
}
