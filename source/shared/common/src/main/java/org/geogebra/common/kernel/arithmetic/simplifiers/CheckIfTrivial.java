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

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isDivNode;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;

/**
 * <p>If node evaluates to a trivial value, it reduces to the simplest trivial node.</p>
 *
 * Examples:
 * <ul>
 *     <li>expression p evaluates an integer n &#8594; p is replaced by n</li>
 *     <li>n + 0 or 0 + n &#8594; n</li>
 *     <li>n / 0, n &gt;= 0 &#8594; infinity</li>
 *     <li>n / 0, n &lt; 0 &#8594; negative infinity</li>
 * </ul>
 *
 */
public class CheckIfTrivial implements SimplifyNode {

	private final SimplifyUtils utils;

	public CheckIfTrivial(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		return node.traverse(ev -> {
			if (ev instanceof MyDouble) {
				return ev;
			}
			double v = ev.evaluateDouble();
			if (Math.round(v) == v) {
				return utils.newDouble(v);
			}

			ExpressionNode node1 = ev.wrap();
			if (ev.isOperation(Operation.PLUS)) {
				if (node1.getLeft().evaluateDouble() == 0) {
					return node1.getRight();
				}
				if (node1.getRight().evaluateDouble() == 0) {
					return node1.getLeft();
				}
			} else if (isDivNode(node1)) {
				double numeratorVal = node1.getLeft().evaluateDouble();
				double denominatorVal = node1.getRight().evaluateDouble();
				if (denominatorVal == 0)  {
					return numeratorVal > 0
							? utils.infinity()
							: utils.negativeInfinity();
				}
			}
			return ev;
		}).wrap();
	}
}
