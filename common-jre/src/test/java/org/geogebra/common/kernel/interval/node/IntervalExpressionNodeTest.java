package org.geogebra.common.kernel.interval.node;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class IntervalExpressionNodeTest extends BaseUnitTest {
	@Test
	public void testCreation() {
		IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
		IntervalExpressionNode node = new IntervalExpressionNode(functionVariable,
				IntervalOperation.SIN);
		functionVariable.set(Math.PI);
		assertEquals(IntervalConstants.zero(), node.evaluate().value());
		functionVariable.set(Math.PI/2);
		assertEquals(one(), node.evaluate().value());
	}

	@Test
	public void testConstant() {
		IntervalFunctionValue constant = new IntervalFunctionValue(one());
		IntervalExpressionNode node = new IntervalExpressionNode(constant);
		assertEquals(one(), node.evaluate().value());
	}
}
