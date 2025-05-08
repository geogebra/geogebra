package org.geogebra.common.kernel.arithmetic.filter.graphing;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeOperationFilter;
import org.geogebra.common.plugin.Operation;

final public class AbsExpressionFilter extends ExpressionNodeOperationFilter {

	public AbsExpressionFilter() {
		super(Operation.ABS);
	}

	@Override
	protected boolean isExpressionNodeAllowedForOperation(@Nonnull ExpressionNode expression) {
		ExpressionValue left = expression.getLeft();
		return left.evaluatesToNumber(true) || left instanceof FunctionNVar;
	}
}
