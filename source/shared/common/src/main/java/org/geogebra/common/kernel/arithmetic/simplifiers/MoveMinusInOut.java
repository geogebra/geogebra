package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAtomic;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isDivNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMinusOne;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * <p>Simplifier to "move into" minus sign or "move out from" expression to improve readability.</p>
 *
 * Examples:
 * <ul>
 *     <li>-sqrt(15) + 6 -> 6 - sqrt(15)</li>
 *     <li>-((sqrt(5) - 6) / 5) -> (6 - sqrt(5)) / 5"</li>
 * </ul>
 **/
public class MoveMinusInOut implements SimplifyNode {
	private final SimplifyUtils utils;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 */
	public MoveMinusInOut(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (isMultiplyNode(node) && isMinusOne(
				node.getLeft())) {
			if (isDivNode(node.getRightTree())) {
				ExpressionNode fraction = node.getRightTree();
				ExpressionValue numerator = fraction.getLeft();
				ExpressionValue negated = utils.negateTagByTag(numerator);

				OrderedExpressionNode orderedNode = new OrderedExpressionNode(negated.wrap(),
						utils);
				if (orderedNode.isAllNegative()) {
					return node;
				}
				return utils.newDiv(orderedNode, fraction.getRight());
			} else {
				ExpressionNode negated = utils.negateTagByTag(node.getRight());
				OrderedExpressionNode orderedNode = new OrderedExpressionNode(negated.wrap(),
						utils);
				if (orderedNode.isAllNegative()) {
					negated = node;
				}
				return orderedNode;
			}
		}

		OrderedExpressionNode orderedNode = new OrderedExpressionNode(node, utils);
		if (orderedNode.hasNumeratorNegativesOnly() && !isAtomic(orderedNode.getLeft())) {
			ExpressionNode numerator = utils.negateTagByTag(node.getLeftTree());
			return utils.newDiv(numerator, orderedNode.getRight()).multiplyR(-1);
		}
		return orderedNode;
	}
}
