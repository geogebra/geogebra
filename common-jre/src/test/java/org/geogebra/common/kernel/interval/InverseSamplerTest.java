package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InverseSamplerTest extends SamplerTest {

	@Test
	public void zeroXInverseShouldBeInfiniteOnly() {
		IntervalTupleList samples = functionValues("1/(0x)", -4, 4, -5, -5);
		for (IntervalTuple tuple: samples) {
			Interval y = tuple.y();
			assertTrue(tuple.y() + " is not +/- infinite singleton",  y.isInfiniteSingleton());
		}
	}

	@Test
	public void inverseOfzeroXInverse() {
		IntervalTupleList samples = functionValues("1/(1/(0x))", -4, 4, -5, -5);
		for (IntervalTuple tuple: samples) {
			Interval y = tuple.y();
			assertTrue(tuple.y() + " is not [0]",  y.isZero());
		}
	}

	@Test
	public void testTanIsDivergent() {
		IntervalTupleList samples = functionValues("tan(x)", -4, 4, -5, -5);
		assertTrue(samples.isDivergentAt(30));
	}

	@Test
	public void testNonDivergent() {
		IntervalTupleList samples = functionValues("(x^(1/9))^-2", -4, 4, -5, -5);
		assertFalse(samples.isDivergentAt(49));
	}
}