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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Term;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.util.debug.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BernsteinPolynomial1DTest extends BaseUnitTest {

	private BernsteinPolynomial1D bernstein1D;
	private GeoImplicitCurve curve;
	private EuclidianView view;
	private BernsteinPolynomialConverter converter;

	@BeforeEach
	void setUp() {
		add("ZoomIn(0,0,1,1)");
		view = getApp().getEuclidianView1();
		converter = new BernsteinPolynomialConverter();
	}

	private void newBernsteinPolynomialPolynomialFrom(String definition) {
		GeoElement geo = add(definition);
		if (geo.isGeoImplicitCurve()) {
			curve = (GeoImplicitCurve) geo;
		}
		bernstein1D = bernsteinPolynomial1DFrom(geo, new BoundsRectangle(0, 1, 0, 1));
	}

	@Test
	void testEvaluatingBernsteinForm() {
		shouldEvaluateTheSame("3x^3 + 2x^2 + x - 1=0");
		shouldEvaluateTheSame("3x^3 + 2x^2 + 6x + 11=0");
		shouldEvaluateTheSame("2x^4 + 5x^3 + x^2 + x - 1=0");
		shouldEvaluateTheSame("x^3+x=0");
		shouldEvaluateTheSame("x^3+1=0");
		shouldEvaluateTheSame("x^4+5x^3=0");
	}

	private void shouldEvaluateTheSame(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		for (double v = -10.0; v < 10.0; v += 0.01) {
			assertEquals(curve.evaluate(v, 0), bernstein1D.evaluate(v), 1E-8);
		}
	}

	@Test
	void testEvaluate() {
		shouldEvaluate("3x^3 + 2x^2 + x - 1=0", 0.5);
		shouldEvaluate("3x^4 + x^2 + x - 3=0", 0.75);
	}

	private void shouldEvaluate(String definition, double v) {
		newBernsteinPolynomialPolynomialFrom(definition);
		assertEquals(curve.evaluate(v, 0), bernstein1D.evaluate(v), 1E-8);
	}

	@Test
	void testOneVariableToBernsteinPolynomial() {
		Polynomial polynomial = new Polynomial(getKernel(), "y");
		BernsteinPolynomial<?> bernsteinPolynomial = from1DPolynomial(polynomial, 0, 2,
				new BoundsRectangle(0, 1, 0, 1),
				new BernsteinBuilder1Var());
		assertEquals("y\u00B2 + y (1 - y)", bernsteinPolynomial.toString());
	}

	@Test
	void testToString() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		assertEquals("5x\u00B3 + x\u00B2 (1 - x) - 2x (1 - x)\u00B2 - (1 - x)\u00B3",
				bernstein1D.toString());
	}

	@Test
	void testBernsteinFromCoefficients() {
		bernsteinShouldBe("2x\u00B2", 0, 0, 2);
		bernsteinShouldBe("4x\u00B2 + 2x (1 - x)", 0, 2, 2);
		bernsteinShouldBe("6x\u00B2 + 6x (1 - x) + 2(1 - x)\u00B2", 2, 2, 2);
		bernsteinShouldBe("14x\u00B3 + 22x\u00B2 (1 - x) + 17x (1 - x)\u00B2"
				+ " + 5(1 - x)\u00B3", 5, 2, 3, 4);
		bernsteinShouldBe("6x + 2(1 - x)", 2, 4);
		bernsteinShouldBe("20x\u00B2 + 10x (1 - x) + 2(1 - x)\u00B2", 2, 6, 12);
	}

	private void bernsteinShouldBe(String expected, double... coeffs) {
		new1DFromCoeffs(coeffs);

		assertEquals(expected, bernstein1D.toString());
	}

	private void new1DFromCoeffs(double... coeffs) {
		BernsteinBuilder1Var builder = new BernsteinBuilder1Var();
		bernstein1D =
				builder.build(coeffs, coeffs.length - 1,
						'x', view.getXmin(), view.getXmax()
				);
	}

	@Disabled
	@Test
	void testSpit() {
		double[] bcoeffs = new double[]{2, 8, 12, 7};
		bernstein1D = new BernsteinPolynomial1D(bcoeffs, 'x' , 0, 1);
		Log.debug("Original: " + bernstein1D);
		BernsteinPolynomial1D[] splits = bernstein1D.split();
		Log.debug("splits[0]: " + splits[0]);
		Log.debug("splits[1]: " + splits[1]);

		assertEquals(bernstein1D.evaluate(0), splits[0].evaluate(0), 0);
		assertEquals(bernstein1D.evaluate(.25), splits[0].evaluate(0.5), 0);
		assertEquals(bernstein1D.evaluate(.5), splits[0].evaluate(1), 0);

		assertEquals(bernstein1D.evaluate(.5), splits[1].evaluate(0), 0);
		assertEquals(bernstein1D.evaluate(.75), splits[1].evaluate(0.5), 0);
		assertEquals(bernstein1D.evaluate(1), splits[1].evaluate(1), 0);
	}

	@Test
	void testHasNoSolution() {
		shouldHaveNoSolution(2, 8, 12, 7);
		shouldHaveNoSolution(-2, -3, -0.75, -7);
	}

	@Test
	void testMightHaveSolution() {
		mightHaveSolution(2, -8, 12, 7);
		mightHaveSolution(-1, 8, 12, 7);
		mightHaveSolution(-0.2, -8, -12, 7);
	}

	@Test
	void testSimplifiedEvaluation() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		BernsteinPolynomial1D b1var = bernstein1D;
		double expected = b1var.evaluate(1);
		double[] coeffs = b1var.dividedCoeffs;
		assertEquals(expected, coeffs[3], 0);
		assertEquals(bernstein1D.evaluate(0), coeffs[0], 0);
	}

	private void mightHaveSolution(double... bcoeffs) {
		testSolution(false, bcoeffs);
	}

	private void shouldHaveNoSolution(double... bcoeffs) {
		testSolution(true, bcoeffs);
	}

	private void testSolution(boolean shouldHave, double... bcoeffs) {
		bernstein1D = new BernsteinPolynomial1D(bcoeffs, 'x', 0, 1);
		assertEquals(shouldHave, bernstein1D.hasNoSolution());
	}

	/**
	 *
	 * @param geo to convert
	 * @param limits of the result polynomial.
	 * @return the {@link BernsteinPolynomial1D} instance if possible, null otherwise.
	 */
	BernsteinPolynomial1D bernsteinPolynomial1DFrom(GeoElement geo,
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
					polynomial.degree('y'), limits, new BernsteinBuilder1Var());
		}
		return null;
	}

	static BernsteinPolynomial1D from1DPolynomial(Polynomial polynomial, int degreeX, int degreeY,
			BoundsRectangle limits, BernsteinBuilder1Var builder1D) {
		if (degreeY == 0) {
			return builder1D.build(coeffsFromPolynomial(polynomial, degreeX, 'x'),
					degreeX, 'x', limits.getXmin(), limits.getXmax());
		}

		return builder1D.build(coeffsFromPolynomial(polynomial, degreeY, 'y'),
				degreeY, 'y', limits.getYmin(), limits.getYmax());
	}

	static double[] coeffsFromPolynomial(Polynomial polynomial, int degree, char variableName) {
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
}
