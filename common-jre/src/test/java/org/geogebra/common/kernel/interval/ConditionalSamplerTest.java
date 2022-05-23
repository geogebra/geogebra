package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class ConditionalSamplerTest extends BaseUnitTest {
	@Test
	public void testSignum() {
		GeoFunction function = add("a=If(x < 0, -1,  1)");
		MyNumberPair pair = (MyNumberPair) Objects.requireNonNull(
				function.getFunctionExpression()).getLeft();
		DiscreteSpace discreteSpace = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler = new ConditionalSampler(function, pair, discreteSpace);
		if (sampler.isAccepted(interval(-5, -1))) {
			sampler.evaluate();
		}
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(),
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(-1))).count());

	}
}