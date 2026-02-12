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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ObjectColorPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"Slider(-5, 5, 1)",
			"Line((0, 0), (1, 1))",
			"Circle((0, 0), 3)",
			"Polygon({(0, 0), (1, 1), (1, 0)})",
	})
	public void testApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new ObjectColorProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"\"abc\"",
			"Button(\"Press\")",
	})
	public void testNotApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new ObjectColorProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testSettingObjectColor() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		ObjectColorProperty objectColorProperty = assertDoesNotThrow(() ->
				new ObjectColorProperty(getLocalization(), geoElement));

		objectColorProperty.setValue(GColor.BLACK);
		assertEquals(GColor.BLACK, objectColorProperty.getValue());
		assertEquals(GColor.BLACK, geoElement.getObjectColor());

		objectColorProperty.setValue(GColor.YELLOW);
		assertEquals(GColor.YELLOW, objectColorProperty.getValue());
		assertEquals(GColor.YELLOW, geoElement.getObjectColor());
	}
}
