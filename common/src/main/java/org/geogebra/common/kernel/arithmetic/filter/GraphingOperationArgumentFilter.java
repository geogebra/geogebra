package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.plugin.Operation;

/**
 * OperationArgumentFilter for the Graphing app.
 */
public class GraphingOperationArgumentFilter implements OperationArgumentFilter {

	@Override
	public boolean isAllowed(Operation operation, ExpressionValue left, ExpressionValue right) {
		return !filtersVector(operation, left, right) && !filtersAbs(operation, left);
	}

	private boolean filtersVector(Operation operation, ExpressionValue left,
			ExpressionValue right) {
		return (operation.equals(Operation.MULTIPLY) || operation.equals(Operation.VECTORPRODUCT))
				&& left instanceof VectorNDValue && right instanceof VectorNDValue
				&& (!isComplex(left) || !isComplex(right));
	}

	private boolean isComplex(ExpressionValue left) {
		return left instanceof VectorNDValue
				&& ((VectorNDValue) left).getToStringMode() == Kernel.COORD_COMPLEX;
	}

	private boolean filtersAbs(Operation operation, ExpressionValue left) {
		return operation.equals(Operation.ABS) && !(left instanceof NumberValue
				|| left instanceof FunctionalNVar);
	}
}
