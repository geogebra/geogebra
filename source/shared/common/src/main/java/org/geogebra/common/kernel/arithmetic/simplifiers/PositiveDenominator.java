package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * <p>Ensures if the denominator of the fraction is positive by multiplying the whole
 * fraction with -1 if necessary. It also makes sure the result will be in the simplest form.</p>
 *
 * Examples:
 * <ul>
 *     <li>3 + sqrt(2) / -5 -> -((3 + sqrt(2)) / 5)</li>
 *     <li>7 (3 + sqrt(2)) / -5 -> (21 + 7sqrt(2)) / 5</li>
 *     <li>(3 (-3 - sqrt(2)) / 7) -> (-(9 - 3sqrt(2))) / 7</li>
 * </ul>
 */
public class PositiveDenominator implements SimplifyNode {

	private final SimplifyUtils utils;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 */
	public PositiveDenominator(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return ExpressionValueUtils.isDivNode(node)
				|| (ExpressionValueUtils.isMultiplyNode(node) && isAccepted(node.getRightTree()));
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (ExpressionValueUtils.isMultiplyNode(node)) {
			ExpressionNode fraction = node.getRightTree();
			return applyForMultipliedFraction(node.getLeft(),
					fraction.getLeft(), fraction.getRight().evaluateDouble());
		}
		ExpressionNode denominator = node.getRightTree();
		ExpressionNode numerator = node.getLeftTree();
		double v = denominator.evaluateDouble();

		if (!denominator.isLeaf()) {
			return node;
		}

		ExpressionValue positiveDenominator = utils.newDouble(Math.abs(v));
		if (ExpressionValueUtils.isMultiplyNode(numerator)) {
			ExpressionValue left = numerator.getLeft();
			ExpressionValue right = numerator.getRight();
			if (ExpressionValueUtils.isMultiplyNode(right)) {
				if (ExpressionValueUtils.isAtomicSurdAdditionNode(right.wrap().getLeft())) {
					SurdAddition tag = new SurdAddition(right.wrap().getLeftTree(), utils);
					OrderedExpressionNode orderedNode = new OrderedExpressionNode(
							tag.multiply(right.wrap().getRight()), utils);
					if (orderedNode.isAllNegative()) {
						right = utils.negateTagByTag(right);
						if (v > 0) {
							right = right.wrap().multiplyR(-1);
						}
					} else {
						if (v < 0) {
							right = right.wrap().multiplyR(-1);
						}
					}
				}
				return utils.newDiv(right.wrap().multiply(left), positiveDenominator);

			} else if (ExpressionValueUtils.isAtomicSurdAdditionNode(right)) {
				SurdAddition tag = new SurdAddition(right.wrap(), utils);
				right = tag.multiply(left);
				if (utils.isAllNegative(right)) {
					right = utils.negateTagByTag(right);
					ExpressionNode fraction = utils.newDiv(right, positiveDenominator);
					return v > 0 ? fraction.multiplyR(-1) : fraction;
				}

				ExpressionNode div = utils.newDiv(right, positiveDenominator);
				return v < 0 ? div.multiplyR(-1) : div;
			}

			if (utils.isAllNegative(right)) {
				ExpressionNode multiplierNode = (isIntegerValue(left)
						? utils.newDouble(-left.evaluateDouble())
						: left.wrap()).wrap();

				numerator = utils.multiplyR(multiplierNode, utils.negateTagByTag(right));
				return utils.newDiv(numerator, v < 0 ? utils.newDouble(-v) : denominator);
			}
		}
		if (ExpressionValueUtils.isAtomicSurdAdditionNode(numerator)) {
			ExpressionNode result = utils.newNode(numerator,
					Operation.DIVIDE,
					positiveDenominator);
			return v < 0 ? result.multiplyR(-1) : result;
		}

		if (numerator.isLeaf()) {
			numerator = v < 0 ? numerator.multiplyR(-1) : numerator;
			return utils.newNode(numerator, Operation.DIVIDE, positiveDenominator);
		}

		ExpressionNode expressionNode =
				utils.newNode(numerator, Operation.DIVIDE, positiveDenominator);
		return v < 0 ? expressionNode.multiplyR(-1) : expressionNode;
	}

	private ExpressionNode applyForMultipliedFraction(ExpressionValue multiplierNode,
			ExpressionValue numerator, double denominator) {
		double multiplier = multiplierNode.evaluateDouble();
		SurdAddition tag = new SurdAddition(numerator.wrap(), utils);
		ExpressionValue numerator1 = tag.multiply(multiplier);
		return utils.newDiv(numerator1, utils.newDouble(Math.abs(denominator)));
	}
}
