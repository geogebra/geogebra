package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertTrue;

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
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		allEquals(-1, tuples);
	}
	@Test
	public void testSingleIfWithCompoundCondition() {
		GeoFunction function = add("a=If(-2 < x < 0, -1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		allEquals(-1, tuples);
	}

	static void allEquals(int singleton, IntervalTupleList tuples) {
		int count = tuples.count();
		long filteredCount = tuples.stream().filter(tuple -> tuple.y().almostEqual(
				interval(singleton))).count();
		assertTrue("filtered: " + filteredCount + " all: " + count,
				count > 0 && count == filteredCount);
	}

	@Test
	public void testIfElse() {
		GeoFunction function = add("a=If(x < 0, -1, 1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		sampler.update(range);
		List<IntervalTupleList> results = sampler.results();
		allEquals(-1, results.get(0));
		allEquals(1, results.get(1));
	}

	@Test
	public void testIfList() {
		GeoFunction function = add("a=If(x < -2, 1, -2 < x < 0, 2, x > 0, 3)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		List<IntervalTupleList> results = sampler.results();
		allEquals(1, results.get(0));
		allEquals(2, results.get(1));
		allEquals(3, results.get(2));
	}

	@Test
	public void testIfListEquals() {
		GeoFunction function = add("a=If(x <= -2, 1, x == 0, 2, x >= 1, 3)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList result1 = sampler.evaluateOn(-10, -2);
		allEquals(1, result1);
	}

	@Test
	public void testIfListOverLapped() {
		GeoFunction function = add("a=If(x < -3, 1, x < -4, 2, x > 0, 3)");
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		allEquals(1, sampler.evaluateOn(-4, -3));
	}

	@Test
	public void evaluateIfElseOnSamplerEdge() {
		GeoFunction function = add("a=If(x < 0, -1, 1)");
		IntervalTuple range = PlotterUtils.newRange(-2, 2, -5, 5);
		IntervalFunctionSampler sampler = new IfFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-2, 2, -10, 10));
		sampler.update(range);
		IntervalTupleList diffTuples = sampler.evaluateOn(interval(-1, 1));
		allEquals(1, diffTuples);
	}
}