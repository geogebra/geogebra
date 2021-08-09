package org.geogebra.common.kernel.interval;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class IntervalEvaluateTest extends BaseUnitTest {
	@Test
	public void testEvaluateOperand() throws Exception {
		assertEquals(interval(0, 0),
				interval(0, 0).evaluate(Operation.SIN));
		assertEquals(interval(0, 1),
				interval(0, PI / 2).evaluate(Operation.SIN));
		assertEquals(interval(-1, 1),
				interval(0, 3 * PI / 2).evaluate(Operation.SIN));
		assertEquals(interval(-1, 0),
				interval(PI, 3 * PI / 2).evaluate(Operation.SIN));
		assertEquals(interval(0, 0),
				interval(-PI, -PI).evaluate(Operation.SIN));
		assertEquals(interval(1, 1),
				interval(PI / 2, PI / 2).evaluate(Operation.SIN));
		assertEquals(interval(-1, -1),
				interval(-PI / 2, -PI / 2).evaluate(Operation.SIN));
		assertEquals(interval(-1, 0),
				interval(-PI, 0).evaluate(Operation.SIN));
		assertEquals(interval(0, 1),
				interval(-2 * PI, -3 * PI / 2).evaluate(Operation.SIN));
		double p = 2 * PI;
		assertEquals(interval(0, 1), interval(-5 * p - 2 * PI,
				-5 * p - (3 * PI) / 2).evaluate(Operation.SIN));
		assertEquals(interval(0.16767801556, 0.18877817478),
				interval(-2.975460122699386, -2.955010224948875)
						.evaluate(Operation.TAN));

	}

	@Test
	public void testAdd() throws Exception {
		Interval i1 = interval(-2, 2);
		assertEquals(i1,
				interval(-1, 1).evaluate(Operation.PLUS, interval(-1, 1)));
		Interval i2 = interval(-1, POSITIVE_INFINITY);
		assertEquals(i2,
				interval(-1, POSITIVE_INFINITY)
						.evaluate(Operation.PLUS, interval(0, 1)));
	}

	@Test
	public void testMultipleAdd() throws Exception {
		assertEquals(interval(2, 3), interval(0, 0)
			.evaluate(Operation.PLUS, interval(0, 1))
			.evaluate(Operation.PLUS, interval(2, 2))
		);

	}

	@Test
	public void testAddAndSinSeparate() {
		Interval result = interval(0, PI / 2).add(interval(PI, PI));
		assertEquals(interval(PI, 3 * PI / 2), result);
		assertEquals(interval(-1, 0), result.sin());
	}

	@Test
	public void testAddAndSinChained() {
		Interval result = interval(PI, PI).add(interval(0, PI / 2))
				.sin();
		assertEquals(interval(-1, 0), result);
	}

	@Test
	public void testSqrtXInverse() {
		Interval result = interval(0, 1).multiplicativeInverse().sqrt();
		assertEquals(interval(-1, 0), result);
	}

	@Test
	public void testXOnFractionPower() {
		Interval result = interval(0, 1).pow(IntervalConstants.one().negative().divide(
				new Interval(3)));
		assertTrue(result.isInverted());
	}

	@Test
	public void testMultiplyInvertedWithZero() {
		Interval result = invertedInterval(-1, 1).multiply(IntervalConstants.zero());
		assertFalse(result.isInverted());
	}

	@Test
	public void testSecSecX() {
		Interval result = interval(-4.8, -4.7).sec().sec();
		assertTrue(interval(-1, 1).almostEqual(result));
	}

	@Test
	public void testSecSecXInverse() {
		Interval secInner = interval(-4.600000000000001, -4.500000000000002)
				.sec();
		Interval secOuter = secInner.sec();
		Interval result = secOuter.multiplicativeInverse();
		assertTrue(result.getLow() >= -1 && result.getHigh() <= 1);
	}

	@Test
	public void testCscTanXInverse() {
		Interval result = interval(-4.600000000000001, -4.500000000000002)
				.tan().csc().multiplicativeInverse();

		assertTrue(result.getLow() >= -1 && result.getHigh() <= 1);
	}

	private Interval uninvertedInterval(double low, double high) {
		Interval interval = new Interval(low, high);
		interval.uninvert();
		return interval;
	}

	@Test
	public void testAddAndSinEvaluate() throws Exception {
		assertEquals(interval(PI, 3 * PI / 2),
				interval(PI, PI).evaluate(Operation.PLUS, interval(0, PI / 2)));

		assertEquals(interval(1, 3),
				interval(PI, PI).evaluate(Operation.PLUS, interval(0, PI / 2))
					.evaluate(Operation.SIN)
					.evaluate(Operation.PLUS, interval(2, 3)));

	}
}
