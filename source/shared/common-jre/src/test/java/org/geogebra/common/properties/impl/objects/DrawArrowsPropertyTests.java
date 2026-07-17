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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DrawArrowsPropertyTests extends BaseAppTestSetup {

	@ParameterizedTest
	@ValueSource(strings = {
			"SlopeField(x + y)"
	})
	void testApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() ->
				new DrawArrowsProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"PenStroke((1, 2), (4, 3), (5, 6))",
			"Locus(x + y, (0, 0))"
	})
	void testNotApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () ->
				new DrawArrowsProperty(getLocalization(), evaluateGeoElement(expression)));
	}
}
