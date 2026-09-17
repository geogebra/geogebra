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

import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotterSettings;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Term;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Converts supported GeoGebra elements to Bernstein polynomial form.
 * <p>
 * Produces {@link BernsteinPolynomial1D} for effectively univariate inputs and
 * {@link BernsteinPolynomial2D} for bivariate inputs, using the supplied bounds
 * as the target domain.
 */
public class BernsteinPolynomialConverter {

	public static final int MAX_SUPPORTED_DEGREES = 10;
	private final BernsteinBuilder1Var builder1D = new BernsteinBuilder1Var();
	private final BernsteinBuilder2Var builder2D;

	/** Creates a converter that uses the default 1D and 2D builders. */
	public BernsteinPolynomialConverter() {
		builder2D = new BernsteinBuilder2Var(builder1D);
	}

	/**
	 * Attempts to create a {@link BernsteinPolynomial1D} from the given element.
	 * <p>
	 * Supports:
	 * <ul>
	 *   <li>{@link GeoFunction} with a polynomial definition, and</li>
	 *   <li>{@link GeoImplicitCurve} whose function reduces to a univariate polynomial.</li>
	 * </ul>
	 *
	 * @param geo    element to convert
	 * @param limits axis-aligned domain used for the resulting 1D polynomial
	 * @return a 1D Bernstein polynomial, or {@code null} if the element is not convertible
	 */
	public BernsteinPolynomial1D bernsteinPolynomial1DFrom(GeoElement geo, BoundsRectangle limits) {
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
			return from1DPolynomial(polynomial, polynomial.degree('x'), polynomial.degree('y'), limits);
		}
		return null;
	}

	/**
	 * Attempts to create a {@link BernsteinPolynomial2D} from the given element.
	 * <p>
	 * Supports:
	 * <ul>
	 *   <li>{@link GeoFunctionNVar} with a polynomial definition, and</li>
	 *   <li>{@link GeoImplicitCurve} whose defining function is polynomial.</li>
	 * </ul>
	 *
	 * @param geo    element to convert
	 * @param limits rectangular domain for the resulting 2D polynomial
	 * @return a 2D Bernstein polynomial, or {@code null} if the element is not convertible
	 */
	public BernsteinPolynomial2D bernsteinPolynomial2DFrom(GeoElement geo, BoundsRectangle limits) {
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
				? builder2D.build(
						Arrays.copyOf(coefficients, coefficients.length),
						coefficients.length - 1,
						coefficients[0].length - 1,
						limits)
				: buildFromFunction(function, limits);
	}

	private BernsteinPolynomial2D buildFromFunction(FunctionNVar function, BoundsRectangle limits) {
		if (function == null) {
			return null;
		}
		Polynomial polynomial = function.getPolynomial();
		return builder2D.build(polynomial, polynomial.degree('x'), polynomial.degree('y'), limits);
	}

	/**
	 * Builds a 1D Bernstein polynomial from a polynomial that varies in exactly one variable.
	 * <p>
	 * If {@code degreeY == 0} the x-axis limits are used; otherwise the y-axis limits are used.
	 *
	 * @param polynomial source polynomial
	 * @param degreeX    degree in x
	 * @param degreeY    degree in y
	 * @param limits     domain bounds for the active axis
	 * @return a 1D Bernstein polynomial along the active axis
	 */
	BernsteinPolynomial1D from1DPolynomial(
			Polynomial polynomial, int degreeX, int degreeY, BoundsRectangle limits) {
		if (degreeY == 0) {
			return builder1D.build(
					coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX,
					'x',
					limits.getXmin(),
					limits.getXmax());
		}

		return builder1D.build(
				coeffsFromPolynomial(polynomial, degreeY, 'y'),
				degreeY,
				'y',
				limits.getYmin(),
				limits.getYmax());
	}

	/**
	 * Returns the coefficients of {@code variableName^k} for {@code k = 0..degree}.
	 * <p>
	 * Missing terms are treated as zero.
	 *
	 * @param polynomial   source polynomial
	 * @param degree       maximum power to extract
	 * @param variableName variable to extract (e.g., {@code 'x'} or {@code 'y'})
	 * @return an array where {@code coeffs[k]} is the coefficient of {@code variableName^k}
	 */
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
	 * Reports whether the element can be converted to a Bernstein polynomial.
	 *
	 * @param geo element to check
	 * @return {@code true} if the element exposes a suitable polynomial; otherwise {@code false}
	 */
	public static boolean isSupported(GeoElementND geo) {
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

	/**
	 * Reports whether the curve is defined and polynomial, hence convertible.
	 *
	 * @param curve implicit curve to check
	 * @return {@code true} if convertible; otherwise {@code false}
	 */
	private static boolean isCurveSupported(GeoImplicitCurve curve) {
		if (!curve.isDefined()) {
			return false;
		}
		FunctionNVar functionNVar = curve.getFunctionDefinition();
		return isMultiVarPolynomial(functionNVar);
	}

	/**
	 * Reports whether the function exists and provides a polynomial.
	 *
	 * @param function multivariate function (may be {@code null})
	 * @return {@code true} if a polynomial is available; otherwise {@code false}
	 */
	private static boolean isMultiVarPolynomial(FunctionNVar function) {
		if (function == null) {
			return false;
		}

		Polynomial polynomial = function.getPolynomial();
		return isSupported(polynomial);
	}

	/**
	 * Reports whether the polynomial meets the minimal criteria used by callers.
	 *
	 * @param polynomial candidate polynomial
	 * @return {@code true} if non-null and of positive degree in {@code y}; otherwise {@code false}
	 */
	public static boolean isSupported(Polynomial polynomial) {
		if (polynomial == null) {
			return false;
		}
		int degreeX = polynomial.degree('x');
		int degreeY = polynomial.degree('y');
		return degreeX < MAX_SUPPORTED_DEGREES && degreeY > 0 && degreeY < MAX_SUPPORTED_DEGREES;
	}

	/**
	 * Converts a supported GeoElement into a Bernstein polynomial over view bounds with margin.
	 *
	 * @param geo source element to convert
	 * @param bounds current Euclidian view bounds
	 * @return converted Bernstein polynomial, or {@code null} if conversion is unsupported
	 */
	public static BernsteinPolynomial2D from(GeoElement geo, EuclidianViewBounds bounds) {
		BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
		double mx = bounds.getInvXscale() * BernsteinPlotterSettings.MARGIN_IN_PX;
		double my = bounds.getInvYscale() * BernsteinPlotterSettings.MARGIN_IN_PX;
		BoundsRectangle limits = new BoundsRectangle(bounds, mx, my);

		return converter.bernsteinPolynomial2DFrom(geo, limits);
	}

	/**
	 * Converts a supported GeoElement into a Bernstein polynomial over the exact view bounds.
	 *
	 * @param geo source element to convert
	 * @param bounds current Euclidian view bounds
	 * @return converted Bernstein polynomial, or {@code null} if conversion is unsupported
	 */
	public static BernsteinPolynomial2D from0(GeoElement geo, EuclidianViewBounds bounds) {
		BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
		BoundsRectangle limits = new BoundsRectangle(bounds, 0, 0);

		return converter.bernsteinPolynomial2DFrom(geo, limits);
	}
}
