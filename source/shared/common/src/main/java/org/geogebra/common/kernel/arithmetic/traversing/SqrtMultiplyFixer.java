package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class SqrtMultiplyFixer implements Traversing {

	public static final Traversing INSTANCE = new SqrtMultiplyFixer();

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			switch (node.getOperation()) {
			case POWER:
			case FACTORIAL:
				node.fixPowerFactorial(Operation.MULTIPLY_OR_FUNCTION);
				break;
			case SQRT_SHORT:
				node.fixSqrtShort(Operation.MULTIPLY_OR_FUNCTION);
				break;
			case MULTIPLY_OR_FUNCTION:
				node.setOperation(Operation.MULTIPLY);
				break;
			default:
				break;
			}
		}
		return ev;
	}
}
