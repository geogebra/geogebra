package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.plugin.Operation;

public class GeoIntervalUtil {

	/**
	 * Checks given node and updates the arrays
	 *
	 * @param en
	 *            expression
	 * @param leftRightDouble
	 *            bounds as doubles
	 */
	public static void updateBoundaries(ExpressionNode en,
			double[] leftRightDouble) {

		leftRightDouble[0] = Double.NaN;
		leftRightDouble[1] = Double.NaN;

		if (en.getOperation().equals(Operation.AND)
				|| en.getOperation().equals(Operation.AND_INTERVAL)) {
			ExpressionNode enLeft = en.getLeftTree();
			ExpressionNode enRight = en.getRightTree();
			updateBoundariesSingle(enLeft, leftRightDouble);
			updateBoundariesSingle(enRight, leftRightDouble);
		}

		if (leftRightDouble[1] < leftRightDouble[0]) {
			leftRightDouble[0] = leftRightDouble[1] = Double.NaN;
		}
	}

	private static void updateBoundariesSingle(ExpressionNode ineq, double[] leftRightDouble) {
		Operation opLeft = ineq.getOperation();
		ExpressionValue ineqLeft = ineq.getLeft();
		ExpressionValue ineqRight = ineq.getRight();
		if ((opLeft.equals(Operation.LESS)
				|| opLeft.equals(Operation.LESS_EQUAL))) {
			if (ineqLeft instanceof FunctionVariable
					&& ineqRight.isNumberValue()) {

				leftRightDouble[1] = ineqRight.evaluateDouble();
			} else if (ineqRight instanceof FunctionVariable
					&& ineqLeft.isNumberValue()) {
				leftRightDouble[0] = ineqLeft.evaluateDouble();
			}

		} else if ((opLeft.equals(Operation.GREATER)
				|| opLeft.equals(Operation.GREATER_EQUAL))) {
			if (ineqLeft instanceof FunctionVariable
					&& ineqRight.isNumberValue()) {
				leftRightDouble[0] = ineqRight.evaluateDouble();
			} else if (ineqRight instanceof FunctionVariable
					&& ineqLeft.isNumberValue()) {
				leftRightDouble[1] = ineqLeft.evaluateDouble();
			}
		}
	}

}
