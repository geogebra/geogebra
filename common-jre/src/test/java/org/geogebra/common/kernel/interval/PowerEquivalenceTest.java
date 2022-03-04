package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.util.DoubleUtil;
import org.junit.Test;

public class PowerEquivalenceTest extends SamplerTest {

	@Test
	public void sqrtAndPowerOnOneHalf() {
		shouldBeEquivalent("sqrt(x)", "(x)^(1/2)", 1E-9);
	}

	@Test
	public void sqrtAndPowerOn5Per3() {
		shouldBeEquivalent("nroot(x^5, 3)", "(x)^(5/3)", 1E-9);
	}

	@Test
	public void nroot9squared() {
		shouldBeEquivalent("x^-1", "1/x", 1E-1);
	}


	private void shouldBeEquivalent(String description1, String description2, double eps) {
		IntervalTupleList samples1 = samplesOf(description1);
		IntervalTupleList samples2 = samplesOf(description2);
		for (int i = 0; i < samples1.count(); i++) {
			IntervalTuple tuple1 = samples1.get(i);
			IntervalTuple tuple2 = samples2.get(i);
			assertTrue(tuple1 + " != " + tuple2,
					isSimilar(tuple1, tuple2, eps));
		}
	}

	private boolean isSimilar(IntervalTuple tuple, IntervalTuple tuple1, double eps) {
		Interval y = tuple.y();
		Interval y1 = tuple1.y();
		return tuple.x().equals(tuple1.x())
			&& DoubleUtil.isEqual(y.getLow(), y1.getLow(), eps)
			&& DoubleUtil.isEqual(y.getHigh(), y1.getHigh(), eps);
	}

	private IntervalTupleList samplesOf(String description) {
		return functionValues(description, -10, 10, 10, 10);
	}
}
