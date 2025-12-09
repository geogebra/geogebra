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

package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.plugin.Operation;

public class SqrtMultiplyFixer implements Traversing {

	public static final Traversing INSTANCE = new SqrtMultiplyFixer();

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			switch (node.getOperation()) {
			case POWER:
			case FACTORIAL:
				node.fixPowerFactorial(Operation.MULTIPLY_OR_FUNCTION);
				break;
			case SQRT_SHORT:
				node.fixSqrtShort(Operation.MULTIPLY_OR_FUNCTION);
				break;
			case MULTIPLY_OR_FUNCTION:
				node.setOperation(Operation.MULTIPLY);
				break;
			default:
				break;
			}
		}
		return ev;
	}
}
