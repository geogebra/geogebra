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

	ExpressionValue getDerivative(String funcName) {
		int index = funcName.length() - 1;
		int order = 0;
		while (index >= 0 && funcName.charAt(index) == '\'') {
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
