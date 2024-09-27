package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public final class RationalizableFraction {

	private static OperationCountChecker sqrtCountChecker =
			new OperationCountChecker(Operation.SQRT);

	/**
	 *
	 * @param root node to check
	 * @return if geo is a rationalizable fraction and supported by this algo.
	 */
	public static boolean isSupported(ExpressionNode root) {

		if (root == null) {
			return false;
		}

		if (!Operation.DIVIDE.equals(root.getOperation())) {
			return false;
		}

		int sq1 = getSquareRootCount(root.getLeft());
		int sq2 = getSquareRootCount(root.getRight());
		if ((sq1 > 1 || sq2 > 1) || (sq1 + sq2 == 0)) {
			return false;
		}

		ExpressionNode numerator = stripFromIntegerMultiplication(root.getLeftTree());
		ExpressionNode denominator = stripFromIntegerMultiplication(root.getRightTree());

		return isSubtreeSupported(numerator) && isSubtreeSupported(denominator);
	}

	private static ExpressionNode stripFromIntegerMultiplication(ExpressionNode leftTree) {
		return isMultipliedByInteger(leftTree)
				? leftTree.getRightTree()
				: leftTree;
	}

	private static boolean isMultipliedByInteger(ExpressionNode node) {
		return node.isOperation(Operation.MULTIPLY) && node.getLeft().isLeaf()
				&& isIntegerEvaluation(node.getLeftTree());
	}

	private static int getSquareRootCount(ExpressionValue node) {
		sqrtCountChecker.reset();
		node.inspect(sqrtCountChecker);
		return sqrtCountChecker.getCount();
	}

	private static boolean isSubtreeSupported(ExpressionNode node) {
		return (node.isLeaf() && isIntegerEvaluation(node))
				|| node.inspect(
						v -> isSquareRootOfPositiveInteger(node) || isPlusMinusInteger(node));
	}

	private static boolean isPlusMinusInteger(ExpressionNode node) {
		if (!(node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS))) {
			return false;
		}
		return isSquareRootPlusMinusInteger(node.getLeftTree(), node.getRightTree())
				|| isSquareRootPlusMinusInteger(node.getRightTree(), node.getLeftTree());
	}

	private static boolean isIntegerEvaluation(ExpressionNode node) {
		return evaluateAsInteger(node) != null;
	}

	private static boolean isSquareRootPlusMinusInteger(ExpressionNode node1,
			ExpressionNode node2) {
		return node1.isOperation(Operation.SQRT) && isIntegerEvaluation(node2);
	}

	private static boolean isSquareRootOfPositiveInteger(ExpressionNode node) {
		if (!node.isOperation(Operation.SQRT)) {
			return false;
		}
		Integer value = evaluateAsInteger(node.getLeftTree());
		return value != null && value >= 0;
	}

	/**
	 * Rationalize the fraction.
	 * @param node to rationalize
	 * @return the rationalized fraction.
	 */
	public static ExpressionNode getResolution(ExpressionNode node) {
		if (!isSupported(node)) {
			return null;
		}
		
		Integer v = evaluateAsInteger(node);
		Kernel kernel = node.getKernel();
		if (v != null) {
			return new MyDouble(kernel, v).wrap();
		}

		Integer denominatorValue = evaluateAsInteger(node.getRightTree());
		if (denominatorValue != null) {
			return denominatorValue == 1
			? new ExpressionNode(node.getLeftTree())
					: new ExpressionNode(kernel, node.getLeftTree(), Operation.DIVIDE,
					new MyDouble(kernel, denominatorValue));
		}

		RationalizeFractionAlgo algo =
				new RationalizeFractionAlgo(kernel, node.getLeftTree(),
						node.getRightTree());
		return algo.compute();
	}

	private static Integer evaluateAsInteger(ExpressionNode node) {
		double v = node.evaluateDouble();
		return DoubleUtil.isEqual(v,  (int) v) ? (int) v : null;
	}
}