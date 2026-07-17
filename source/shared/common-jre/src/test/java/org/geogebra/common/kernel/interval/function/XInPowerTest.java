/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.SamplerTest;
import org.geogebra.common.kernel.interval.TuplesQuery;
import org.junit.jupiter.api.Test;

class XInPowerTest extends SamplerTest {
	private final GeoFunctionConverter converter = new GeoFunctionConverter();

	@Test
	void twoPowerX() {
		IntervalTupleList tuples = functionValues("2^x", -5, 5, -5, 5);
		TuplesQuery query = new TuplesQuery(tuples);
		assertFalse(query.noDefinedTuples());
	}

	@Test
	void sinEPowerX() {
		IntervalTupleList tuples = functionValues("sin(e^x)", -5, 5, -5, 5);
		TuplesQuery query = new TuplesQuery(tuples);
		assertFalse(query.noDefinedTuples());
	}

	@Test
	void twoPowerXEvaluate() {
		IntervalNodeFunction function = createFunction("2^x");
		List<Double> values = Arrays.asList(0.0625, 0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0, 16.0);
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();
		int x = -4;
		for (Double value: values) {
			expected.add(interval(value));
			IntervalSet set = connected(x, x);
			actual.add(function.value(connectedInterval(set)));
			x++;
		}
		assertEquals(expected, actual);
	}

	@Test
	void twoPowerXEvaluate1() {
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
	void equalityTest() {
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
	void sinEquality() {
		shouldBeEqual("sin(x)");
	}
}
