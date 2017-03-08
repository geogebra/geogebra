package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

/**
 * Functions for csymbolic computation with vectors
 */
public class VectorArithmetic {
	/**
	 * @param kernel
	 *            kernel for output expression
	 * @param left
	 *            left subtree
	 * @param right
	 *            right subtree
	 * @param operation
	 *            operation
	 * @return x(left)*x(right)+y(left)*y(right)
	 */
	static ExpressionNode expandScalarProduct(Kernel kernel,
			ExpressionValue left, ExpressionValue right, Operation operation) {
		if (operation == Operation.MULTIPLY
				&& left.evaluatesToNonComplex2DVector()
				&& right.evaluatesToNonComplex2DVector()) {
			return scalarProductComponent(kernel, 0, left, right)
					.plus(scalarProductComponent(kernel, 1, left, right));

		}
		if (operation == Operation.MULTIPLY && left.evaluatesTo3DVector()
				&& right.evaluatesTo3DVector()) {
			return scalarProductComponent(kernel, 0, left, right)
					.plus(scalarProductComponent(kernel, 1, left, right))
					.plus(scalarProductComponent(kernel, 2, left, right));
		}
		if (operation == Operation.POWER && left.evaluatesToNonComplex2DVector()
				&& ExpressionNode.isConstantDouble(right, 2)) {
			return scalarProductComponent(kernel, 0, left, left)
					.plus(scalarProductComponent(kernel, 1, left, left));

		}
		if (operation == Operation.POWER && left.evaluatesTo3DVector()
				&& ExpressionNode.isConstantDouble(right, 2)) {
			return scalarProductComponent(kernel, 0, left, left)
					.plus(scalarProductComponent(kernel, 1, left, left).plus(
							scalarProductComponent(kernel, 2, left, left)));

		}
		return null;
	}

	private static ExpressionNode scalarProductComponent(Kernel kernel0, int i,
			ExpressionValue left1, ExpressionValue right1) {
		return kernel0.getAlgebraProcessor().computeCoord(left1.wrap(), i)
				.multiply(kernel0.getAlgebraProcessor()
						.computeCoord(right1.wrap(), i));
	}
}
