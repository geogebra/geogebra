/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.getLeftMultiplier;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isDivNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;
import static org.geogebra.common.util.DoubleUtil.isInteger;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * <p>Cancels GCD from numerator and denominator if there is any.
 * The fraction should be factored out first. @see {@link FactorOutGCDFromSurd} or
 * {@link ExpandAndFactorOutGCD}</p>
 *
 * Examples:
 * <ul>
 *     <li>2 / 2sqrt(3) &#8594; 1 / sqrt(3)</li>
 *     <li>2 (-1 + sqrt(2)) / 4 &#8594; -1 + sqrt(2) / 2</li>
 *     <li>-9 (8 + sqrt(10))) / 54 &#8594; (-8 - sqrt(10)) / 6</li>
 *     <li>-4 (sqrt(2) + sqrt(3) - 2sqrt(6) - 4) / -2
 *         &#8594; 2sqrt(2) + 2sqrt(3) - 4sqrt(6) - 8</li>
 * </ul>
 */
public class CancelGCDInFraction implements SimplifyNode {

	private int multiplier;

	private enum NodeType {
		INVALID,
		SIMPLE_FRACTION,
		MULTIPLIED_FRACTION,
		MULTIPLIED_NUMERATOR,
		NEGATIVE_MULTIPLIED_NUMERATOR,
	}

	private final SimplifyUtils utils;
	private final Kernel kernel;
	private NodeType nodeType = NodeType.INVALID;
	private ExpressionNode numerator;

	/**
	 * @param utils {@link SimplifyUtils}
	 */
	public CancelGCDInFraction(SimplifyUtils utils) {
		this.utils = utils;
		kernel = utils.kernel;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		if (isDivNode(node)) {
			ExpressionNode leftTree = node.getLeftTree();
			multiplier = getLeftMultiplier(leftTree);
			if (multiplier == -1 && isMultiplyNode(leftTree.getRightTree())) {
				multiplier *= getLeftMultiplier(leftTree.getRightTree());
				nodeType = NodeType.NEGATIVE_MULTIPLIED_NUMERATOR;
			} else {
				nodeType = isTrivialMultiplier()
						? NodeType.SIMPLE_FRACTION
						: NodeType.MULTIPLIED_NUMERATOR;
			}
		}

		if (isMultiplyNode(node)
				&& isDivNode(node.getRightTree())) {
			multiplier = getLeftMultiplier(node);
			nodeType = isTrivialMultiplier()
					? NodeType.SIMPLE_FRACTION
					: NodeType.MULTIPLIED_FRACTION;
		}
		return nodeType != NodeType.INVALID;
	}

	private boolean isTrivialMultiplier() {
		return Math.abs(multiplier) == 1;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		switch (nodeType) {
		case MULTIPLIED_FRACTION:
			return applyForMultipliedFraction(node);
		case MULTIPLIED_NUMERATOR:
			return applyForMultipliedNumerator(node);
		case NEGATIVE_MULTIPLIED_NUMERATOR:
			return applyForNegativeMultipliedNumerator(node);
		case SIMPLE_FRACTION:
			return applyForSimpleFraction(node);
		case INVALID:
		default:
			return node;
		}
	}

	private ExpressionNode applyForNegativeMultipliedNumerator(ExpressionNode node) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode canceledNumerator = utils.newNode(
				utils.newDouble(multiplier),
				Operation.MULTIPLY,
				numerator.getRightTree().getRightTree()
		);
		ExpressionNode node1 = utils.newDiv(canceledNumerator, node.getRightTree());

