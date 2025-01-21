package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.plugin.Operation;

public class PositiveDenominator implements SimplifyNode {

	private NodeType nodeType = NodeType.INVALID;

	private enum NodeType {
		SIMPLE_FRACTION,
		MULTIPLIED_FRACTION,
		INVALID
	}

	private final SimplifyUtils utils;

	public PositiveDenominator(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		if (node.isOperation(Operation.DIVIDE)) {
			nodeType = NodeType.SIMPLE_FRACTION;
		} else if (node.isOperation(Operation.MULTIPLY)
				&& node.getRightTree().isOperation(Operation.DIVIDE)) {
			nodeType = NodeType.MULTIPLIED_FRACTION;
		}
		return nodeType != NodeType.INVALID;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		switch (nodeType) {
		case SIMPLE_FRACTION:
			return applyForSimpleFraction(node);
		case MULTIPLIED_FRACTION:
			return applyForMultipliedFraction(node);
		case INVALID:
		default:
		return node;
		}
	}

	private ExpressionNode applyForMultipliedFraction(ExpressionNode node) {
		int multiplier = utils.getLeftMultiplier(node);
		int denominator = (int) node.getRightTree().getRightTree().evaluateDouble();
		ExpressionNode num = multiplier == -1 ? utils.minusOne().wrap()
				: utils.newDouble(-multiplier).wrap();
		return utils.newNode(
				num,
				Operation.MULTIPLY,
				utils.div(node.getRightTree().getLeftTree(),
						utils.newDouble(-denominator).wrap())
		).wrap();
	}

	private ExpressionNode applyForSimpleFraction(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();
		double v = denominator.evaluateDouble();
		if (denominator.isLeaf() && v < 0) {
			return utils.newNode(utils.flipSign(numerator),
					Operation.DIVIDE,
					utils.newDouble(-v));
		}
		return node;
	}
}
