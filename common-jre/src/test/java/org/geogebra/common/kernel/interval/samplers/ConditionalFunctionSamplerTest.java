package org.geogebra.common.kernel.interval.samplers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTest;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class ConditionalFunctionSamplerTest extends BaseUnitTest {
	@Test
	public void testSingleIf() {
		GeoFunction function = add("a=If(x < 0, 1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(), countPieceByValue(tuples, IntervalTest.interval(1), 0));

	}

	@Test
	public void testSingleIfWithCompoundCondition() {
		GeoFunction function = add("a=If(-2 < x < 0, -1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(), countPieceByValue(tuples, IntervalTest.interval(-1), 0));
	}

	private int countPieceByValue(IntervalTupleList tuples, Interval y, int piece) {
		return (int) tuples.stream().filter(tuple -> tuple.y().equals(y) && tuple.piece() == piece)
				.count();
	}

	@Test
	public void testIfElse() {
		GeoFunction function = add("a=If(x < 0, -1, 1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		pieceCountShouldBe(tuples, Arrays.asList(15, 14), Arrays.asList(-1, 1));
	}

	private void pieceCountShouldBe(IntervalTupleList tuples, List<Integer> expectedCounts,
			List<Integer> pieceYVvalues) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < pieceYVvalues.size(); i++) {
			list.add(countPieceByValue(tuples, new Interval(pieceYVvalues.get(i)), i));
		}
		assertEquals(expectedCounts, list);
	}

	@Test
	public void testIfList() {
		GeoFunction function = add("a=If(x < -2, 1, -2 < x < 0, 2, x > 0, 3)");
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		pieceCountShouldBe(tuples, Arrays.asList(13, 1, 15), Arrays.asList(1, 2, 3));
	}

	@Test
	public void testIfListEquals() {
		GeoFunction function = add("a=If(x <= -2, 1, x == 0, 2, x >= 1, 3)");
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-150, 150, -10, 10));
		IntervalTupleList tuples = sampler.result();
		pieceCountShouldBe(tuples, Arrays.asList(135, 1, 142), Arrays.asList(1, 2, 3));
	}

	@Test
	public void testIfListOverLapped() {
		GeoFunction function = add("a=If(x < -3, 1, x < -4, 2, x > 0, 3)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(range, 100, 100));
		IntervalTupleList tuples = sampler.result();
		pieceCountShouldBe(tuples, Arrays.asList(42, 0, 50), Arrays.asList(1, 2, 3));
	}

	@Test
	public void evaluateIfElseExtending() {
		GeoFunction function = add("a=If(x < 0, -1, 1)");
		IntervalTuple range = PlotterUtils.newRange(-12.4, 12.4, -15.0, 15.0);
		EuclidianViewBoundsMock evBounds = new EuclidianViewBoundsMock(range,
				1920, 1280);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				evBounds);
		sampler.update(range);
		IntervalTupleList diff = sampler.extendDomain(-12.6, 12.6);
		assertNotEquals(IntervalTupleList.emptyList(), diff);
		IntervalTupleList diffMax = sampler.extendDomain(-12.4, 12.6);
		assertNotEquals(IntervalTupleList.emptyList(), diffMax);
	}

	@Test
	public void evaluateIfExtendingFromOffscreen() {
		GeoFunction function = add("a=If(x > 0, 1)");
		IntervalTuple range = PlotterUtils.newRange(-9.0, -1.0, -15.0, 15.0);
		EuclidianViewBoundsMock evBounds = new EuclidianViewBoundsMock(range,
				1920, 1280);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				evBounds);
		sampler.update(range);
		assertEquals(IntervalTupleList.emptyList(), sampler.result());
		IntervalTupleList diff = sampler.extendDomain(-7.0, 1.0);
		assertNotEquals(IntervalTupleList.emptyList(), diff);
	}

	@Test
	public void evaluateIfListExtendingFromOffscreen() {
		GeoFunction function = add("a=If(x < 0, 1, x > 3, 4)");
		IntervalTuple range = PlotterUtils.newRange(5.0, 10.0, -15.0, 15.0);
		EuclidianViewBoundsMock evBounds = new EuclidianViewBoundsMock(range,
				1920, 1280);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				evBounds);
		sampler.update(range);
		IntervalTupleList tuples = sampler.result();
		assertEquals(1920, countPieceByValue(tuples, new Interval(4), 1));
		IntervalTupleList diff = sampler.extendDomain(-2.0, 10.0);
		assertNotEquals(IntervalTupleList.emptyList(), diff);
	}
}