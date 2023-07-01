package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.junit.Test;

public class GeoFunctionConverterTest extends BaseUnitTest {
	private GeoFunctionConverter converter = new GeoFunctionConverter();

	@Test
	public void testConvertSinX() {
		IntervalExpressionNode expression = convert("sin(x)").getRoot();
		assertTrue(expression.getLeft() instanceof IntervalFunctionVariable);
		assertEquals(IntervalOperation.SIN, expression.getOperation());
	}

	@Test
	public void testConvertSinXPlus1() {
		IntervalNodeFunction function = convert("sin(x)+1");
		assertEquals(one(), function.value(pi()));
		assertEquals(new Interval(2), function.value(piHalf()));
	}

	@Test
	public void testConvertDivide() {
		IntervalNodeFunction function = convert("x/2");
		assertEquals(one(), function.value(interval(2)));
		assertEquals(interval(2, 4), function.value(interval(4, 8)));
	}

	@Test
	public void testConvertSinBracketXPlus1Bracket() {
		IntervalNodeFunction function = convert("sin(x+pi+pi)");
		assertEquals(one(), function.value(piHalf()));
		assertEquals(zero(), function.value(pi()));
	}

	@Test
	public void testConvertX() {
		IntervalNodeFunction function = convert("x");
		assertEquivalent(Interval::new, function, -5, 5);
	}

	@Test
	public void testConvertAbsX() {
		IntervalNodeFunction function = convert("|x|");
		assertEquivalent(x -> new Interval(Math.abs(x)), function, -5, 5);
	}

	@Test
	public void testConvertLnX() {
		IntervalNodeFunction function = convert("ln(x)");
		assertEquivalent(x -> x < 0 ? undefined() : new Interval(Math.log(x)),
				function, -5, 5);
	}

	@Test
	public void testConvertInverse() {
		IntervalNodeFunction function = convert("1/x");
		assertEquals(one(), function.value(one()));
		assertEquals(new Interval(0.5), function.value(new Interval(2)));
	}

	@Test
	public void testConvertTanSquaredXInverse() {
		IntervalNodeFunction function = convert("1/(tan^(2)(x))");
		assertEquals(whole(), function.value(around(Math.PI / 2, 1E-7)));
	}

	@Test
	public void testUndefinedInFunction() {
		add("b=2");
		addAvInput("SetValue(b, ?)");
		IntervalNodeFunction function = convert("x^b");
		assertEquals(IntervalConstants.undefined(), function.value(new Interval(2)));
	}

	@Test
	public void testDependentFunctions() {
		add("f(x)=x");
		IntervalNodeFunction g = convert("f(x) + 1");
		assertEquivalent(x -> new Interval(x + 1), g, 0, 10);
	}

	@Test
	public void testFitFunction() {
		IntervalNodeFunction g = convert("FitPoly({(1,3),(2,5)},1)");
		assertEquivalent(x -> new Interval(2 * x + 1), g, 0, 10);
	}

	@Test
	public void testFunctionOfConstant() {
		IntervalNodeFunction g = convert("x * ld(64)");
		assertEquivalent(x -> new Interval(6 * x), g, 0, 10);
	}

	private void assertEquivalent(
			Function<Double, Interval> exp, IntervalNodeFunction g, int from, int to) {
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();
		for (int i = from; i < to; i++) {
			expected.add(exp.apply((double) i));
			actual.add(g.value(new Interval(i)));
		}
		assertEquals(expected, actual);
	}

	private IntervalNodeFunction convert(String functionString) {
		GeoFunction geoFunction = add(functionString);
		return converter.convert(geoFunction);
	}

	@Test
	public void testNormal() {
		GeoFunction f = add("Normal(1, 2, x, false)");
		IntervalNodeFunction g = converter.convert(f);
		assertEquivalent(x -> new Interval(f.value(x)), g, 0, 10);
	}

	@Test
	public void testDivBelowZeroThreshold() {
		GeoFunction f = add("((1*10^(-13))/(1*10^(-13)))x");
		IntervalNodeFunction g = converter.convert(f);
		assertEquals(one(), g.value(one()));
	}

	@Test
	public void testExpShouldBeNoUndefined() {
		GeoFunction f = add("1-exp(-5x)");
		IntervalNodeFunction g = converter.convert(f);
		assertEquals(one(), g.value(interval(7.484375, 7.5)));
	}
}