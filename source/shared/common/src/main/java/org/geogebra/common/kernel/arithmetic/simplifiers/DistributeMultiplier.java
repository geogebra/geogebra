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
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Multiply node tag by tag if it still left factored after canceling GCD.
 * ie for (4m (1+sqrt(2)) / 2n will be 2m (1 + sqrt(2)) after cancellation,
 * simplifier gives 2m + 2m sqrt(2) as the result.
 */
public class DistributeMultiplier implements SimplifyNode {
	private final SimplifyUtils utils;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 */
	public DistributeMultiplier(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return isMultiplyNode(node);
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		ExpressionValue left = node.getLeft();
		if (isAtomicSurdAdditionNode(node.getRight())) {
			SurdAddition tag = new SurdAddition(node.getRightTree(), utils);
			ExpressionValue multiply = tag.multiply(left);
			OrderedExpressionNode orderedNode = new OrderedExpressionNode(multiply, utils);
			if (orderedNode.isAllNegative()) {
				multiply = utils.negateTagByTag(multiply).multiplyR(-1);
			}
			return multiply.wrap();
		}
		double lv = left.evaluateDouble();

		return DoubleUtil.isInteger(lv) && Math.abs(lv) != 1
				? utils.multiplyTagByTag(node.getRightTree(), (long) lv).wrap()
				: node;
	}
}
