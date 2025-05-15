package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isSqrtNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * <p>Simplifies square roots in all across the node as much as possible.</p>
 * <br>Examples:
 * <ul>
 *  <li>sqrt(4) &#8594; 2</li>
 *  <li>sqrt(3 + 2) &#8594; sqrt(5)</li>
 *  <li>sqrt(18) &#8594; 3sqrt(2)</li>
 *  <li>2sqrt(18) &#8594; 6sqrt(2)</li>
 *  <li>sqrt(54) &#8594; 3sqrt(6)</li>
 * </ul>
 */
public class ReduceRoot implements SimplifyNode {
	private final SimplifyUtils utils;

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	/**
	 * @param utils {@link SimplifyUtils}
	 */
	public ReduceRoot(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionValue reduced = node.traverse(this::reduceRadicand);
		return utils.getSurdsOrSame(reduced.wrap());

	}

	private ExpressionValue reduceRadicand(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		if (isMultiplyNode(ev) && isIntegerValue(node.getLeft()) && isSqrtNode(node.getRight())) {
			ExpressionValue surd = utils.getSurds(node.getRight());
			if (surd != null) {
				double reducedMultiplier = node.getLeft().evaluateDouble()
						* surd.wrap().getLeft().evaluateDouble();
				return utils.multiplyR(surd.wrap().getRightTree(), reducedMultiplier);
			}
		}
		if (isSqrtNode(ev)) {
			ExpressionValue surd = utils.getSurds(ev);
			if (surd != null) {
				return surd;
			}
			double valUnderSqrt = node.getLeftTree().evaluateDouble();
			double sqrt = Math.sqrt(valUnderSqrt);
			if (DoubleUtil.isInteger(sqrt)) {
				return utils.newDouble(sqrt);
			}
			ExpressionValue evalUnderSqrt = utils.newDouble(valUnderSqrt);
			node.setLeft(evalUnderSqrt);
		}
		return ev;
	}
}
