package org.geogebra.common.kernel.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalFunction.isSupported;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.shouldEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IntervalFunctionTest extends BaseUnitTest {

	@Test
	public void evaluateSin() throws Exception {
		GeoFunction geo = add("sin(x)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldEqual(interval(0, 1), function.evaluate(interval(0, PI / 2)));
	}

	@Test
	public void evaluate2Sin() throws Exception {
		GeoFunction geo = add("2 * sin(x)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldEqual(interval(-2, 2),
				function.evaluate(interval(-PI, PI)));
	}

	@Test
	public void evaluateSin2x() throws Exception {
		GeoFunction geo = add("sin(2x)");
		geo.value(2);
		IntervalFunction function = new IntervalFunction(geo);
		shouldEqual(interval(-1, 1),
				function.evaluate(interval(-PI, PI)));
	}

	@Test
	public void testSupportOneVariableOnly() {
		assertFalse(isSupported(add("x + x")));
		assertFalse(isSupported(add("x^2 + x")));
		assertFalse(isSupported(add("abs(x)/x")));
		assertFalse(isSupported(add("(1/x)sin(x)")));
		assertFalse(isSupported(add("sin(x^4) + x")));
		assertFalse(isSupported(add("tan(x)/x")));
		assertFalse(isSupported(add("x+3x")));
	}

	@Test
	public void testSupportedOperands() {
		assertTrue(isSupported(add("x + 1")));
		assertTrue(isSupported(add("x - 1")));
		assertTrue(isSupported(add("x * 5")));
		assertTrue(isSupported(add("x / 5")));
		assertTrue(isSupported(add("x^3")));
		assertTrue(isSupported(add("nroot(x, 4)")));
		assertTrue(isSupported(add("sin(x)")));
		assertTrue(isSupported(add("cos(x)")));
		assertTrue(isSupported(add("sqrt(x)")));
		assertTrue(isSupported(add("tan(x)")));
		assertTrue(isSupported(add("exp(x)")));
		assertTrue(isSupported(add("log(x)")));
		assertTrue(isSupported(add("arccos(x)")));
		assertTrue(isSupported(add("arcsin(x)")));
		assertTrue(isSupported(add("arctan(x)")));
		assertTrue(isSupported(add("abs(x)")));
		assertTrue(isSupported(add("cosh(x)")));
		assertTrue(isSupported(add("sinh(x)")));
		assertTrue(isSupported(add("tanh(x)")));
		assertTrue(isSupported(add("acosh(x)")));
		assertTrue(isSupported(add("log10(x)")));
		assertTrue(isSupported(add("log(x)")));
	}

	@Test
	public void testUnsupportedOperands() {
		assertFalse(isSupported(add("x!")));
		assertFalse(isSupported(add("gamma(x)")));
	}
}