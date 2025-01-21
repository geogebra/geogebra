package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class ReduceToIntegers implements SimplifyNode {

	private final SimplifyUtils utils;

	public ReduceToIntegers(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof MyDouble) {
					return ev;
				}
				double v = ev.evaluateDouble();
				if (Math.round(v) == v) {
					return utils.newDouble(v);
				}

				ExpressionNode node = ev.wrap();
				if (ev.isOperation(Operation.PLUS)) {
					if (node.getLeft().evaluateDouble() == 0) {
						return node.getRight();
					}
					if (node.getRight().evaluateDouble() == 0) {
						return node.getLeft();
					}
				} else if (ev.isOperation(Operation.MINUS)) {
					if (node.getLeft().evaluateDouble() == 0) {
						return utils.makeNegative(node.getRightTree());
					}
					if (node.getRight().evaluateDouble() == 0) {
						return node.getLeft();
					}
				} else if (utils.isDivNode(node)) {
					double numeratorVal = node.getLeft().evaluateDouble();
					double denominatorVal = node.getRight().evaluateDouble();
					if (denominatorVal == 0)  {
						return numeratorVal > 0
								? utils.infinity()
								: utils.negativeInfinity();
					}
				}
				return ev;
			}

		}).wrap();
	}
}
