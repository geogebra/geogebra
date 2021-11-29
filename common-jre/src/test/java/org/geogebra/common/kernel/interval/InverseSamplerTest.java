package org.geogebra.common.kernel.interval;

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
}