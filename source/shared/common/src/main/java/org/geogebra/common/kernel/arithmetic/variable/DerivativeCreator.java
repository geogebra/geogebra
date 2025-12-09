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

package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.FunctionParser;

class DerivativeCreator {

	private Kernel kernel;

	DerivativeCreator(Kernel kernel) {
		this.kernel = kernel;
	}

	private boolean isDerivativeChar(char ch) {
		return ch == '\'' || ch == '\u2018' || ch == '\u2019';
	}

	ExpressionValue getDerivative(String funcName) {
		int index = funcName.length() - 1;
		int order = 0;
		while (index >= 0 && isDerivativeChar(funcName.charAt(index))) {
			order++;
			index--;
		}
		GeoElement geo = null;
		boolean hasGeoDerivative = false;
		while (index < funcName.length() && !hasGeoDerivative) {
			String label = funcName.substring(0, index + 1);
			geo = kernel.lookupLabel(label);
			hasGeoDerivative = geo != null && hasDerivative(geo);
			// stop if f' is defined but f is not defined, see #1444
			if (!hasGeoDerivative) {
				order--;
				index++;
			}
		}

		if (hasGeoDerivative) {
			return FunctionParser.derivativeNode(kernel, geo, order,
					geo.isGeoCurveCartesian(), new FunctionVariable(kernel));
		}
		return null;
	}

	private boolean hasDerivative(GeoElement geoElement) {
		return geoElement.isRealValuedFunction() || geoElement.isGeoCurveCartesian();
	}
}
