package org.geogebra.common.kernel.arithmetic.filter.graphing;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeOperationFilter;
import org.geogebra.common.plugin.Operation;

final public class PowerInnerProductExpressionFilter extends ExpressionNodeOperationFilter {

	public PowerInnerProductExpressionFilter() {
		super(Operation.POWER);
	}

	@Override
	protected boolean isExpressionNodeAllowedForOperation(ExpressionNode expression) {
		return !(expression.getLeft().evaluatesToNDVector() && expression.getRight()
				.evaluatesToNumber(true));
	}
}
