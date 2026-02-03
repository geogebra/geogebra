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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FontSizePropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"\"abc\"",
			"Button(\"Press\")",
	})
	public void testApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() ->
				new FontSizeProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"a = Slider(-5, 5, 1)",
	})
	public void testNonApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () ->
				new FontSizeProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testChangingFontSize() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		FontSizeProperty fontSizeProperty = assertDoesNotThrow(() ->
				new FontSizeProperty(getLocalization(), geoText));

		fontSizeProperty.setValue(FontSizeProperty.FontSize.EXTRA_SMALL);
		assertEquals(0.5, geoText.getFontSizeMultiplier());

		fontSizeProperty.setValue(FontSizeProperty.FontSize.EXTRA_LARGE);
		assertEquals(8.0, geoText.getFontSizeMultiplier());
	}

	@Test
	public void testCustomFontSizeValues() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		FontSizeProperty fontSizeProperty = assertDoesNotThrow(() ->
				new FontSizeProperty(getLocalization(), geoText));

		geoText.setFontSizeMultiplier(1.25);
		assertEquals(FontSizeProperty.FontSize.MEDIUM, fontSizeProperty.getValue());

		geoText.setFontSizeMultiplier(15.0);
		assertEquals(FontSizeProperty.FontSize.EXTRA_LARGE, fontSizeProperty.getValue());
	}
}
