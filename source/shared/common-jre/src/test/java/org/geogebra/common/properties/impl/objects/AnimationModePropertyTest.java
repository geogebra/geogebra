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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AnimationModePropertyTest extends BaseAppTestSetup {

	@ParameterizedTest
	@ValueSource(strings = {
			"Slider(-5, 5, 0.1, 1, 1, false, false, false, false)", // Slider
			"Point(Circle((0,0), 2))", // Point on circle
	})
	public void testSuccessfulConstruction(String input) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement(input);
		assertDoesNotThrow(() -> new AnimationModeProperty(getLocalization(), geoElement));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{1, 2, 3}", // List
			"x+y=0", // Line
			"a=5", // Simple number
	})
	public void testConstructingNotApplicableProperty(String input) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement(input);
		Assertions.assertThrows(NotApplicablePropertyException.class,
				() -> new AnimationModeProperty(getLocalization(), geoElement));
	}

	@Test
	public void testHasRandomForNumeric() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric numeric = evaluateGeoElement("a = 5");
		numeric.setEuclidianVisible(true);

		AnimationModeProperty property =
				assertDoesNotThrow(() -> new AnimationModeProperty(getLocalization(), numeric));
		assertTrue(property.getValues().contains(AnimationModeProperty.AnimationMode.RANDOM));
	}
}