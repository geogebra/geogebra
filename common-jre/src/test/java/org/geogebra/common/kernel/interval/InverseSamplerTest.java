package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InverseSamplerTest extends SamplerTest {

	@Test
	public void zeroXInverseShouldBeInfiniteOnly() {
		assertAll("1/(0x)", IntervalTuple::isUndefined);
	}

	private void assertAll(String description, Predicate<? super IntervalTuple> predicate) {
		IntervalTupleList samples = functionValues(description, -4, 4, -5, -5);
		assertEquals(samples.count(), samples.stream().filter(predicate).count());
	}

	@Test
	public void inverseOfzeroXInverse() {
		assertAll("1/(1/(0x))", IntervalTuple::isUndefined);
	}

	@Test
	public void zeroDividedByTanSecXShouldBeZero() {
		assertAll("0/(tan(sec(x)))", t -> t.y().isZero());
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