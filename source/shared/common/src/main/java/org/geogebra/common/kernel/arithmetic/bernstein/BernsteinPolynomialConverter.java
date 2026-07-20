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

package org.geogebra.common.kernel.arithmetic.bernstein;

import java.util.Arrays;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class BernsteinPolynomialConverter {

	private final BernsteinBuilder2Var builder2D;

	public BernsteinPolynomialConverter() {
		builder2D = new BernsteinBuilder2Var(new BernsteinBuilder1Var());
	}

	/**
	 *
	 * @param geo to convert.
	 * @param limits of the result polynomial.
	 * @return the {@link BernsteinPolynomial2D} instance if possible, null otherwise.
	 */
	public BernsteinPolynomial2D bernsteinPolynomial2DFrom(GeoElement geo,
			BoundsRectangle limits) {
		FunctionNVar function = null;
		double[][] coefficients = null;
		boolean coefficientsValid = false;
		if (geo.isGeoFunctionNVar()) {
			function = ((GeoFunctionNVar) geo).getFunction();
		} else if (geo instanceof GeoImplicitCurve curve) {
			coefficients = curve.getCoeff();
			coefficientsValid = coefficients != null && coefficients.length > 0;
			function = coefficientsValid ? null : curve.getFunctionDefinition();
		}
		return coefficientsValid
				? builder2D.build(Arrays.copyOf(coefficients, coefficients.length),
				coefficients.length - 1, coefficients[0].length - 1, limits)
				: buildFromFunction(function, limits);
	}

	private BernsteinPolynomial2D buildFromFunction(FunctionNVar function, BoundsRectangle limits) {
		if (function == null) {
			return null;
		}
		Polynomial polynomial = function.getPolynomial();
		int degX = polynomial.degree('x');
		int degY = polynomial.degree('y');
		return builder2D.build(polynomial, degX, degY, limits);
	}

	/**
	 *
	 * @param geo to check
	 * @return if geo can be converted to a Bernstein polynomial.
	 */
	public static boolean iSupported(GeoElement geo) {
		if (geo.isGeoImplicitCurve()) {
			GeoImplicitCurve curve = (GeoImplicitCurve) geo;
			return isCurveSupported(curve);
		} else if (geo instanceof GeoFunctionNVar) {
			FunctionNVar function = ((GeoFunctionNVar) geo).getFunction();
			return isMultiVarPolynomial(function);
		} else if (geo instanceof GeoFunction) {
			Function function = ((GeoFunction) geo).getFunction();
			return isMultiVarPolynomial(function);
		}
		return false;
	}

	private static boolean isCurveSupported(GeoImplicitCurve curve) {
		if (!curve.isDefined()) {
			return false;
		}
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		return isMultiVarPolynomial(functionNVar);
	}

	private static boolean isMultiVarPolynomial(FunctionNVar function) {
		if (function == null) {
			return false;
		}

		Polynomial polynomial = function.getPolynomial();
		return polynomial != null && polynomial.degree('y') > 0;
	}

}
