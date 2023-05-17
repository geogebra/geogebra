package org.geogebra.common.kernel.interval.samplers;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.junit.Test;

public class FunctionSamplerTest extends BaseUnitTest {

	@Test
	public void testExtend() {
		GeoFunction function = add("sin(x)");
		IntervalTuple range = PlotterUtils.newRange(-100, -99, 1, 1);
		FunctionSampler sampler = PlotterUtils.newSampler(function, range,
				5);
		assertEquals(6, sampler.tuples().count());
		sampler.extend(new Interval(1000, 1002));
		assertEquals(6, sampler.tuples().count());
	}
}
