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
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TextColorPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"\"abc\"",
			"Button(\"Press\")",
	})
	public void testApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() ->
				new TextColorProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"a = Slider(-5, 5, 1)",
	})
	public void testNonApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () ->
				new TextColorProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testChangingTextColor() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		TextColorProperty textColorProperty = assertDoesNotThrow(() ->
				new TextColorProperty(getLocalization(), geoText));

		textColorProperty.setValue(GColor.BLACK);
		assertEquals(GColor.BLACK, textColorProperty.getValue());
		assertEquals(GColor.BLACK, geoText.getObjectColor());

		textColorProperty.setValue(GColor.newColorRGB(0x12AB34));
		assertEquals(GColor.newColorRGB(0x12AB34), textColorProperty.getValue());
		assertEquals(GColor.newColorRGB(0x12AB34), geoText.getObjectColor());

		geoText.setObjColor(GColor.YELLOW);
		assertEquals(GColor.YELLOW, textColorProperty.getValue());
		assertEquals(GColor.YELLOW, geoText.getObjectColor());
	}
}
