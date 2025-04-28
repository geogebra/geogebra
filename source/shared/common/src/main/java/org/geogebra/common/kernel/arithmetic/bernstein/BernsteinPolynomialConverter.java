package org.geogebra.common.kernel.arithmetic.bernstein;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Term;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

public class BernsteinPolynomialConverter {

	private final BernsteinBuilder1Var builder1D = new BernsteinBuilder1Var();
	private final BernsteinBuilder2Var builder2D;

	public BernsteinPolynomialConverter() {
		builder2D = new BernsteinBuilder2Var(builder1D);
	}

	/**
	 *
	 * @param geo to convert
	 * @param limits of the result polynomial.
	 * @return the {@link BernsteinPolynomial1D} instance if possible, null otherwise.
	 */
	public BernsteinPolynomial1D bernsteinPolynomial1DFrom(GeoElement geo,
			BoundsRectangle limits) {
		Polynomial polynomial = null;
		if (geo instanceof GeoFunction) {
			Function function = ((GeoFunction) geo).getFunction();
			if (function != null) {
				polynomial = function.getPolynomial();
			}
		} else if (geo instanceof GeoImplicitCurve) {
			FunctionNVar function = ((GeoImplicitCurve) geo).getFunctionDefinition();
			if (function != null) {
				polynomial = function.getPolynomial();
			}
		}

		if (polynomial != null) {
			return from1DPolynomial(polynomial, polynomial.degree('x'),
					polynomial.degree('y'), limits);
		}
		return null;
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
		if (geo.isGeoFunctionNVar()) {
			function = ((GeoFunctionNVar) geo).getFunction();
		} else if (geo.isGeoImplicitCurve()) {
			function = ((GeoImplicitCurve) geo).getFunctionDefinition();
		}
		if (function == null) {
			return null;
		}
		Polynomial polynomial = function.getPolynomial();
		return builder2D.build(polynomial, polynomial.degree('x'),
				polynomial.degree('y'), limits);
	}

	BernsteinPolynomial1D from1DPolynomial(Polynomial polynomial, int degreeX, int degreeY,
			BoundsRectangle limits) {
		if (degreeY == 0) {
			return builder1D.build(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, 'x', limits.getXmin(), limits.getXmax());
		}

		return builder1D.build(coeffsFromPolynomial(polynomial, degreeY, 'y'),
				degreeY, 'y', limits.getYmin(), limits.getYmax());
	}

	private double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
		double[] coeffs = new double[degree + 1];
		for (int i = 0; i <= degree; i++) {
			Term term = i < polynomial.length() ? polynomial.getTerm(i) : null;
			if (term != null) {
				int power = term.degree(variableName);
				coeffs[power] = term.getCoefficient().evaluateDouble();
			}
		}
		return coeffs;
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