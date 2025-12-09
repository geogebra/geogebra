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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

public class IntervalFunctionSupport {
	private static final UnsupportedOperatorChecker
			operatorChecker = new UnsupportedOperatorChecker();

	/**
	 *
	 * @param geo to check.
	 * @return true if the geo is a function
	 * and supported by our interval arithmetic implementation.
	 */
	public static boolean isSupported(GeoElement geo) {
		if (!(geo instanceof GeoFunction)) {
			return false;
		}

		return isOperationSupported(((GeoFunction) geo).getFunctionExpression());
	}

	static boolean isOperationSupported(ExpressionNode node) {
		if (node == null) {
			return false;
		}

		return !hasMoreVariables(node) && !node.any(operatorChecker);
	}

	private static boolean hasMoreVariables(ExpressionNode node) {
		if (node == null) {
			return false;
		}
		return node.any(new MultipleVariableChecker());
	}
}
