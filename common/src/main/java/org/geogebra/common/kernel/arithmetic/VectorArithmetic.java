package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
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

	/**
	 * @param kernel0
	 *            target kernel, ignored ATM
	 */
	private static ExpressionNode scalarProductComponent(Kernel kernel0, int i,
			ExpressionValue left1, ExpressionValue right1) {
		return computeCoord(left1.wrap(), i).multiply(
				computeCoord(right1.wrap(), i));
	}

	/**
	 * @param exp
	 *            expression
	 * @param i
	 *            0 for x, 1 for y, 2 for z
	 * @return given coordinate of expression
	 */
	public static ExpressionNode computeCoord(ExpressionNode exp, int i) {
		Kernel kernel = exp.getKernel();
		Operation[] ops = new Operation[] { Operation.XCOORD, Operation.YCOORD,
				Operation.ZCOORD };
		if (exp.isLeaf()) {
			ExpressionValue coord = extractCoord(exp, i, kernel);
			if (coord != null) {
				return coord.traverse(new CoordComputer()).wrap();
			}
		}
		switch (exp.getOperation()) {
		case VEC_FUNCTION:
			if (exp.getLeft() instanceof GeoCurveCartesianND) {
				return ((GeoCurveCartesianND) exp.getLeft()).getFun(i)
					.getExpression().deepCopy(kernel);
			}
			return new ExpressionNode(kernel, exp.traverse(new CoordComputer()),
					ops[i], null);
		case IF:
			return new ExpressionNode(kernel, exp.getLeft().deepCopy(kernel),
					Operation.IF, computeCoord(exp.getRightTree(), i));
		case PLUS:
			return computeCoord(exp.getLeftTree(), i).plus(
					computeCoord(exp.getRightTree(), i));
		case MINUS:
			return computeCoord(exp.getLeftTree(), i).subtract(
					computeCoord(exp.getRightTree(), i));
		case MULTIPLY:
			if (exp.getRight().evaluatesToNDVector()) {
				return computeCoord(exp.getRightTree(), i).multiply(
						exp.getLeft());
			} else if (exp.getLeft().evaluatesToNDVector()) {
				return computeCoord(exp.getLeftTree(), i).multiply(
						exp.getRight());
			}
		default:
			return new ExpressionNode(kernel, exp.traverse(new CoordComputer()),
					ops[i], null);
		}

	}

	private static ExpressionValue extractCoord(ExpressionNode exp, int i,
			Kernel kernel) {
		if (exp.getLeft() instanceof MyVecNode && ((MyVecNode) exp.getLeft())
				.getMode() == Kernel.COORD_CARTESIAN) {
			return i == 0 ? ((MyVecNode) exp.getLeft()).getX().wrap()
					: (i == 1 ? ((MyVecNode) exp.getLeft()).getY().wrap()
							: new ExpressionNode(kernel, 0));
		}
		if (exp.getLeft() instanceof MyVecNode && ((MyVecNode) exp.getLeft())
				.getMode() == Kernel.COORD_POLAR) {
			if (i == 2) {
				return new ExpressionNode(kernel, 0);
			}
			return ((MyVecNode) exp.getLeft()).getX().wrap()
					.multiply(((MyVecNode) exp.getLeft()).getY().wrap()
							.apply(i == 0 ? Operation.COS : Operation.SIN));
		}
		if (exp.getLeft() instanceof MyVec3DNode
				&& (((MyVec3DNode) exp.getLeft())
						.getMode() == Kernel.COORD_CARTESIAN
						|| ((MyVec3DNode) exp.getLeft())
								.getMode() == Kernel.COORD_CARTESIAN_3D)) {
			return i == 0 ? ((MyVec3DNode) exp.getLeft()).getX().wrap()
					: (i == 1 ? ((MyVec3DNode) exp.getLeft()).getY().wrap()
							: ((MyVec3DNode) exp.getLeft()).getZ().wrap());
		}
		return null;
	}
}
