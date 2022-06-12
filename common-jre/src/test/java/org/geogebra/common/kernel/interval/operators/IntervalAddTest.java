package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.geogebra.common.kernel.interval.operators.IntervalDivide.next;
import static org.geogebra.common.kernel.interval.operators.IntervalDivide.prev;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTest;
import org.junit.Assert;
import org.junit.Test;

public class IntervalAddTest {

	@Test
	public void addNegativeInfinityWithFiniteToNegativeInfinityWithFinite() {
		Assert.assertEquals(negativeInf(4.68),
				negativeInf(1.23).add(negativeInf(3.45)));
		Assert.assertEquals(negativeInf(0),
				negativeInf(-1.23).add(negativeInf(1.23)));
	}

	private Interval negativeInf(double v) {
		return IntervalTest.interval(Double.NEGATIVE_INFINITY, v);
	}

	@Test
	public void addNegativeInfinityWithFiniteToFiniteInterval() {
		assertEquals(negativeInf(45.67 + 56.78),
				negativeInf(45.67).add(IntervalTest.interval(12.34, 56.78)));
		assertEquals(negativeInf(91.34),
				negativeInf(45.67).add(IntervalTest.interval(12.34, 45.67)));
	}

	@Test
	public void addNegativeInfinityWithFiniteToFiniteOpenToPositiveInfinity() {
		assertEquals(whole(),
				negativeInf(45.67).add(IntervalTest.interval(12.34, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void addWholeToAnything() {
		assertEquals(whole(), negativeInf(34.56).add(whole()));
		Assert.assertEquals(whole(), IntervalTest.interval(-12.34, 1E32).add(whole()));
		Assert.assertEquals(whole(), IntervalTest.interval(-12.34, Double.POSITIVE_INFINITY).add(whole()));
		assertEquals(whole(), whole().add(whole()));
	}

	@Test
	public void addFiniteToNegativeInfinityAndFinite() {
		Assert.assertEquals(negativeInf(1E-2 + 1234.567),
				IntervalTest.interval(0, 1E-2).add(negativeInf(1234.567)));
	}

	@Test
	public void addFiniteToFinite() {
		addFiniteToFinite(12.34, 56.78, 78.97, 100.12);
		addFiniteToFinite(4, -56.78, 78.97, 100.12);
		addFiniteToFinite(4, -56.78, -1E-3, 1E-2);
	}

	private void addFiniteToFinite(double a1, double a2, double b1, double b2) {
		Assert.assertEquals(IntervalTest.interval(prev(a1 + b1), next(a2 + b2)),
				IntervalTest.interval(a1, a2).add(IntervalTest.interval(b1, b2)));
	}

	@Test
	public void addFiniteToFiniteOpenToPositiveInfinity() {
		addFiniteToFiniteOpenToPositiveInfinity(12.34, 56.78, 2.1);
		addFiniteToFiniteOpenToPositiveInfinity(-12.34, 56.78, 0);
		addFiniteToFiniteOpenToPositiveInfinity(-56.34, -12.78, 1E234);
	}

	private void addFiniteToFiniteOpenToPositiveInfinity(double a1, double a2, double b1) {
		Assert.assertEquals(IntervalTest.interval(prev(a1 + b1), Double.POSITIVE_INFINITY),
				IntervalTest.interval(a1, a2).add(IntervalTest.interval(b1, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void addToUndefinedShouldBeUndefined() {
		assertEquals(undefined(), undefined().add(IntervalTest.interval(1E123, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void testAddToInverted() {
		Interval actual = invertedInterval(-3.45, 78.97)
				.add(IntervalTest.interval(12.34, 56.78));
		Assert.assertEquals(IntervalTest.interval(Double.NEGATIVE_INFINITY, 8.89), actual.extractLow());
		Assert.assertEquals(IntervalTest.interval(135.75, Double.POSITIVE_INFINITY), actual.extractHigh());
	}

	@Test
	public void testAddInvertedTo() {
		Interval actual = IntervalTest.interval(12.34, 56.78)
				.add(invertedInterval(-3.45, 78.97));
		Assert.assertEquals(IntervalTest.interval(Double.NEGATIVE_INFINITY, 8.89), actual.extractLow());
		Assert.assertEquals(IntervalTest.interval(135.75, Double.POSITIVE_INFINITY), actual.extractHigh());
	}

}
