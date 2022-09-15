package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionValue;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.junit.Test;

public class IntervalNodeFunctionTest {
	private final IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();

	@Test
	public void testEvalSin() {
		IntervalExpressionNode node = new IntervalExpressionNode(functionVariable,
				IntervalOperation.SIN);
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(zero(), function.value(zero()));
		assertEquals(zero(), function.value(pi()));
		assertEquals(one(), function.value(piHalf()));
	}

	@Test
	public void testEvalConstant() {
		IntervalFunctionValue constant = new IntervalFunctionValue(one());
		IntervalExpressionNode node = new IntervalExpressionNode(constant);
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(one(), function.value(zero()));
		assertEquals(one(), function.value(new Interval(-12.34, 5678.967)));
		assertEquals(one(), function.value(whole()));

	}
}
