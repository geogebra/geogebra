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

package org.geogebra.common.kernel.arithmetic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Regression coverage for updating implicit inequality borders in place. */
@Issue("APPS-8035")
class ImplicitInequalityTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changingMultiplierRefreshesBorder(boolean sliderUpdate) {
		GeoNumeric parameter = evaluateGeoElement("a = 10");
		GeoFunctionNVar inequality = evaluateGeoElement("c: a*x^4+y^4<5");
		GeoImplicitCurve border = inequality.getIneqs().get(0).getImplicitCurveBorder();
		assertCoefficients(border, -10, 5);

		for (double value : new double[] {20, 2, 10}) {
			changeParameter(parameter, value, sliderUpdate);
			assertSame(border, inequality.getIneqs().get(0).getImplicitCurveBorder());
			assertCoefficients(border, -value, 5);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changingRightHandSideRefreshesBorder(boolean sliderUpdate) {
		GeoNumeric parameter = evaluateGeoElement("a = 10");
		GeoFunctionNVar inequality = evaluateGeoElement("c: x^4+y^4<a");
		GeoImplicitCurve border = inequality.getIneqs().get(0).getImplicitCurveBorder();
		assertCoefficients(border, -1, 10);

		for (double value : new double[] {20, 2, 10}) {
			changeParameter(parameter, value, sliderUpdate);
			assertSame(border, inequality.getIneqs().get(0).getImplicitCurveBorder());
			assertCoefficients(border, -1, value);
		}
	}

	@ParameterizedTest
	@CsvSource({"a*x^4+y^4<5, 10", "x^4+y^4<a, 10", "a^2*x^4+y^4<5, -10"})
	void unchangedCoefficientsPreserveBorderData(String expression, double value) {
		evaluate("a = 10");
		GeoFunctionNVar inequality = evaluateGeoElement("c: " + expression);
		GeoImplicitCurve border = inequality.getIneqs().get(0).getImplicitCurveBorder();
		FunctionNVar function = border.getFunctionDefinition();
		double[][] coefficients = border.getCoeff();

		evaluate("SetValue(a, " + value + ")");
		// Exercise a full inequality update even if setting the same value is optimized upstream.
		inequality.update();

		assertSame(border, inequality.getIneqs().get(0).getImplicitCurveBorder());
		assertSame(function, border.getFunctionDefinition());
		assertSame(coefficients, border.getCoeff());
	}

	@Test
	void smallCoefficientChangesStillRefreshBorder() {
		GeoNumeric parameter = evaluateGeoElement("a = 10");
		GeoFunctionNVar inequality = evaluateGeoElement("c: a*x^4+y^4<5");
		GeoImplicitCurve border = inequality.getIneqs().get(0).getImplicitCurveBorder();
		double value = Math.nextUp(10.0);

		changeParameter(parameter, value, true);

		assertSame(border, inequality.getIneqs().get(0).getImplicitCurveBorder());
		assertEquals(-value, border.getCoeff()[4][0]);
	}

	@Test
	void appearingAndDisappearingTermsRefreshBorder() {
		GeoNumeric parameter = evaluateGeoElement("a = 0");
		GeoFunctionNVar inequality = evaluateGeoElement("c: a*x^5+x^4+y^4<5");
		GeoImplicitCurve border = inequality.getIneqs().get(0).getImplicitCurveBorder();

		for (double value : new double[] {2, 0}) {
			changeParameter(parameter, value, true);
			assertSame(border, inequality.getIneqs().get(0).getImplicitCurveBorder());
			assertEquals(5 - value - 1 - 1, border.evaluateImplicitCurve(1, 1), 1E-9);
		}
	}

	private void changeParameter(GeoNumeric parameter, double value, boolean sliderUpdate) {
		if (sliderUpdate) {
			parameter.setValue(value);
			parameter.updateRepaint();
		} else {
			evaluate("SetValue(a, " + value + ")");
		}
	}

	private void assertCoefficients(GeoImplicitCurve border, double x4, double constant) {
		double[][] coefficients = border.getCoeff();
		assertEquals(constant, coefficients[0][0], 1E-9);
		assertEquals(x4, coefficients[4][0], 1E-9);
		assertEquals(-1, coefficients[0][4], 1E-9);
		for (int x = 0; x < coefficients.length; x++) {
			for (int y = 0; y < coefficients[x].length; y++) {
				if ((x != 0 || y != 0) && (x != 4 || y != 0) && (x != 0 || y != 4)) {
					assertEquals(0, coefficients[x][y], 1E-9);
				}
			}
		}
	}
}
