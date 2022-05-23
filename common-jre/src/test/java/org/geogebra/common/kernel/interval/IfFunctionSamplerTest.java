package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IfFunctionSamplerTest extends BaseUnitTest {
	@Test
	public void testSingleIf() {
		GeoFunction function = add("a=If(x < 0, -1)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		allEquals(-1, tuples);
	}

	private void allEquals(int singleton, IntervalTupleList tuples) {
		assertEquals(0, tuples.stream().filter(tuple -> tuple.y().almostEqual(
				interval(singleton))).count());
	}

	@Test
	public void testIfElse() {
		GeoFunction function = add("a=If(x < 0, -1, 1)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		List<IntervalTupleList> results = sampler.results();
		allEquals(-1, results.get(0));
		allEquals(1, results.get(1));
	}

	@Test
	public void testIfList() {
		GeoFunction function = add("a=If(x < -2, 1, 2 < x < 0, 2, x > 0, 3)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		List<IntervalTupleList> results = sampler.results();
		allEquals(1, results.get(0));
		allEquals(2, results.get(1));
		allEquals(3, results.get(2));
	}
}