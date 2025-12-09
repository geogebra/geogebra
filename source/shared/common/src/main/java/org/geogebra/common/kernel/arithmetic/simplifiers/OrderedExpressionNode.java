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

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAddSubNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAtomic;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isDivNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * ExpressionNode that will be ordered after creation with positive integer value first followed
 * by the value, with the least value at the end.
 * Thus, if the first value is negative, means that the expression contains no positive values.
 */
public final class OrderedExpressionNode extends ExpressionNode {
	private final SimplifyUtils utils;

	/**
	 *
	 * @param value {@link ExpressionValue}
	 * @param utils {@link SimplifyUtils}
	 */
	public OrderedExpressionNode(ExpressionValue value, SimplifyUtils utils) {
		this(value.wrap(), utils);
	}

	/**
	 *
	 * @param node {@link ExpressionNode}
	 * @param utils {@link SimplifyUtils}
	 */
	public OrderedExpressionNode(ExpressionNode node, SimplifyUtils utils) {
		super(node);
		this.utils = utils;
		ExpressionNode sorted = node.traverse(this::sortOperands).wrap();
		setLeft(sorted.getLeft());
		setOperation(sorted.getOperation());
		setRight(sorted.getRight());
	}

	private ExpressionValue sortOperands(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		if (isAddSubNode(node) && isAtomic(node.getRight())
				&& node.getRight().evaluateDouble() < 0) {
			right = utils.negateAtomic(right);
			node = utils.newNode(left, Operation.inverse(node.getOperation()), right);
		}

		if (node.isOperation(Operation.PLUS)) {
			double v = left.evaluateDouble();
			if (v < right.evaluateDouble() && !(DoubleUtil.isInteger(v) && v > 0)) {
				return utils.newNode(right.traverse(this::sortOperands),
						Operation.PLUS, left.traverse(this::sortOperands));
			}
			return utils.newNode(left.traverse(this::sortOperands),
					Operation.PLUS, right.traverse(this::sortOperands));
		}

		return ev;
	}

	/**
	 *
	 * @return if the expression contains negative values only.
	 */
	public boolean isAllNegative() {
		return isAllNegative(this);
	}

	/**
	 *
	 * @param value {@link ExpressionValue}  to check.
	 * @return if the given expression value contains negative values only.
	 */
	private static boolean isAllNegative(ExpressionValue value) {
		ExpressionNode node = value.wrap();
		while (!node.isLeaf()) {
			node = node.getLeftTree();
		}
		return node.evaluateDouble() < 0;
	}

	/**
	 *
	 * @return if the expression is a frction and its numerator contains negative values only.
	 */
	public boolean hasNumeratorNegativesOnly() {
		return isDivNode(this) && isAllNegative(getLeft());
	}

}
