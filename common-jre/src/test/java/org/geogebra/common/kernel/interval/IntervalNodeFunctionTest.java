package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.junit.Test;

public class IntervalNodeFunctionTest {
	@Test
	public void testEvalSin() {
		IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
		IntervalExpressionNode node = new IntervalExpressionNode(functionVariable,
				IntervalOperation.SIN);
		IntervalNodeFunction function =
				new IntervalNodeFunction(node, functionVariable);
		assertEquals(zero(), function.value(zero()));
		assertEquals(zero(), function.value(pi()));
		assertEquals(one(), function.value(piHalf()));
	}
}
