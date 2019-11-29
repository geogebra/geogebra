package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.plugin.Operation;

/**
 * OperationArgumentFilter for the Graphing app.
 */
public class GraphingOperationArgumentFilter implements OperationArgumentFilter {

	@Override
	public boolean isAllowed(Operation operation, ExpressionValue left, ExpressionValue right) {
		return !isVectorProduct(operation, left, right) && !isNotNumericAbs(operation, left, right);
	}

	private boolean isVectorProduct(Operation operation, ExpressionValue left,
									ExpressionValue right) {
		return (operation.equals(Operation.MULTIPLY) || operation.equals(Operation.VECTORPRODUCT))
				&& left instanceof VectorNDValue && right instanceof VectorNDValue;
	}

	private boolean isNotNumericAbs(Operation operation, ExpressionValue left,
								   ExpressionValue right) {
		return operation.equals(Operation.ABS) && left instanceof NumberValue;
	}
}
