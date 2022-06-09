package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class XInPowerTest extends SamplerTest {

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
		IntervalFunction function = new IntervalFunction(add("2^x"));
		assertEquals(interval(0.5), function.evaluate(around(-1)));
		assertEquals(IntervalConstants.one(), function.evaluate(around(0)));
		assertEquals(interval(2), function.evaluate(around(1)));
	}
}
