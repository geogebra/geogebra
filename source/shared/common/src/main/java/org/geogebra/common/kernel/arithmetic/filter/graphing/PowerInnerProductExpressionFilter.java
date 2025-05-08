package org.geogebra.common.kernel.arithmetic.filter.graphing;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeOperationFilter;
import org.geogebra.common.plugin.Operation;

final public class PowerInnerProductExpressionFilter extends ExpressionNodeOperationFilter {

	public PowerInnerProductExpressionFilter() {
		super(Operation.POWER);
	}

	@Override
	protected boolean isExpressionNodeAllowedForOperation(@Nonnull ExpressionNode expression) {
		return !(expression.getLeft().evaluatesToNDVector() && expression.getRight()
				.evaluatesToNumber(true));
	}
}
