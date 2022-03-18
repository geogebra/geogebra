package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class PowerEquivalenceTest extends SamplerTest {

	@Test
	public void sqrtAndPOWOneHalfShouldBeEqual() {
		shouldBeEquivalent("sqrt(x)", "(x)^(1/2)");
	}

	@Test
	public void root3OfXPOW5AndPOW5Under3ShouldBeEqual() {
		shouldBeEquivalent("nroot(x^5, 3)", "(x)^(5/3)");
	}

	@Test
	public void xInverseAndXPOWMinus1ShouldBeEqual() {
		shouldBeEquivalent("x^-1", "1/x");
	}

	@Test
	public void xInverseAndXPOWDoubleApply() {
		shouldBeEquivalent("(x^-1)^-1", "1/(1/x)");
	}

	private void shouldBeEquivalent(String description1, String description2) {
		IntervalTupleList samples = samplesOf(description1);
		samples.removeAll(samplesOf(description2));
		samples.forEach(Log::debug);
		assertEquals(0, samples.count());
	}

	private IntervalTupleList samplesOf(String description) {
		return functionValues(description, -10, 10, 10, 10);
	}
}
