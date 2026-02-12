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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TextStylePropertyTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@ParameterizedTest
	@ValueSource(strings = {
			"\"abc\"",
			"Button(\"Press\")",
	})
	public void testApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new TextStyleProperty(
				propertiesFactory, getLocalization(), List.of(evaluateGeoElement(expression))));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"a = Slider(-5, 5, 1)",
	})
	public void testNonApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new TextStyleProperty(
				propertiesFactory, getLocalization(), List.of(evaluateGeoElement(expression))));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a = InputBox()",
	})
	public void testInputBoxHasOnlySerifTextStyle(String expression)
			throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new TextStyleProperty(
				propertiesFactory, getLocalization(), List.of(evaluateGeoElement(expression))));

		TextStyleProperty textStyleProperty = new TextStyleProperty(
			propertiesFactory, getLocalization(), List.of(evaluateGeoElement(expression)));
		assertEquals(1, textStyleProperty.getProperties().length);
		assertEquals("Serif", textStyleProperty.getProperties()[0].getName());
	}

	@Test
	public void testInlineHasUnderlineTextStyle() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoInlineTable table = new GeoInlineTable(getKernel().getConstruction(), new GPoint2D());
		TextStyleProperty textStyleProperty = new TextStyleProperty(
				propertiesFactory, getLocalization(), List.of(table));
		assertEquals(3, textStyleProperty.getProperties().length);
		assertEquals("Underline", textStyleProperty.getProperties()[2].getName());
	}

	@Test
	public void testTogglingValues() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		TextStyleProperty textStyleProperty = assertDoesNotThrow(() -> new TextStyleProperty(
				propertiesFactory, getLocalization(), List.of(geoText)));

		// Bold -> ON
		textStyleProperty.getProperties()[0].setValue(true);
		assertTrue(textStyleProperty.getProperties()[0].getValue());
		assertEquals(0b01, geoText.getFontStyle());

		// Italic -> ON
		textStyleProperty.getProperties()[1].setValue(true);
		assertTrue(textStyleProperty.getProperties()[1].getValue());
		assertEquals(0b11, geoText.getFontStyle());

		// Serif -> ON
		textStyleProperty.getProperties()[2].setValue(true);
		assertTrue(textStyleProperty.getProperties()[2].getValue());
		assertTrue(geoText.isSerifFont());

		// Bold -> OFF
		textStyleProperty.getProperties()[0].setValue(false);
		assertFalse(textStyleProperty.getProperties()[0].getValue());
		assertEquals(0b10, geoText.getFontStyle());

		// Italic -> OFF
		textStyleProperty.getProperties()[1].setValue(false);
		assertFalse(textStyleProperty.getProperties()[1].getValue());
		assertEquals(0b00, geoText.getFontStyle());

		// Serif -> OFF
		textStyleProperty.getProperties()[2].setValue(false);
		assertFalse(textStyleProperty.getProperties()[2].getValue());
		assertFalse(geoText.isSerifFont());
	}
}
