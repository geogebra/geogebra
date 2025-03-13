package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.*;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

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
				negated = negated.traverse(this::sortOperands);
				if (utils.isAllNegative(negated)) { // TODO: make isAllNegative() work
					return node;
				}
				return utils.newDiv(negated.traverse(this::sortOperands), fraction.getRight());
			} else {
				ExpressionNode negated = utils.negateTagByTag(node.getRight());
				if (utils.isAllNegative(negated)) {
					negated = node;
				}
				return negated.traverse(this::sortOperands).wrap();
			}
		}

		if (isDivNode(node)) {
			if (isIntegerValue(node.getLeft())) {
				return utils.newDiv(utils.newDouble(node.getLeft()), node.getRight());
			}
			ExpressionValue sortedNumerator = node.getLeft().traverse(this::sortOperands);
			if (sortedNumerator.wrap().getLeft().evaluateDouble() < 0) {
				ExpressionNode negatedNumerator = utils.negateTagByTag(sortedNumerator);
				ExpressionNode fraction = utils.newDiv(negatedNumerator, node.getRight());
				return fraction.multiplyR(-1);
			}
		}

		return node.traverse(this::sortOperands).wrap();
	}

	ExpressionValue sortOperands(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		if (node.isOperation(Operation.MINUS) && isAtomic(node.getRight())
				&& node.getRight().evaluateDouble() < 0) {
			right = utils.negateAtomic(right);
			node = utils.newNode(left, Operation.PLUS, right);
		}

		if (node.isOperation(Operation.PLUS)) {
			if (left.evaluateDouble() < right.evaluateDouble()) {
				return utils.newNode(right.traverse(this::sortOperands),
						Operation.PLUS, left.traverse(this::sortOperands));
			}
			return utils.newNode(left.traverse(this::sortOperands),
					Operation.PLUS, right.traverse(this::sortOperands));
		}

		return ev;
	}

	/**
	 * Package-private to be able to test it isolated.
	 */
	ExpressionNode sort(ExpressionNode node) {
		return node.traverse(this::sortOperands).wrap();
	}
}
