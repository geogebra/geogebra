package org.geogebra.common.kernel.interval.function.evaluation;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.junit.Test;

public class SqrtEvaluationTest extends BaseUnitTest {

	private final GeoFunctionConverter converter = new GeoFunctionConverter();

	@Test
	public void testSqrtX() {
		assertEquals(zero(), functionValue("sqrt(x)", zero()));
	}

	private Interval functionValue(String definition, Interval x) {
		IntervalNodeFunction nodeFunction = createFunction(definition);
		return nodeFunction.value(x);
	}

	private IntervalNodeFunction createFunction(String definition) {
		return converter.convert(add(definition));
	}

	@Test
	public void testNoWholeInInverseOfMinusSqrtMinusX() {
		IntervalNodeFunction function = createFunction("1/-sqrt(-x)");
		for (double t = -5; t < 1; t += 1E-4) {
			assertFalse(function.value(around(t)).isWhole());
		}
	}

	@Test
	public void testSqrtXInverseEmptyOnNegative() {
		assertEquals(undefined(), functionValue("1/sqrt(x)", interval(-2, -1)));
	}

	@Test
	public void testNegativeOfSqrtXInverse() {
		assertEquals(interval(Double.NEGATIVE_INFINITY, -100.0),
				functionValue("-(1/sqrt(x))", interval(-1E-4, 1E-4)));
	}
}
