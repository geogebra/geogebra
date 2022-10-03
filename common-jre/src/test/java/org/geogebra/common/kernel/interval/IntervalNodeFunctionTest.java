package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionValue;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.junit.Test;

public class IntervalNodeFunctionTest {
	private final IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
	private IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	public void testEvalSin() {
		IntervalExpressionNode node = new IntervalExpressionNode(evaluator, functionVariable,
				IntervalOperation.SIN);
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(zero(), function.value(zero()));
		assertEquals(zero(), function.value(pi()));
		assertEquals(one(), function.value(piHalf()));
	}

	@Test
	public void testEvalConstant() {
		IntervalFunctionValue constant = newValue(one());
		IntervalExpressionNode node = new IntervalExpressionNode(evaluator, constant);
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(one(), function.value(zero()));
		assertEquals(one(), function.value(new Interval(-12.34, 5678.967)));
		assertEquals(one(), function.value(whole()));

	}

	private IntervalFunctionValue newValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}

	@Test
	public void testEvalXPlusOne() {
		IntervalExpressionNode node = new IntervalExpressionNode(evaluator, functionVariable,
				IntervalOperation.PLUS, newValue(one()));
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(one(), function.value(zero()));
		assertEquals(interval(-10.34, 56.78),
				function.value(interval(-11.34, 55.78)));
	}

	@Test
	public void testEvalXPlusOnePlusOne() {
		IntervalExpressionNode node1 = new IntervalExpressionNode(evaluator, functionVariable,
				IntervalOperation.PLUS, newValue(one()));
		IntervalExpressionNode node = new IntervalExpressionNode(evaluator, node1,
				IntervalOperation.PLUS, newValue(one()));
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(interval(2), function.value(zero()));
		assertEquals(interval(2, 3),
				function.value(interval(0, 1)));
	}
}
