package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.util.DoubleUtil;

public class ReduceRoot implements SimplifyNode {
	private final SimplifyUtils utils;

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	public ReduceRoot(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionValue reduceUnderSqrt = node.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (utils.isSqrt(ev)) {
					ExpressionValue surd = utils.getSurds(ev);
					if (surd != null) {
						return surd;
					}
					double valUnderSqrt = ev.wrap().getLeftTree().evaluateDouble();
					double sqrt = Math.sqrt(valUnderSqrt);
					if (DoubleUtil.isInteger(sqrt)) {
						return utils.newDouble(sqrt);
					}
					ExpressionValue evalUnderSqrt = utils.newDouble(valUnderSqrt);
					ev.wrap().setLeft(evalUnderSqrt);
				}
				return ev;
			}
		});
		double v = reduceUnderSqrt.evaluateDouble();
		if (v == Math.round(v)) {
			return utils.newDouble(v).wrap();
		}

		return utils.getSurdsOrSame(reduceUnderSqrt).wrap();

	}
}
