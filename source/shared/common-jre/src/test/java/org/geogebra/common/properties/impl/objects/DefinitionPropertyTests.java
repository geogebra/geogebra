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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DefinitionPropertyTests extends BaseAppTestSetup {

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"a = 1 + 2"
	})
	public void testApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement(expression);
		assertDoesNotThrow(() ->
				new DefinitionProperty(getLocalization(), element));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"PenStroke((1, 2), (4, 3), (5, 6))",
			"Button()"
	})
	public void testNotApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement(expression);
		assertThrows(NotApplicablePropertyException.class, () ->
				new DefinitionProperty(getLocalization(), element));
	}
}
