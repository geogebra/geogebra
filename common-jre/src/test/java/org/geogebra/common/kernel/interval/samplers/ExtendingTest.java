package org.geogebra.common.kernel.interval.samplers;

import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class ExtendingTest extends BaseUnitTest {

	@Test
	public void extendMinLnx() {
		GeoFunction function = add("a=ln(x)");
		IntervalTuple range = PlotterUtils.newRange(0, 10.0, -15.0, 15.0);
		EuclidianViewBoundsMock evBounds = new EuclidianViewBoundsMock(range,
				1920, 1280);
		IntervalFunctionSampler sampler = new FunctionSampler(function,
				evBounds);
		sampler.update(range);
		IntervalTupleList result1 = sampler.result();
		IntervalTupleList diff1 = sampler.extendDomain(-0.05, 9.95);
		IntervalTupleList result2 = sampler.result();
		IntervalTupleList diff2 = sampler.extendDomain(0.03, 10.0);
		IntervalTupleList result3 = sampler.result();
		assertNotEquals(IntervalTupleList.emptyList(), diff2);

	}
}
