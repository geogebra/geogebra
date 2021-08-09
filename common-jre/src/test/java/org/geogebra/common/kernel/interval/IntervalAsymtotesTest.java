package org.geogebra.common.kernel.interval;

import static java.lang.Math.PI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IntervalAsymtotesTest extends BaseUnitTest {

	@Test
	public void tanX() {
		IntervalTupleList result = functionValues("tan(x)", -PI / 4, 3 * PI / 4, -10, 10);
		assertTrue(result.get(74).isAsymptote());
	}

	@Test
	public void tanXHighResolution() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"tan(x)", -20, 20, -10, 10, 1920);
		assertTrue(result.get(74).isAsymptote());
	}

	@Test
	public void xInverseHighResolution() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"1/x", -10, 10, -30, 30, 100);
		assertTrue(result.get(50).y().isInverted());
	}

	@Test
	public void xInverseMonotonity() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"1/x", -10, 10, -30, 30, 100);
		assertFalse(result.isAscendingBefore(2));
		assertFalse(result.isAscendingBefore(50));
	}

	@Test
	public void xInverseInverse() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"1/(1/x)", -10, 10, -30, 30, 100);
		assertTrue(result.getInvertedTuples().isEmpty());
	}

	@Test
	public void minusXInverseMonotonity() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"-1/x", -10, 10, -30, 30, 100);
		assertTrue(result.isAscendingBefore(2));
		assertTrue(result.isAscendingBefore(50));
	}

	@Test
	public void sinXInverseShouldBeInverted() {
		IntervalTupleList result = functionValues("1/sin(x)",
				-PI, PI, -10, 10);
		assertTrue(result.get(0).y().isInverted());
		assertTrue(result.get(99).y().isInverted());
	}

	@Test
	public void sinXInverseShouldBeInvertedOn4Pi() {
		IntervalTupleList result = functionValues("1/sin(x)",
				-2 * PI, 2 * PI, -10, 10);
		assertTrue(result.get(0).y().isInverted());
		assertTrue(result.get(24).y().isInverted());
		assertTrue(result.get(74).y().isInverted());
		assertTrue(result.get(99).y().isInverted());
	}

	@Test
	public void cotX() {
		IntervalTupleList result = functionValues("cot(x)", 0, PI, -9, 9);
		assertTrue(result.get(0).y().isUndefined()
				&& result.get(100).y().isUndefined());
	}

	@Test
	public void secCscXInverseCutOff() {
		IntervalTupleList result = functionValues("1/sec(csc(x))", -2.9, 2.9, -8, 8);
		List<Integer> cutOffIndexes = Arrays.asList(7, 38, 61, 92);
		for (int index: cutOffIndexes) {
			assertFalse(result.get(index).y().isWhole());
		}
	}

	@Test
	public void sqrtXInverse() {
		GeoFunction function = add("sqrt(1/x)");
		IntervalTuple range = PlotterUtils.newRange(0, 10, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(0).isInverted());
	}

	@Test
	public void minusSqrtXInverse() {
		GeoFunction function = add("-sqrt(1/x)");
		IntervalTuple range = PlotterUtils.newRange(0, 10, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(0).isInverted());
	}

	@Test
	public void minusSqrtXMinusInverse() {
		GeoFunction function = add("-sqrt(1/-x)");
		IntervalTuple range = PlotterUtils.newRange(-10, 0, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(100).isInverted());
	}

	@Test
	public void squareRootOfTanInverse() {
		GeoFunction function = add("1/sqrt(tan(x))");
		IntervalTuple range = PlotterUtils.newRange(0, 5, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		for (IntervalTuple tuple: result) {
			assertFalse(tuple.y().isHalfNegativeInfinity());
		}

	}

	@Test
	public void tanSqrtCosX() {
		IntervalTupleList tuples = functionValues("tan(sqrt(cos(x)))", -2, 2, 10, 10);
		assertTrue(tuples.valueAt(0).isEmpty());
		assertTrue(tuples.valueAt(1).hasZero());
		assertEquals(new Interval(1.5560382788311629, 1.5574077246549032),
				tuples.valueAt(41));
		assertTrue(tuples.valueAt(80).hasZero());
		assertTrue(tuples.valueAt(81).isEmpty());
	}

	@Test
	public void squareRootOfSinX() {
		IntervalTupleList tuples = functionValues("sqrt(sin(x))", 0, 3 * PI, -8, 8);
		assertTrue(tuples.valueAt(33).hasZero());
		assertTrue(tuples.valueAt(34).isEmpty());
		assertTrue(tuples.valueAt(35).hasZero());
		assertTrue(tuples.valueAt(68).hasZero());
	}

	@Test
	public void absOfXInverse() {
		IntervalTupleList tuples = functionValues("abs(1/x)", -1, 1, -8, 8);
		assertTrue(tuples.valueAt(49).isUndefined());
	}

	@Test
	public void squareRootOfTanX() {
		IntervalTupleList tuples = functionValues("sqrt(tan(x))", 0, 3 * PI, -8, 8);
		assertEquals(tuples.valueAt(54).getLow(), tuples.valueAt(53).getHigh(), 0);
	}

	@Test
	public void inverseSquareRootOfTanX() {
		IntervalTupleList tuples = functionValues("1/sqrt(tan(x))", 0, 2 * PI, -8, 8);
		assertEquals(tuples.valueAt(54).getLow(), tuples.valueAt(53).getHigh(), 0);
	}

	@Test
	public void inspectFunctions() {
		IntervalTupleList tuples = functionValuesWithSampleCount(
				"1/(sec(sec(x)))",
				-15, 15, -10, 10, 1280);
		valuesShouldBeBetween(tuples, -1, 1);
	}

	@Test
	public void cscTanXInverse() {
		IntervalTupleList tuples = functionValuesWithSampleCount(
				"1/(csc(tan(x)))",
				-15, 15, -10, 10, 1280);
		valuesShouldBeBetween(tuples, -1, 1);
	}

	@Test
	public void cotLnCotX() {
		IntervalTupleList tuples = functionValues(
				"cot(ln(cot(x)))",
				0, 2, -10, 10);
		valuesShouldBeBetween(tuples, -1, 1);
	}

	@Test
	public void sqrtSecCotX() {
		IntervalTupleList tuples = functionValues(
				"sqrt(sec(cot(x)))",
				0, 2, -10, 10);
		valuesShouldBeBetween(tuples, 0, 10);
	}

	private void valuesShouldBeBetween(IntervalTupleList tuples, double low, double high) {
		List<IntervalTuple> result =
				tuples.stream().filter(entry -> entry.y().getLow() < low - 1E-6
						|| entry.y().getHigh() > high + 1E-6).collect(Collectors.toList());
		assertEquals(Collections.emptyList(), result);
	}

	private IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax) {
		return functionValuesWithSampleCount(functionDescription,
				xmin, xmax, ymin, ymax,
				100);
	}

	private IntervalTupleList functionValuesWithSampleCount(String functionDescription,
			double xmin, double xmax, double ymin, double ymax, int sampleCount) {
		GeoFunction function = add(functionDescription);
		IntervalTuple range = PlotterUtils.newRange(xmin, xmax, ymin, ymax);
		IntervalFunctionSampler sampler = PlotterUtils.newSampler(function, range,
				sampleCount);
		return sampler.result();
	}

}