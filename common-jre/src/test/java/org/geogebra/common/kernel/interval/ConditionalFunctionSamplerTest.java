package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
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
		assertEquals(tuples.count(), countPieceByValue(tuples, interval(1), 0));

	}
	@Test
	public void testSingleIfWithCompoundCondition() {
		GeoFunction function = add("a=If(-2 < x < 0, -1)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(), countPieceByValue(tuples, interval(-1), 0));
	}

	private long countPieceByValue(IntervalTupleList tuples, Interval y, int piece) {
		return tuples.stream().filter(tuple -> tuple.y().equals(y) && tuple.piece() == piece)
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
		assertEquals(17, countPieceByValue(tuples, interval(-1), 0));
		assertEquals(16, countPieceByValue(tuples, interval(1), 1));
	}

	@Test
	public void testIfList() {
		GeoFunction function = add("a=If(x < -2, 1, -2 < x < 0, 2, x > 0, 3)");
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-15, 15, -10, 10));
		IntervalTupleList tuples = sampler.result();
		assertEquals(13, countPieceByValue(tuples, interval(1), 0));
		assertEquals(1, countPieceByValue(tuples, interval(2), 1));
		assertEquals(16, countPieceByValue(tuples, interval(3), 2));
	}

	@Test
	public void testIfListEquals() {
		GeoFunction function = add("a=If(x <= -2, 1, x == 0, 2, x >= 1, 3)");
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				PlotterUtils.newRange(-20, 20, -5, 5),
				new EuclidianViewBoundsMock(-150, 150, -10, 10));
		IntervalTupleList tuples = sampler.result();
		assertEquals(135, countPieceByValue(tuples, interval(1), 0));
		assertEquals(1, countPieceByValue(tuples, interval(2), 1));
		assertEquals(142, countPieceByValue(tuples, interval(3), 2));
	}

	@Test
	public void testIfListOverLapped() {
		GeoFunction function = add("a=If(x < -3, 1, x < -4, 2, x > 0, 3)");
		IntervalTuple range = PlotterUtils.newRange(-20, 20, -5, 5);
		IntervalFunctionSampler sampler = new ConditionalFunctionSampler(function,
				range,
				new EuclidianViewBoundsMock(range, 100, 100));
		IntervalTupleList tuples = sampler.result();
		assertEquals(42, countPieceByValue(tuples, interval(1), 0));
		assertEquals(0, countPieceByValue(tuples, interval(2), 1));
		assertEquals(51, countPieceByValue(tuples, interval(3), 2));
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
}