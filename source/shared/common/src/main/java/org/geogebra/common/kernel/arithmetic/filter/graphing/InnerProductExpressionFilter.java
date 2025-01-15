package org.geogebra.common.kernel.arithmetic.filter.graphing;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeOperationFilter;
import org.geogebra.common.plugin.Operation;

final public class InnerProductExpressionFilter extends ExpressionNodeOperationFilter {

	public InnerProductExpressionFilter() {
		super(Operation.MULTIPLY);
	}

	@Override
	protected boolean isExpressionNodeAllowedForOperation(ExpressionNode expression) {
		return !(expression.getLeft().evaluatesToNDVector() && expression.getRight()
				.evaluatesToNDVector());
	}
}
