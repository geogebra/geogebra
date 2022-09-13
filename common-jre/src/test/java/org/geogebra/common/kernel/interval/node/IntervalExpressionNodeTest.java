package org.geogebra.common.kernel.interval.node;

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
		assertEquals(IntervalConstants.zero(), node.evaluate().evaluate());
		functionVariable.set(Math.PI/2);
		assertEquals(IntervalConstants.one(), node.evaluate().evaluate());

	}
}
