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

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAtomicSurdAdditionNode;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * <p>Factor out GCD, if node is a {@link SurdAddition} <b>a + m * sqrt(b)</b>,
 * multiplied {@link SurdAddition} <b>m * (a + sqrt(b))</b> or a fraction of these two.
 * For expanded numerators @see {@link ExpandAndFactorOutGCD}</p>
 *
 * Examples:
 * <ul>
 *     <li>12 - 4sqrt(2) &#8594; 4 (3 - sqrt(2)</li>
 *     <li>3 (2 + 2sqrt(2)) &#8594; 6 (1 + sqrt(2))</li>
 *     <li>(2 - 2sqrt(2)) / 4 &#8594; (2 (1 - sqrt(2))) / 4</li>
 * </ul>
 */
public final class FactorOutGCDFromSurd implements SimplifyNode {

	private enum NodeType {
		NONE,
		SURD_ADDITION,
		MULTIPLIED,
		FRACTION
	}

	private final SimplifyUtils utils;

	public FactorOutGCDFromSurd(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	private NodeType getNodeType(ExpressionNode node) {
		if (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS)) {
			return getPlusOrMinusType(node);
		}

		if (node.isOperation(Operation.MULTIPLY) && isAtomicSurdAdditionNode(node.getRightTree())) {
			return NodeType.MULTIPLIED;
		}

		if (node.isOperation(Operation.DIVIDE)
				&& isAccepted(node.getLeftTree()) && isAccepted(node.getRightTree())) {
			return NodeType.FRACTION;
		}

		return NodeType.NONE;
	}

	private NodeType getPlusOrMinusType(ExpressionNode node) {
		return minusNodeAccepted(node.getLeftTree(), node.getRightTree())
				? NodeType.SURD_ADDITION
				: NodeType.NONE;
	}

	private boolean minusNodeAccepted(ExpressionNode leftTree, ExpressionNode rightTree) {
		int num1 = utils.getNumberForGCD(leftTree);
		int num2 = utils.getNumberForGCD(rightTree);
		return hasRealGCD(Math.min(num1, num2), Math.max(num1, num2));
	}

	private boolean hasRealGCD(int num1, int num2) {
		long gcd = Kernel.gcd(num1, num2);
		return Math.abs(gcd) != 1 && gcd <= num2;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		NodeType nodeType = getNodeType(node);
		switch (nodeType) {
		case FRACTION:
			ExpressionNode left = apply(node.getLeftTree());
			ExpressionNode right = apply(node.getRightTree());
			return left != null && right != null
					? utils.div(left, right)
					: node;
		case SURD_ADDITION:
			return factorOutPlusOrMinusNode(node);
		case MULTIPLIED:
			ExpressionNode expressionNode =
					factorOutMultiplied(node.getLeftTree(), node.getRightTree());
			return expressionNode != null
					? expressionNode
					: node;
		default:
			return node;
		}
	}

	private ExpressionNode factorOutPlusOrMinusNode(ExpressionNode node) {
		SurdAddition tag = new SurdAddition(node, utils);
		return tag.factorOut();
	}

	private ExpressionNode factorOutMultiplied(ExpressionNode leftTree, ExpressionNode rightTree) {
		if (hasOnlyOnePlusOrMinus(rightTree) && leftTree.evaluateDouble() != -1) {
			SurdAddition surdAddition = new SurdAddition(rightTree, utils);
			ExpressionNode node1 = surdAddition.factorOut();

			if (node1 != null) {
				if (node1.getRightTree() == null) {
					return node1;
				}

				return utils.multiplyR(node1.getRightTree(),
						leftTree.evaluateDouble() * node1.getLeft().evaluateDouble());
			}
		}
		return null;
	}

	private boolean hasOnlyOnePlusOrMinus(ExpressionNode node) {
		int count = 0;
		for (ExpressionValue value: node) {
			if (value.isOperation(Operation.PLUS) || value.isOperation(Operation.MINUS)) {
				count++;
			}
		}
		return count <= 1;
	}
}