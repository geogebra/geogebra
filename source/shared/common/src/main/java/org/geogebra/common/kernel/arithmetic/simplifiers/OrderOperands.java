package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class OrderOperands implements SimplifyNode {
	private final SimplifyUtils utils;

	public OrderOperands(SimplifyUtils utils) {
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
				if (ev.isOperation(Operation.PLUS)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (valLeft < 0 && valRight > 0) {
						return utils.flipTrees(wrap);
					}
				} else if (ev.isOperation(Operation.MINUS)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (valLeft < 0 && valRight < 0 && SimplifyUtils.isIntegerValue(valLeft)) {
						return utils.newNode(utils.mulByMinusOne(wrap.getRightTree()),
								Operation.PLUS, utils.newDouble(valLeft));
					}

				} else if (ev.isOperation(Operation.MULTIPLY)) {
					ExpressionNode wrap = ev.wrap();
					double valLeft = wrap.getLeft().evaluateDouble();
					double valRight = wrap.getRight().evaluateDouble();
					if (SimplifyUtils.isIntegerValue(valRight)
							&& !SimplifyUtils.isIntegerValue(valLeft)) {
						ExpressionValue lft = process(wrap.getLeftTree());
						return utils.newNode(wrap.getRight(), wrap.getOperation(), lft);
					}
				}
				return ev;
			}
		}).wrap();
	}
}
