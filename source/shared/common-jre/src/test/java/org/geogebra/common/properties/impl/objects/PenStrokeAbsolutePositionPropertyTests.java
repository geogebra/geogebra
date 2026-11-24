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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PenStrokeAbsolutePositionPropertyTests extends BaseAppTestSetup {

	@Test
	void pinningAndUnpinningPreservesSelection() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement stroke = evaluateGeoElement("stroke = PenStroke((1, 2), (4, 3), (5, 6))");
		getApp().getSelectionManager().addSelectedGeo(stroke);
		PenStrokeAbsolutePositionProperty property =
				new PenStrokeAbsolutePositionProperty(getLocalization(), stroke);
		property.setValue(true);

		assertTrue(getApp().getSelectionManager().containsSelectedGeo(lookup("stroke")));

		property.setValue(false);

		assertTrue(getApp().getSelectionManager().containsSelectedGeo(lookup("stroke")));
	}

	@ParameterizedTest
	@ValueSource(strings = {"PenStroke((1, 2), (4, 3), (5, 6))"})
	void testApplicable(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement(expression);
		assertDoesNotThrow(() -> new PenStrokeAbsolutePositionProperty(getLocalization(), element));
	}

	@ParameterizedTest
	@ValueSource(
			strings = {"(1, 2)", "f(x) = x^2", "Slider(-5, 5, 1)", "\"abc\"", "Button()", "true"})
	void testNotApplicable(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement(expression);
		assertThrows(
				NotApplicablePropertyException.class,
				() -> new PenStrokeAbsolutePositionProperty(getLocalization(), element));
	}
}
