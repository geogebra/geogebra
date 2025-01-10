package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.SamplerTest;
import org.geogebra.common.kernel.interval.TuplesQuery;
import org.junit.Test;

public class XInPowerTest extends SamplerTest {
	private final GeoFunctionConverter converter = new GeoFunctionConverter();

	@Test
	public void twoPowerX() {
		IntervalTupleList tuples = functionValues("2^x", -5, 5, -5, 5);
		TuplesQuery query = new TuplesQuery(tuples);
		assertFalse(query.noDefinedTuples());
	}

	@Test
	public void sinEPowerX() {
		IntervalTupleList tuples = functionValues("sin(e^x)", -5, 5, -5, 5);
		TuplesQuery query = new TuplesQuery(tuples);
		assertFalse(query.noDefinedTuples());
	}

	@Test
	public void twoPowerXEvaluate() {
		IntervalNodeFunction function = createFunction("2^x");
		List<Double> values = Arrays.asList(0.0625, 0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0, 16.0);
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();
		int x = -4;
		for (Double value: values) {
			expected.add(interval(value));
			actual.add(function.value(around(x)));
			x++;
		}
		assertEquals(expected, actual);
	}

	@Test
	public void twoPowerXEvaluate1() {
		IntervalNodeFunction function = createFunction("2^x");
		assertEquals(interval(0.0625), function.value(interval(-4)));
		assertEquals(interval(2), function.value(interval(1)));
		assertEquals(interval(4), function.value(interval(2)));
		assertEquals(interval(8), function.value(interval(3)));
	}

	private IntervalNodeFunction createFunction(String definition) {
		return converter.convert(add(definition));
	}

	@Test
	public void equalityTest() {
		shouldBeEqual("x");
		shouldBeEqual("2x");
	}

	private void shouldBeEqual(String definition) {
		GeoFunction geoFunction = add(definition);
		IntervalNodeFunction nodeFunction = converter.convert(geoFunction);
		for (double x = -10; x < 10; x += 1E-4) {
			assertEquals(interval(geoFunction.evaluate(x, 0)),
					nodeFunction.value(interval(x)));
		}
	}

	@Test
	public void sinEquality() {
		shouldBeEqual("sin(x)");
	}
}
