package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class PlusTagOrder implements SimplifyNode {
	private final SimplifyUtils utils;

	public PlusTagOrder(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (node.isOperation(Operation.DIVIDE)) {
			return utils.div(apply(node.getLeftTree()), apply(node.getRightTree()));
		}
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		double v = left.evaluateDouble();
		if (left.isLeaf() && v < 0
				&& node.isOperation(Operation.PLUS) && right.evaluateDouble() >= 0) {
			return utils.newNode(right, Operation.MINUS, utils.newDouble(-v));
		}
		return node;
	}
}
