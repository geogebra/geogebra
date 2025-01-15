package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.plugin.Operation;

/**
 * Filters expression nodes based on Operation. If used with subclassing, special case criteria
 * can be implemented using {@link ExpressionNodeOperationFilter#isExpressionNodeAllowedForOperation(ExpressionNode)}
 * method. Otherwise, can be used as filtering for operations.
 */
public class ExpressionNodeOperationFilter extends ExpressionNodeFilter {

	final Operation operation;

	public ExpressionNodeOperationFilter(Operation operation) {
		this.operation = operation;
	}

	@Override
	protected boolean isExpressionNodeAllowed(ExpressionNode expressionNode) {
		return !expressionNode.isOperation(operation) || isExpressionNodeAllowedForOperation(
				expressionNode);
	}

	/**
	 * Checks if expression node with given operation is allowed. Consider this as a special case,
	 * where an operation is filtered only if some conditions are met.
	 * @param expression expression node
	 * @return true if operation is allowed. returns false by default.
	 */
	protected boolean isExpressionNodeAllowedForOperation(ExpressionNode expression) {
		return false;
	}
}
