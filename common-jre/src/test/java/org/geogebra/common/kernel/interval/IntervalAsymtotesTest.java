package org.geogebra.common.kernel.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IntervalAsymtotesTest extends SamplerTest {

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
		TuplesQuery query = new TuplesQuery(result);
		assertEquals(0, query.invertedTuples().count());
	}

	@Test
	public void minusXInverseMonotonity() {
		IntervalTupleList result = functionValuesWithSampleCount(
				"-1/x", -10, 10, -30, 30, 100);
		assertTrue(result.isAscendingBefore(2));
		assertTrue(result.isAscendingBefore(50));
	}

	@Test
	public void sinXInverseShouldBeInfiniteOnPi4() {
		IntervalTupleList result = functionValues("1/sin(x)",
				-2 * PI, 2 * PI, -10, 10);
		assertEquals(interval(7.978729755559, Double.POSITIVE_INFINITY),
				result.valueAt(0));
		assertEquals(interval(7.978729755559, Double.POSITIVE_INFINITY),
				result.valueAt(24));
		assertEquals(interval(7.978729755559, Double.POSITIVE_INFINITY),
				result.valueAt(74));
	}

	@Test
	public void secCscXInverseCutOff() {
		IntervalTupleList result = functionValues("1/sec(csc(x))", -2.9, 2.9, -8, 8);
		List<Integer> cutOffIndexes = Arrays.asList(7, 38, 61, 92);
		for (int index: cutOffIndexes) {
			assertFalse(result.get(index).y().isRealWhole());
		}
	}

	@Test
	public void minusSqrtXInverse() {
		GeoFunction function = add("-sqrt(1/x)");
		IntervalTuple range = PlotterUtils.newRange(0, 10, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		assertEquals(interval(Double.NEGATIVE_INFINITY, -3.1622776601683786),
				result.get(0).y());
	}

	@Test
	public void squareRootOfTanInverse() {
		GeoFunction function = add("1/sqrt(tan(x))");
		IntervalTuple range = PlotterUtils.newRange(0, PI, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		for (IntervalTuple tuple: result) {
			assertFalse(tuple.y().isHalfNegativeInfinity());
		}
	}

	@Test
	public void absOfXInverse() {
		IntervalTupleList tuples = functionValues("abs(1/x)", -1, 1, -8, 8);
		assertEquals(empty().invert(), tuples.valueAt(49));
	}

	@Test
	public void squareRootOfTanX() {
		IntervalTupleList tuples = functionValues("sqrt(tan(x))", 0, 3 * PI, -8, 8);
		assertEquals(invertedInterval(0, 100), tuples.valueAt(16));
		assertTrue(tuples.valueAt(51).isEmpty());
	}

	@Test
	public void cscTanXInverse() {
		IntervalTupleList tuples = functionValuesWithSampleCount(
				"1/(csc(tan(x)))",
				-15, 15, -10, 10, 1280);
		valuesShouldBeBetween(tuples, -1, 1);
	}

	@Test
	public void sqrtSecCotX() {
		IntervalTupleList tuples = functionValues(
				"sqrt(sec(cot(x)))",
				0, 2, -10, 10);
		valuesShouldBeBetween(tuples, 0, 10);
	}
}