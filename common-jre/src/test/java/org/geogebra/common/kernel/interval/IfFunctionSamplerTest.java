package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IfFunctionSamplerTest extends BaseUnitTest {
	@Test
	public void testSignum() {
		GeoFunction function = add("a=If(x < 0, -1,  1)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.evaluateOn(-5, 0);
		assertEquals(null, tuples);
	}
}