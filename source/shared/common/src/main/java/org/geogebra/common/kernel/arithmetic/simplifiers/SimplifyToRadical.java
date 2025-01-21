package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public class SimplifyToRadical implements SimplifyNode {

	private final SimplifyUtils utils;

	public SimplifyToRadical(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return utils.isDivNode(node);
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		if (utils.isSqrt(numerator)) {
			ExpressionValue reducedSqrt = utils.getSurds(numerator);
			if (reducedSqrt != null) {
				return utils.div(reducedSqrt, node.getRightTree());
			}
		} else if (utils.isMultiplyNode(numerator)) {
			ExpressionNode rightTree = numerator.getRightTree();
			ExpressionValue reducedSqrt = utils.getSurdsOrSame(rightTree);
			if (reducedSqrt != rightTree) {
				ExpressionNode constantProduct =
						numerator.getLeftTree().multiplyR(reducedSqrt.wrap().getLeftTree());
				return utils.newDiv(
						reducedSqrt.wrap().getRightTree().multiplyR(
								constantProduct.unwrap().evaluateDouble()),
						node.getRightTree());
			}
		}

		return node;
	}
}
