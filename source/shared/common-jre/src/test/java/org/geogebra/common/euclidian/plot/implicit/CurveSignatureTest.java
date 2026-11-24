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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CurveSignatureTest extends BaseAppTestSetup {
	@BeforeEach
	void setUp() {
		super.setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void signatureMustChangeWithVariablesFromIneq() {
		evaluateGeoElement("a = 2");
		evaluateGeoElement("b = 1");
		GeoFunctionNVar fun = evaluateGeoElement("a*x^3+b*y^4 <= 0");
		shouldSignatureChange(new CurveSignature(fun.getIneqs().get(0)));
	}

	@Test
	void signatureMustChangeWithVariablesFromCurve() {
		evaluateGeoElement("a = 2");
		evaluateGeoElement("b = 1");
		GeoImplicitCurve curve = evaluateGeoElement("a*x^3+b*y^4 = 0");
		shouldSignatureChange(new CurveSignature(curve));
	}

	@Test
	void signatureMustChangeWithVariableOnTheRight() {
		evaluateGeoElement("a = 2");
		GeoImplicitCurve curve = evaluateGeoElement("x^3+y^4 = a");
		shouldSignatureChange(new CurveSignature(curve));
	}

	@Test
	void inequalitySignatureMustChangeWithVariableOnTheRight() {
		evaluateGeoElement("a = 2");
		GeoFunctionNVar functionNVar = evaluateGeoElement("x^3+y^4 < a");
		shouldSignatureChange(
				new CurveSignature(functionNVar.getIneqs().get(0).getImplicitCurveBorder()));
	}

	@Issue("APPS-8035")
	@ParameterizedTest
	@ValueSource(strings = {"a^2*x^4+y^4<5", "x^4+y^4<a^2"})
	void inequalitySignaturesIgnoreInputsWhenCoefficientsAreUnchanged(String expression) {
		evaluate("a = 10");
		GeoFunctionNVar function = evaluateGeoElement("c: " + expression);
		CurveSignature inequalitySignature = new CurveSignature(function.getIneqs().get(0));
		CurveSignature borderSignature =
				new CurveSignature(function.getIneqs().get(0).getImplicitCurveBorder());

		evaluate("SetValue(a, -10)");
		assertFalse(inequalitySignature.isOutdated());
		assertFalse(borderSignature.isOutdated());

		evaluate("SetValue(a, 20)");
		assertTrue(inequalitySignature.isOutdated());
		assertTrue(borderSignature.isOutdated());
		inequalitySignature.update();
		borderSignature.update();

		evaluate("SetValue(a, -20)");
		assertFalse(inequalitySignature.isOutdated());
		assertFalse(borderSignature.isOutdated());
	}

	@Issue("APPS-8035")
	@ParameterizedTest
	@ValueSource(strings = {"a^2*x^4+y^4=5", "x^4+y^4=a^2"})
	void curveSignaturesIgnoreInputsWhenCoefficientsAreUnchanged(String expression) {
		evaluate("a = 10");
		GeoImplicitCurve curve = evaluateGeoElement(expression);
		CurveSignature signature = new CurveSignature(curve);

		evaluate("SetValue(a, -10)");
		assertFalse(signature.isOutdated());

		evaluate("SetValue(a, 20)");
		assertTrue(signature.isOutdated());
		signature.update();

		evaluate("SetValue(a, -20)");
		assertFalse(signature.isOutdated());
	}

	@Issue("APPS-8035")
	@Test
	void fillerSkipsReplotWhenCoefficientsAreUnchanged() {
		evaluate("a = 10");
		GeoFunctionNVar function = evaluateGeoElement("c: a^2*x^4+y^4<5");
		EuclidianView view = getApp().getActiveEuclidianView();
		BernsteinCurveFiller filler = spy(new BernsteinCurveFiller(
				function.getIneqs().get(0), view, new GeneralPathClippedForCurvePlotter(view)));
		filler.update();
		clearInvocations(filler);

		evaluate("SetValue(a, -10)");
		assertTrue(filler.matchesBorder(function.getIneqs().get(0).getImplicitCurveBorder()));
		filler.update();
		verify(filler, never()).doUpdate();

		evaluate("SetValue(a, 20)");
		assertFalse(filler.matchesBorder(function.getIneqs().get(0).getImplicitCurveBorder()));
		filler.update();
		verify(filler).doUpdate();
	}

	@Issue("APPS-8035")
	@ParameterizedTest
	@ValueSource(strings = {"a*x^4+a*y^4<5", "a*x^4+y^4<a"})
	void inequalitySignaturesDetectSimultaneousCoefficientSignChanges(String expression) {
		evaluate("a = 1");
		GeoFunctionNVar function = evaluateGeoElement("c: " + expression);
		CurveSignature inequalitySignature = new CurveSignature(function.getIneqs().get(0));
		CurveSignature borderSignature =
				new CurveSignature(function.getIneqs().get(0).getImplicitCurveBorder());

		for (int value : new int[] {-1, 1}) {
			evaluate("SetValue(a, " + value + ")");
			assertTrue(inequalitySignature.isOutdated());
			assertTrue(borderSignature.isOutdated());
			inequalitySignature.update();
			borderSignature.update();
			assertFalse(inequalitySignature.isOutdated());
			assertFalse(borderSignature.isOutdated());
		}
	}

	@Issue("APPS-8035")
	@ParameterizedTest
	@ValueSource(strings = {"a*x^4+a*y^4=5", "a*x^4+y^4=a"})
	void curveSignaturesDetectSimultaneousCoefficientSignChanges(String expression) {
		evaluate("a = 1");
		GeoImplicitCurve curve = evaluateGeoElement(expression);
		CurveSignature signature = new CurveSignature(curve);

		for (int value : new int[] {-1, 1}) {
			evaluate("SetValue(a, " + value + ")");
			assertTrue(signature.isOutdated());
			signature.update();
			assertFalse(signature.isOutdated());
		}
	}

	@Issue("APPS-8035")
	@Test
	void fillerRefreshesAfterSimultaneousCoefficientSignChanges() {
		evaluate("a = 1");
		GeoFunctionNVar function = evaluateGeoElement("c: a*x^4+a*y^4<5");
		EuclidianView view = getApp().getActiveEuclidianView();
		BernsteinCurveFiller filler = new BernsteinCurveFiller(
				function.getIneqs().get(0), view, new GeneralPathClippedForCurvePlotter(view));
		double x = view.toScreenCoordXd(3);
		double y = view.toScreenCoordYd(0);
		filler.update();
		assertFalse(filler.getArea().contains(x, y));

		evaluate("SetValue(a, -1)");
		filler.update();
		assertTrue(filler.getArea().contains(x, y));

		evaluate("SetValue(a, 1)");
		filler.update();
		assertFalse(filler.getArea().contains(x, y));
	}

	@Issue("APPS-8035")
	@Test
	void snapshotDetectsInPlaceChangesWithoutRounding() {
		GeoImplicitCurve curve = evaluateGeoElement("x^4+y^4=5");
		CurveSignature signature = new CurveSignature(curve);
		double[][] coefficients = curve.getCoeff();
		double initial = coefficients[4][0];

		coefficients[4][0] = Math.nextUp(initial);
		assertTrue(signature.isOutdated());
		signature.update();
		assertFalse(signature.isOutdated());

		coefficients[4][0] = initial;
		assertTrue(signature.isOutdated());
	}

	@Issue("APPS-8035")
	@Test
	void snapshotTracksMissingAndResizedCoefficients() {
		GeoImplicit curve = mock(GeoImplicit.class);
		double[][] coefficients = {{1}, null};
		when(curve.getCoeff()).thenReturn(coefficients);
		CurveSignature signature = new CurveSignature(curve);
		assertFalse(signature.isOutdated());

		coefficients[1] = new double[] {2};
		assertTrue(signature.isOutdated());
		signature.update();
		assertFalse(signature.isOutdated());

		coefficients[0] = new double[] {1, 0};
		assertTrue(signature.isOutdated());
		signature.update();
		when(curve.getCoeff()).thenReturn(new double[][] {{1, 0}, {2}});
		assertFalse(signature.isOutdated());

		when(curve.getCoeff()).thenReturn(new double[][] {{1, 0}, {2}, {3}});
		assertTrue(signature.isOutdated());
		signature.update();
		when(curve.getCoeff()).thenReturn((double[][]) null);
		assertTrue(signature.isOutdated());
		signature.update();
		assertFalse(signature.isOutdated());
		when(curve.getCoeff()).thenReturn(coefficients);
		assertTrue(signature.isOutdated());
	}

	@Issue("APPS-8035")
	@Test
	void unchangedNaNsAndSignedZerosDoNotInvalidateSnapshot() {
		GeoImplicit curve = mock(GeoImplicit.class);
		double[][] coefficients = {{Double.NaN, 0}};
		when(curve.getCoeff()).thenReturn(coefficients);
		CurveSignature signature = new CurveSignature(curve);
		assertFalse(signature.isOutdated());

		coefficients[0][1] = -0.0;
		assertFalse(signature.isOutdated());
		coefficients[0][0] = 1;
		assertTrue(signature.isOutdated());
	}

	@Issue("APPS-8035")
	@Test
	void changingInequalityOperationInvalidatesSnapshot() {
		GeoImplicitCurve curve = evaluateGeoElement("x^4+y^4=5");
		Inequality inequality = mock(Inequality.class);
		when(inequality.getImplicitCurveBorder()).thenReturn(curve);
		when(inequality.getOperation()).thenReturn(Operation.LESS);
		CurveSignature signature = new CurveSignature(inequality);

		when(inequality.getOperation()).thenReturn(Operation.LESS_EQUAL);
		assertTrue(signature.isOutdated());
		signature.update();
		assertFalse(signature.isOutdated());
	}

	private void shouldSignatureChange(CurveSignature signature) {
		evaluate("SetValue(a, 20)");
		assertTrue(signature.isOutdated(), "Signature is the same ");
	}
}
