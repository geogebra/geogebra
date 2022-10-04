package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
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
		assertFalse(expression.hasRight());
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
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();

		for (int i = -5; i < 5; i++) {
			expected.add(new Interval(i));
			actual.add(function.value(new Interval(i)));
		}

		assertEquals(expected, actual);
	}

	@Test
	public void testConvertAbsX() {
		IntervalNodeFunction function = convert("|x|");
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();

		for (int i = -5; i < 5; i++) {
			expected.add(new Interval(Math.abs(i)));
			actual.add(function.value(new Interval(i)));
		}

		assertEquals(expected, actual);
	}

	@Test
	public void testConvertLnX() {
		IntervalNodeFunction function = convert("ln(x)");
		List<Interval> expected = new ArrayList<>();
		List<Interval> actual = new ArrayList<>();

		for (int i = -5; i < 5; i++) {
			expected.add(i < 0 ? undefined() : new Interval(Math.log(i)));
			actual.add(function.value(new Interval(i)));
		}

		assertEquals(expected, actual);
	}

	@Test
	public void testConvertInverse() {
		IntervalNodeFunction function = convert("1/x");
		assertEquals(one(), function.value(one()));
		assertEquals(new Interval(0.5), function.value(new Interval(2)));
	}

	private IntervalNodeFunction convert(String functionString) {
		GeoFunction geoFunction = add(functionString);
		return converter.convert(geoFunction);
	}
}