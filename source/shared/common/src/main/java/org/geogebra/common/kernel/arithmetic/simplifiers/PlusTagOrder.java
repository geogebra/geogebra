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

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * <p>Simplifier to rewrite -a + b to a - b, where 'a' is an integer and 'b' is any expression.</p>
 *
 * Examples:
 * <ul>
 *     <li>-1 + sqrt(2) &#8594; sqrt(2) - 1</li>
 *     <li>(-1 + sqrt(2)) / 2 &#8594; (sqrt(2) - 1) / 2</li>
 * </ul>
 */
public class PlusTagOrder implements SimplifyNode {
	private final SimplifyUtils utils;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 */
	public PlusTagOrder(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (ExpressionValueUtils.isDivNode(node)) {
			return utils.div(apply(node.getLeftTree()), apply(node.getRightTree()));
		}
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		double v = left.evaluateDouble();
		if (left.isLeaf() && v < 0
				&& node.isOperation(Operation.PLUS) && right.evaluateDouble() >= 0) {
			return utils.newNode(right, Operation.MINUS, utils.newDouble(-v));
		}
		return node;
	}
}
