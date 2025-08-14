package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuadraticEquationFormPropertyTests extends BaseAppTestSetup {
	@BeforeEach
	public void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testQuadraticEquationFormPropertyForSimpleEquation() {
		QuadraticEquationFormProperty quadraticEquationFormProperty = assertDoesNotThrow(() ->
				new QuadraticEquationFormProperty(getApp().getLocalization(),
						evaluateGeoElement("x^2 = 0")));
		assertEquals(List.of(
				QuadraticEquationRepresentable.Form.SPECIFIC.rawValue,
				QuadraticEquationRepresentable.Form.USER.rawValue,
				QuadraticEquationRepresentable.Form.IMPLICIT.rawValue,
				QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue
		), quadraticEquationFormProperty.getValues());
	}

	@Test
	public void testQuadraticEquationFormPropertyForEmptyList() {
		assertDoesNotThrow(() -> new QuadraticEquationFormProperty(
				getApp().getLocalization(), evaluateGeoElement("{}")));
	}

	@Test
	public void testQuadraticEquationFormPropertyForListOfEquations() {
		QuadraticEquationFormProperty quadraticEquationFormProperty = assertDoesNotThrow(
				() -> new QuadraticEquationFormProperty(getApp().getLocalization(),
						evaluateGeoElement("{x^2 = 0, x^2 + y^2 = 4}")));
		assertEquals(List.of(
				QuadraticEquationRepresentable.Form.SPECIFIC.rawValue,
				QuadraticEquationRepresentable.Form.USER.rawValue,
				QuadraticEquationRepresentable.Form.IMPLICIT.rawValue,
				QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue
		), quadraticEquationFormProperty.getValues());
	}

	@Test
	public void testChangingQuadraticEquationFormPropertyForSimpleEquation() {
		QuadraticEquationFormProperty quadraticEquationFormProperty = assertDoesNotThrow(() ->
				new QuadraticEquationFormProperty(getApp().getLocalization(),
						evaluateGeoElement("x^2 = 0")));
		assertEquals(QuadraticEquationRepresentable.Form.USER.rawValue,
				quadraticEquationFormProperty.getValue());
		quadraticEquationFormProperty.setValue(
				QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue);
		assertEquals(QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue,
				quadraticEquationFormProperty.getValue());
	}

	@Test
	public void testChangingQuadraticEquationFormPropertyForListOfEquations() {
		QuadraticEquationFormProperty quadraticEquationFormProperty = assertDoesNotThrow(() ->
				new QuadraticEquationFormProperty(getApp().getLocalization(),
						evaluateGeoElement("{x^2 = 0, x^2 + y^2 = 5}")));
		assertEquals(QuadraticEquationRepresentable.Form.USER.rawValue,
				quadraticEquationFormProperty.getValue());
		quadraticEquationFormProperty.setValue(
				QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue);
		assertEquals(QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue,
				quadraticEquationFormProperty.getValue());
	}
}