		return applyForMultipliedNumerator(node1);
	}

	private ExpressionNode applyForMultipliedNumerator(ExpressionNode node) {
		int num = multiplier;
		int denominator = (int) Math.round(node.getRightTree().evaluateDouble());
		GCDInFraction gcd = new GCDInFraction(utils, num, denominator);

		if (gcd.isTrivial()) {
			return node;
		}

		gcd.flipIfNegative();

		return utils.div(
				utils.multiplyTagByTag(node.getLeftTree().getRightTree(), gcd.reducedNumerator()),
				gcd.reducedDenominatorValue());
	}

	private ExpressionNode applyForMultipliedFraction(ExpressionNode node) {
		GCDInFraction gcd = new GCDInFraction(utils, getLeftMultiplier(node),
				(long) node.getRightTree().getRightTree().evaluateDouble());

		return utils.div(utils.multiplyR(node.getRightTree().getLeftTree(), gcd.reducedNumerator()),
				gcd.reducedDenominatorValue());
	}

	private ExpressionNode applyForSimpleFraction(ExpressionNode node) {
		numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();

		return getCanceledFraction(node, numerator, denominator);
	}

	private ExpressionNode getCanceledFraction(ExpressionNode node, ExpressionNode node1,
			ExpressionNode node2) {
		ExpressionNode canceledFraction = null;
		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return simplifyConstantFraction(node, node1, node2);
		}
		if (isCancelable(node1, node2)) {
			canceledFraction = doCancel(node1, node2);
		} else if (isCancelable(node2, node1)) {
			canceledFraction = doCancel(node2, node1);
		}
		return canceledFraction != null ? canceledFraction : node;
	}

	private ExpressionNode simplifyConstantFraction(ExpressionNode node, ExpressionNode node1,
			ExpressionNode node2) {
		int n = (int) node1.evaluateDouble();
		int m = (int) node2.evaluateDouble();
		GCDInFraction gcd = new GCDInFraction(utils, n, m);
		if (gcd.isEqualDenominator()) {
			return gcd.reducedNumeratorValue().wrap();
		}

		if (!gcd.isTrivial() && !gcd.isEqualDenominator()) {
			return gcd.reducedFraction();
		}

		return node;
	}

	private boolean isCancelable(ExpressionNode node1, ExpressionNode node2) {
		return !node1.isLeaf() && node2.isLeaf();
	}

	private ExpressionNode doCancel(ExpressionNode node1, ExpressionNode node2) {
		ExpressionValue canceled = node2.getLeft();
		double evalCanceled = canceled.evaluateDouble();
		if (isMultiplyNode(node1)) {
			if (isMultiplyNode(node1.getLeftTree())) {
				node1.setRight(node1.getLeftTree().getRightTree().multiply(node1.getRight()));
				node1.setLeft(node1.getLeftTree().getLeft());
			}

			double evalLeft = node1.getLeft().evaluateDouble();
			double evalRight = node1.getRight().evaluateDouble();
			if (isInteger(evalLeft)) {
				return cancel(node1, node2, evalCanceled, evalLeft);
			}

			if (isInteger(evalRight)) {
				return cancel(node1, node2, evalCanceled, evalRight);
			}
		}
		return node1.divide(canceled);
	}

	private ExpressionNode cancel(ExpressionNode node1, ExpressionNode node2,
			double evalCanceled, double eval) {
		GCDInFraction gcd = new GCDInFraction(utils, (long) evalCanceled, (long) eval);
		if (gcd.isEqual(-1)) {
			return null;
		}

		if (gcd.isEqual((long) evalCanceled)) {
			if (node1 == numerator) {
				if (node1.getLeft().evaluateDouble() == evalCanceled) {
					return node1.getRightTree();
				}

				double rightValue = node1.getRightTree().evaluateDouble();
				if ((int) rightValue % (int) evalCanceled == 0) {
					double div = rightValue / evalCanceled;
					return DoubleUtil.isEqual(div, 1, Kernel.MAX_PRECISION)
							? node1.getLeftTree()
							: utils.newMultiply(utils.newDouble(div), node1.getLeftTree());
				}
			} else {
				return new ExpressionNode(kernel,
						new MyDouble(kernel, 1), Operation.DIVIDE, node1.getRight());
			}
		} else if (!gcd.isEqual(1)) {
			double v = eval / gcd.gcd();
			double canceledDominator = node2.divide(gcd.gcd()).evaluateDouble();
			ExpressionValue multRArg = node1.getLeft().isLeaf() ? node1.getRight()
					: node1.getLeft();
			return utils.newDiv(utils.newDouble(v).wrap().multiplyR(multRArg),
					utils.newDouble(canceledDominator)
			);
		}
		return null;
	}
}
