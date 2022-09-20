package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		IntervalExpressionNode expression = convert("sin(x)");
		assertTrue(expression.getLeft() instanceof IntervalFunctionVariable);
		assertEquals(IntervalOperation.SIN, expression.getOperation());
		assertFalse(expression.hasRight());
	}

	@Test
	public void testConvertSinXPlus1() {
		IntervalNodeFunction function = converter.convert(add("sin(x)+1"));
		assertEquals(one(), function.value(pi()));
		assertEquals(new Interval(2), function.value(piHalf()));
	}

	@Test
	public void testConvertDivide() {
		IntervalNodeFunction function = converter.convert(add("x/2"));
		assertEquals(one(), function.value(interval(2)));
		assertEquals(interval(2, 4), function.value(interval(4, 8)));
	}

	@Test
	public void testConvertSinBracketXPlus1Bracket() {
		IntervalNodeFunction function = converter.convert(add("sin(x+pi+pi)"));
		assertEquals(one(), function.value(piHalf()));
		assertEquals(zero(), function.value(pi()));
	}

	@Test
	public void testConvertX() {
		IntervalNodeFunction function = converter.convert(add("x"));
		assertEquals(zero(), function.value(zero()));
		assertEquals(one(), function.value(one()));
		Interval interval = interval(-12.34, 56.78);
		assertEquals(interval, function.value(interval));
	}

	@Test
	public void testConvertInverse() {
		IntervalNodeFunction function = converter.convert(add("1/x"));
		assertEquals(one(), function.value(one()));
	}

	private IntervalExpressionNode convert(String functionString) {
		GeoFunction geoFunction = add(functionString);
		IntervalNodeFunction nodeFunction = converter.convert(geoFunction);
		IntervalExpressionNode expression = nodeFunction.getRoot();
		return expression;
	}
}
