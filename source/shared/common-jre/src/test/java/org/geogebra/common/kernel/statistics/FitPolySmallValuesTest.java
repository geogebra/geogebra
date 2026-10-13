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

package org.geogebra.common.kernel.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests polynomial regression with the small coordinates from Calculation With Table.ggb. */
@Issue("APPS-7950")
class FitPolySmallValuesTest extends BaseAppTestSetup {

	// Coefficients in ascending power order, calculated with the x-values scaled to nanounits
	// and the y-values scaled to microunits, then converted back to the original units.
	private static final double[][] EXPECTED_COEFFICIENTS = {
		{1.35735294117648e-6, 33636.733416771},
		{-4.81866032290828e-6, 37561.672097872, -4.47581709559522e11},
		{-1.07220893817950e-5, 43841.0900241073, -2.19326109374907e12, 1.38127315122702e20},
		{
			-1.82834440249030e-5,
			54498.6528464981,
			-6.81685253354753e12,
			9.13300139584858e20,
			-4.41997295669889e28
		}
	};

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4})
	void fitSmallCoordinates(int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction fit = evaluateGeoElement("FitPoly({"
				+ "(1.2*10^(-9),39.1*10^(-6)),(2.5*10^(-9),86.6*10^(-6)),"
				+ "(4*10^(-9),140*10^(-6)),(6.2*10^(-9),204*10^(-6)),"
				+ "(6.9*10^(-9),241*10^(-6)),(7.4*10^(-9),246*10^(-6))},"
				+ degree + ")");

		assertTrue(fit.isDefined(), "Distinct small x-values must not be treated as singular");
		double[] actual = ((FitAlgo) fit.getParentAlgorithm()).getCoeffs();
		double[] expected = EXPECTED_COEFFICIENTS[degree - 1];
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i], Math.abs(expected[i]) * 1e-8, "Coefficient of x^" + i);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4})
	void predictionsAndCoefficientsSurviveScalingAndTranslation(int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		double[] polynomial = {-3, 2, 1, -0.5, 0.25};
		double[] reference = null;
		for (double scale : new double[] {1, 1e-9, 1e9}) {
			GeoList points = new GeoList(getConstruction());
			for (int t = -2; t <= 3; t++) {
				points.add(new GeoPoint(
						getConstruction(), scale * (t + 7), evaluatePolynomial(polynomial, degree, t), 1));
			}
			AlgoFitPoly fit =
					new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), degree));
			assertTrue(fit.getFitPoly().isDefined());
			double[] coefficients = fit.getCoeffs();
			if (reference == null) {
				reference = coefficients;
			}
			for (int i = 0; i <= degree; i++) {
				assertEquals(
						reference[i],
						coefficients[i] * Math.pow(scale, i),
						Math.max(1, Math.abs(reference[i])) * 1e-9);
			}
			for (double t : new double[] {-2, -0.5, 0, 1.5, 3}) {
				assertEquals(
						evaluatePolynomial(polynomial, degree, t),
						fit.getFitPoly().value(scale * (t + 7)),
						1e-8);
			}
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4})
	void repeatedXValuesWithEnoughDistinctValuesAreAllowed(int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoList points = new GeoList(getConstruction());
		for (int x = 0; x <= degree; x++) {
			points.add(new GeoPoint(getConstruction(), x, x * x - 1, 1));
			points.add(new GeoPoint(getConstruction(), x, x * x + 1, 1));
		}
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), degree));
		assertTrue(fit.getFitPoly().isDefined());
		for (int x = 0; x <= degree; x++) {
			assertEquals(x * x, fit.getFitPoly().value(x), 1e-10);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4, 5})
	void losingDistinctXValuesClearsPreviousCoefficients(int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoList points = new GeoList(getConstruction());
		for (int x = 0; x < 6; x++) {
			points.add(new GeoPoint(getConstruction(), x, x * x, 1));
		}
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), degree));
		assertTrue(fit.getFitPoly().isDefined());
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).setCoords(i % degree, i * i, 1);
		}
		points.updateCascade();
		assertFalse(fit.getFitPoly().isDefined());
		assertNull(fit.getCoeffs());
	}

	@Test
	void constantFitAllowsIdenticalXValues() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction fit = evaluateGeoElement("FitPoly({(1,1),(1,3),(1,5)},0)");
		assertTrue(fit.isDefined());
		assertEquals(3, fit.value(10), 1e-12);
	}

	@Test
	void nonFiniteCoordinatesAreRejected() {
		setupApp(SuiteSubApp.GRAPHING);
		for (double coordinate : new double[] {Double.NaN, Double.POSITIVE_INFINITY}) {
			for (boolean invalidX : new boolean[] {false, true}) {
				GeoList points = new GeoList(getConstruction());
				points.add(new GeoPoint(getConstruction(), 0, 0, 1));
				points.add(new GeoPoint(getConstruction(), 1, 1, 1));
				points.add(new GeoPoint(
						getConstruction(), invalidX ? coordinate : 2, invalidX ? 2 : coordinate, 1));
				AlgoFitPoly fit =
						new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), 1));
				assertFalse(fit.getFitPoly().isDefined());
				assertNull(fit.getCoeffs());
			}
		}
	}

	@Test
	void nonFiniteConvertedCoefficientsAreRejected() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoList points = new GeoList(getConstruction());
		for (int x = 0; x < 4; x++) {
			points.add(new GeoPoint(getConstruction(), x * 1e-200, x * x, 1));
		}
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), 2));
		assertFalse(fit.getFitPoly().isDefined());
		assertNull(fit.getCoeffs());
	}

	@Test
	void undefinedInputClearsPreviousCoefficients() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoList points = evaluateGeoElement("points = {(0,0),(1,1),(2,4)}");
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), 1));
		assertTrue(fit.getFitPoly().isDefined());
		points.setUndefined();
		points.updateCascade();
		assertFalse(fit.getFitPoly().isDefined());
		assertNull(fit.getCoeffs());
	}

	@Test
	void quarticFitOfConstantDataWithLargeOffsetStaysConstant() {
		setupApp(SuiteSubApp.GRAPHING);
		double center = 1e9;
		GeoList points = new GeoList(getConstruction());
		for (int i = -2; i <= 3; i++) {
			points.add(new GeoPoint(getConstruction(), center + i, 1, 1));
		}
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), 4));
		assertTrue(fit.getFitPoly().isDefined());
		for (int i = -2; i <= 3; i++) {
			assertEquals(1, fit.getFitPoly().value(center + i), 1e-12, "Prediction at x=" + (center + i));
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4})
	void nonConstantFitsStayAccurateWithLargePositiveAndNegativeOffsets(int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		double[] polynomial = {1, 0.5, -2, 0.125, -0.25};
		for (double center : new double[] {-1e9, 1e9}) {
			GeoList points = new GeoList(getConstruction());
			for (int i = -2; i <= 3; i++) {
				points.add(new GeoPoint(
						getConstruction(), center + i, evaluatePolynomial(polynomial, degree, i), 1));
			}
			AlgoFitPoly fit =
					new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), degree));
			assertTrue(fit.getFitPoly().isDefined());
			for (double x : new double[] {-2, -0.5, 0, 1.5, 3}) {
				assertEquals(
						evaluatePolynomial(polynomial, degree, x), fit.getFitPoly().value(center + x), 1e-10);
			}
		}
	}

	@Test
	void copiedAndReloadedFitRetainsStableEvaluation() {
		setupApp(SuiteSubApp.GRAPHING);
		evaluateGeoElement(
				"points = {(10^9-2,4),(10^9-1,1),(10^9,0)," + "(10^9+1,1),(10^9+2,4),(10^9+3,9)}");
		GeoFunction fit = evaluateGeoElement("fit = FitPoly(points,2)");
		GeoFunction copy = (GeoFunction) fit.copyInternal(getConstruction());
		for (double x : new double[] {-2, -0.5, 0, 1.5, 3}) {
			assertEquals(x * x, copy.value(1e9 + x), 1e-10);
		}
		assertEquals(1, this.<GeoNumeric>evaluateGeoElement("RSquare(points,fit)").getDouble(), 1e-12);
		getApp().setXML(getApp().getXML(), true);
		GeoFunction restored = (GeoFunction) lookup("fit");
		assertTrue(restored.isDefined());
		for (double x : new double[] {-2, -0.5, 0, 1.5, 3}) {
			assertEquals(x * x, restored.value(1e9 + x), 1e-10);
		}
		// Coefficients() must agree with the parameters exposed to the regression dialog.
		GeoList coefficients = evaluateGeoElement("Coefficients(fit)");
		double[] expected = ((FitAlgo) restored.getParentAlgorithm()).getCoeffs();
		assertEquals(expected.length, coefficients.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[expected.length - 1 - i], coefficients.get(i).evaluateDouble());
		}
	}

	@Test
	void updatingPointsReplacesTheNormalization() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoList points = new GeoList(getConstruction());
		for (int i = 0; i < 6; i++) {
			points.add(new GeoPoint(getConstruction(), 1e9 + i, i * i, 1));
		}
		AlgoFitPoly fit =
				new AlgoFitPoly(getConstruction(), points, new GeoNumeric(getConstruction(), 2));
		assertTrue(fit.getFitPoly().isDefined());
		assertEquals(2.25, fit.getFitPoly().value(1e9 + 1.5), 1e-10);
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).setCoords(-1e9 + i, i * i, 1);
		}
		points.updateCascade();
		assertTrue(fit.getFitPoly().isDefined());
		assertEquals(2.25, fit.getFitPoly().value(-1e9 + 1.5), 1e-10);
	}

	@Test
	void coefficientsOfZeroFitHaveConstantDegree() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction fit = evaluateGeoElement("fit = FitPoly({(0,0),(1,0),(2,0),(3,0)},2)");
		assertTrue(fit.isDefined());
		GeoList coefficients = evaluateGeoElement("Coefficients(fit)");
		assertEquals(1, coefficients.size());
		assertEquals(0, coefficients.get(0).evaluateDouble());
	}

	@ParameterizedTest
	@CsvSource({
		"1.000000005, 0", "0.999999995, 0", "-1.000000005, 0", "-0.999999995, 0",
		"1.000000005, 1", "0.999999995, 1", "-1.000000005, 1", "-0.999999995, 1",
		"1.000000005, 2", "0.999999995, 2", "-1.000000005, 2", "-0.999999995, 2"
	})
	void nearUnitCoefficientsSurviveEvaluationCopyAndReload(double coefficient, int degree) {
		setupApp(SuiteSubApp.GRAPHING);
		double[] expected = new double[degree + 1];
		Arrays.fill(expected, coefficient);
		StringBuilder points = new StringBuilder("points = {");
		for (int x = -2; x <= 3; x++) {
			if (x > -2) {
				points.append(',');
			}
			points
					.append('(')
					.append(x)
					.append(',')
					.append(evaluatePolynomial(expected, degree, x))
					.append(')');
		}
		evaluateGeoElement(points.append('}').toString());
		GeoFunction fit = evaluateGeoElement("fit = FitPoly(points," + degree + ")");
		assertTrue(fit.isDefined());
		double[] fitted = ((FitAlgo) fit.getParentAlgorithm()).getCoeffs();
		for (int i = 0; i <= degree; i++) {
			assertEquals(coefficient, fitted[i], 1e-12, "Coefficient of x^" + i);
		}
		GeoList coefficients = evaluateGeoElement("Coefficients(fit)");
		assertEquals(degree + 1, coefficients.size());
		for (int i = 0; i <= degree; i++) {
			assertEquals(fitted[degree - i], coefficients.get(i).evaluateDouble());
		}
		GeoFunction copy = (GeoFunction) fit.copyInternal(getConstruction());
		for (double x : new double[] {-2, -0.5, 0, 1.5, 3}) {
			double value = evaluatePolynomial(expected, degree, x);
			assertEquals(value, fit.value(x), 1e-12, "Prediction at x=" + x);
			assertEquals(value, copy.value(x), 1e-12, "Copied prediction at x=" + x);
		}
		getApp().setXML(getApp().getXML(), true);
		GeoFunction restored = (GeoFunction) lookup("fit");
		assertTrue(restored.isDefined());
		for (double x : new double[] {-2, -0.5, 0, 1.5, 3}) {
			assertEquals(
					evaluatePolynomial(expected, degree, x),
					restored.value(x),
					1e-12,
					"Reloaded prediction at x=" + x);
		}
	}

	private double evaluatePolynomial(double @NonNull [] coefficients, int degree, double x) {
		double value = coefficients[degree];
		for (int i = degree - 1; i >= 0; i--) {
			value = value * x + coefficients[i];
		}
		return value;
	}
}
