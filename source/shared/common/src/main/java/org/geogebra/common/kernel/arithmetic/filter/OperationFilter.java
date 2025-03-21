package org.geogebra.common.kernel.arithmetic.filter;

import static org.geogebra.common.plugin.Operation.NO_OPERATION;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.plugin.Operation;

/**
 * A filter for operations used to restrict expressions containing them (during exams).
 */
public interface OperationFilter {
	/**
	 * @param operation operation to be evaluated
	 * @return {@code true} if the operation is allowed, {@code false} otherwise
	 */
	boolean isAllowed(Operation operation);

	/**
	 * Determines whether the specified expression is allowed or not
	 * with the specified filter for operations based on their occurrence in the expression.
	 * @param expressionValue the expression to be evaluated
	 * @param operationFilter the filter for operations
	 * @return {@code true} if the expression is allowed, {@code false} otherwise
	 */
	static boolean isAllowed(ExpressionValue expressionValue, OperationFilter operationFilter) {
		ExpressionNode expressionNode = null;
		if (expressionValue instanceof ExpressionNode) {
			expressionNode = (ExpressionNode) expressionValue;
		} else if (expressionValue instanceof GeoSymbolic
				&& ((GeoSymbolic) expressionValue).getValue() instanceof ExpressionNode) {
			expressionNode = (ExpressionNode) ((GeoSymbolic) expressionValue).getValue();
		}
		if (expressionNode == null) {
			return true;
		}

		Operation operation = expressionNode.getOperation();
		ExpressionValue right = expressionNode.getRight();
		ExpressionValue left = expressionNode.getLeft();
		return (operation == NO_OPERATION || operationFilter.isAllowed(operation))
				&& (right == null || isAllowed(right, operationFilter))
				&& (left == null || isAllowed(left, operationFilter));
	}
}
