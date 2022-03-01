package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PowerEquivalenceTest extends SamplerTest {

	@Test
	public void sqrtAndPowerOnOneHalf() {
		shouldBeEquivalent("sqrt(x)", "(x)^(1/2)");
	}

	@Test
	public void sqrtAndPowerOn5Per3() {
		shouldBeEquivalent("nroot(x^5, 3)", "(x)^(5/3)");
	}

	private void shouldBeEquivalent(String description1, String description2) {
		IntervalTupleList samples1 = samplesOf(description1);
		IntervalTupleList samples2 = samplesOf(description2);
		for (int i = 0; i < samples1.count(); i++) {
			IntervalTuple tuple1 = samples1.get(i);
			IntervalTuple tuple2 = samples2.get(i);
			assertTrue(tuple1 + " != " + tuple2,
					isSimilar(tuple1, tuple2));
		}
	}

	private boolean isSimilar(IntervalTuple tuple, IntervalTuple tuple1) {
		Interval y = tuple.y();
		Interval y1 = tuple1.y();
		return tuple.x().equals(tuple1.x())
			&& (y.equals(y1)) || (Math.abs(y.getLow() - y1.getLow()) < 1E-5
				&& Math.abs(y.getHigh() - y1.getHigh()) < 1E-5);
	}

	private IntervalTupleList samplesOf(String description) {
		return functionValues(description, -10, 10, 10, 10);
	}
}
