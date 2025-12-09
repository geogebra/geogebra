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

package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.plugin.Operation;

/**
 * Checker to determine if an operation is supported by the interval arithmetic.
 */
public class UnsupportedOperatorChecker implements Inspecting {

	private GeoFunctionConverter converter = null;

	@Override
	public boolean check(ExpressionValue v) {
		ExpressionNode wrap = v.wrap();
		Operation operation = wrap.getOperation();

		if (operation == Operation.MULTIPLY) {
			return checkMultiply(wrap);
		} else if (operation == Operation.POWER) {
			return checkPower(wrap);
		}
		if (converter == null) {
			converter = wrap.getKernel().getFunctionConverter();
		}
		return !converter.isSupportedOperation(operation);
	}

	private boolean checkMultiply(ExpressionNode node) {
		return isVector(node.getLeft()) ^ isVector(node.getRight());
	}

	private boolean isVector(ExpressionValue node) {
		return node.evaluatesToNDVector() || node.evaluatesToNonComplex2DVector();
	}

	private boolean checkPower(ExpressionNode node) {
		double power = node.getRight().evaluateDouble();
		if (Double.isNaN(power)) {
			return true;
		}

		return power >= 100;
	}
}
