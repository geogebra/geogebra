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

	private final BernsteinBuilder1Var builder1Var = new BernsteinBuilder1Var();
	private final BernsteinBuilder2Var builder2Var;

	public BernsteinPolynomialConverter() {
		builder2Var = new BernsteinBuilder2Var(builder1Var);
	}

	/**
	 *
	 * @param geo to convert
	 * @param limits of the result polynomial.
	 * @return the equivalent Bernstein polynomial of geo if possible, null otherwise.
	 */
	public BernsteinPolynomial from(GeoElement geo, BoundsRectangle limits) {
		if (geo.isGeoImplicitCurve()) {
			GeoImplicitCurve curve = (GeoImplicitCurve) geo;
			return curve.isDefined() ? fromImplicitCurve(curve, limits) : null;
		} else if (geo instanceof GeoFunctionNVar) {
			FunctionNVar function = ((GeoFunctionNVar) geo).getFunction();
			return function != null ? fromFunctionNVar(function, limits) : null;
		} else if (geo instanceof GeoFunction) {
			Function function = ((GeoFunction) geo).getFunction();
			return function != null ? fromFunctionNVar(function, limits) : null;
		}
		return null;
	}

	private BernsteinPolynomial fromImplicitCurve(GeoImplicitCurve curve, BoundsRectangle limits) {
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		return fromFunctionNVar(functionNVar, limits);
	}

	private BernsteinPolynomial fromFunctionNVar(FunctionNVar functionNVar,
			BoundsRectangle limits) {
		Polynomial polynomial = functionNVar.getPolynomial();
		return fromPolynomial(polynomial, polynomial.degree('x'),
				polynomial.degree('y'), limits);
	}

	BernsteinPolynomial fromPolynomial(Polynomial polynomial, int degreeX, int degreeY,
			BoundsRectangle limits) {
		if (degreeX != 0 && degreeY != 0) {
			return builder2Var.build(polynomial, degreeX, degreeY, limits);
		}

		if (degreeY == 0) {
			return builder1Var.build(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, 'x', limits.getXmin(), limits.getXmax());
		}

		return builder1Var.build(coeffsFromPolynomial(polynomial, degreeY, 'y'),
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