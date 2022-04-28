package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalAsymptotesTest extends SamplerTest {

	@Test
	public void isConstant() {
		IntervalTupleList samples = functionValues("f(x)=7", -10, 10, -5, 5);
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		assertTrue(asymptotes.isConstant());
	}

	@Test
	public void isConstantWithWhole() {
		IntervalTupleList samples = functionValues("f(x)=0/tan(sec(x))", -10, 10, -5, 5);
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		assertTrue(asymptotes.isConstant());
	}

	@Test
	public void testProcessConstantWithWhole() {
		IntervalTupleList samples = functionValues("0/tan(sec(x))", -10, 10, -5, 5);
		assertEquals(samples.count(), zerosIn(samples));
	}

	private long zerosIn(IntervalTupleList samples) {
		return samples.stream().filter(t -> t.y().isZero()).count();
	}
}