package org.geogebra.common.kernel.interval.samplers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class FunctionSamplerTest extends BaseUnitTest {

	@Test
	public void testExtend() {
		GeoFunction function = add("sin(x)");
		IntervalTuple range = PlotterUtils.newRange(-100, -99, 1, 1);
		FunctionSampler sampler = PlotterUtils.newSampler(function, range,
				5, new EuclidianViewBoundsImp(getApp().getActiveEuclidianView()));
		assertEquals(6, sampler.tuples().count());

		sampler.extend(new Interval(1000, 1005));
		assertEquals(Arrays.asList(999, 1000, 1001, 1002, 1003, 1004),
				getX(sampler.tuples()));

		sampler.extend(new Interval(1002, 1007));
		assertEquals(Arrays.asList(999, 1000, 1001, 1002, 1003, 1004, 1005, 1006),
				getX(sampler.tuples()));

		sampler.extend(new Interval(1001, 1016));
		assertEquals(Arrays.asList(999, 1002, 1005, 1008, 1011, 1014),
				getX(sampler.tuples()));

	}

	private List<Integer> getX(IntervalTupleList tuples) {
		return tuples.stream().map(t -> (int) (t.x().getLow())).collect(Collectors.toList());
	}
}
