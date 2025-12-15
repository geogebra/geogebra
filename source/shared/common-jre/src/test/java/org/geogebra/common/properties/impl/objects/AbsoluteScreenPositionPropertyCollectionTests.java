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

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AbsoluteScreenPositionPropertyCollectionTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"a = 1 + 2",
	})
	public void testNotApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement(expression);
		assertThrows(NotApplicablePropertyException.class, () ->
				new AbsoluteScreenPositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoElement)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Slider(-5, 5, 1)",
			"true",
			"\"abc\"",
	})
	public void testApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new AbsoluteScreenPositionPropertyCollection(
				propertiesFactory, getLocalization(), List.of(evaluateGeoElement(expression))));
	}

	@Test
	public void testSettingConstantValues() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		geoText.setAbsoluteScreenLocActive(true);
		AbsoluteScreenPositionPropertyCollection absoluteScreenPositionPropertyCollection =
				assertDoesNotThrow(() -> new AbsoluteScreenPositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoText)));
		absoluteScreenPositionPropertyCollection.getProperties()[0].setValue("100");
		absoluteScreenPositionPropertyCollection.getProperties()[1].setValue("500");
		assertEquals("100", absoluteScreenPositionPropertyCollection.getProperties()[0].getValue());
		assertEquals("500", absoluteScreenPositionPropertyCollection.getProperties()[1].getValue());
		assertEquals(100, geoText.getAbsoluteScreenLocX());
		assertEquals(500, geoText.getAbsoluteScreenLocY());
	}

	@Test
	public void testSettingDynamicValuesWithSlider() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 500, 1)");
		GeoText geoText = evaluateGeoElement("\"abc\"");
		geoText.setAbsoluteScreenLocActive(true);
		AbsoluteScreenPositionPropertyCollection absoluteScreenPositionPropertyCollection =
				assertDoesNotThrow(() -> new AbsoluteScreenPositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoText)));

		absoluteScreenPositionPropertyCollection.getProperties()[0].setValue("a");
		assertEquals("a", absoluteScreenPositionPropertyCollection.getProperties()[0].getValue());
		assertEquals(0.0, ((GeoPoint) geoText.getStartPoint()).getX(), 0.001);

		slider.setValue(500.0);
		slider.updateRepaint();
		assertEquals(500.0, ((GeoPoint) geoText.getStartPoint()).getX(), 0.001);
	}

	@Test
	public void testSettingDynamicValuesWithAnyObjectThatEvaluatesToNumber() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 500, 1)");
		evaluate("A = (a, 200)");
		GeoText geoText = evaluateGeoElement("\"abc\"");
		geoText.setAbsoluteScreenLocActive(true);
		AbsoluteScreenPositionPropertyCollection absoluteScreenPositionPropertyCollection =
				assertDoesNotThrow(() -> new AbsoluteScreenPositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoText)));

		absoluteScreenPositionPropertyCollection.getProperties()[0].setValue("x(A)");
		assertEquals("x(A)", absoluteScreenPositionPropertyCollection
				.getProperties()[0].getValue());
		assertEquals(0.0, ((GeoPoint) geoText.getStartPoint()).getX(), 0.001);

		slider.setValue(500.0);
		slider.updateRepaint();
		assertEquals(500.0, ((GeoPoint) geoText.getStartPoint()).getX(), 0.001);
	}
}
