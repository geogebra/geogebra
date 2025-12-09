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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.plugin.Operation;

public class GeoIntervalUtil {

	/**
	 * Checks given node and updates the arrays
	 *
	 * @param en
	 *            expression
	 * @param leftRightDouble
	 *            bounds as doubles
	 */
	public static void updateBoundaries(ExpressionNode en,
			double[] leftRightDouble) {
		leftRightDouble[0] = Double.NEGATIVE_INFINITY;
		leftRightDouble[1] = Double.POSITIVE_INFINITY;
		doUpdateBoundaries(en, leftRightDouble);
	}

	private static void doUpdateBoundaries(ExpressionNode en,
		double[] leftRightDouble) {
		Operation op = en.getOperation();
		if (op.equals(Operation.AND)
				|| op.equals(Operation.AND_INTERVAL)) {
			ExpressionNode enLeft = en.getLeftTree();
			ExpressionNode enRight = en.getRightTree();
			doUpdateBoundaries(enLeft, leftRightDouble);
			doUpdateBoundaries(enRight, leftRightDouble);
		} else if (op.equals(Operation.LESS)
				|| op.equals(Operation.LESS_EQUAL)) {
			// x < c
			updateBoundariesSingle(en.getLeft(), en.getRight(), leftRightDouble);
		} else if (op.equals(Operation.GREATER)
				|| op.equals(Operation.GREATER_EQUAL)) {
			updateBoundariesSingle(en.getRight(), en.getLeft(), leftRightDouble);
		} else {
			leftRightDouble[0] = leftRightDouble[1] = Double.NaN;
		}

		if (leftRightDouble[1] < leftRightDouble[0]) {
			leftRightDouble[0] = leftRightDouble[1] = Double.NaN;
		}
	}

	private static void updateBoundariesSingle(ExpressionValue smaller,
			ExpressionValue greater, double[] leftRightDouble) {
		if (smaller instanceof FunctionVariable
				&& isConstant(greater)) {
			leftRightDouble[1] = Math.min(leftRightDouble[1], greater.evaluateDouble());
		} else if (greater instanceof FunctionVariable
				&& isConstant(smaller)) {
			leftRightDouble[0] = Math.max(leftRightDouble[0], smaller.evaluateDouble());
		} else {
			leftRightDouble[0] = leftRightDouble[1] = Double.NaN;
		}
	}

	private static boolean isConstant(ExpressionValue greater) {
		return greater.isExpressionNode()
				? !greater.wrap().containsFreeFunctionVariable(null)
				: !(greater instanceof FunctionVariable);
	}

}
