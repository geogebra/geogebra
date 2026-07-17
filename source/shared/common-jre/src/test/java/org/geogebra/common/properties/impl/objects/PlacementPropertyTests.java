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

import static org.geogebra.common.properties.impl.objects.PlacementProperty.Placement.ABSOLUTE_POSITION_ON_SCREEN;
import static org.geogebra.common.properties.impl.objects.PlacementProperty.Placement.CENTER_IMAGE;
import static org.geogebra.common.properties.impl.objects.PlacementProperty.Placement.CORNERS;
import static org.geogebra.common.properties.impl.objects.PlacementProperty.Placement.STARTING_POINT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PlacementPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"Vector((1, 2))",
			"β = Angle((0, 0), (1, 1), (2, 2))",
			"IF(3 > 2, \"abc\", \"edf\")",
			"BarChart({1,2,3},{4,5,6})"
	})
	void testNotApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () ->
				new PlacementProperty(getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@CsvSource(value = {
			"\"abc\": false",
			"IF(3 > 2, Text(\"abc\"), Text(\"edf\")): true",
			"IF(3 > 2, \"abc\", \"edf\"): true",
			"IF(3 > 2, Text(\"abc\", (1, 2)), Text(\"edf\", (2, 3))): true",
			"Element({Text(\"abc\"), Text(\"edf\")}, 1): true",
			"Element({Text(\"abc\", (1, 2)), Text(\"edf\", (1, 2))}, 1): true",
			"Element({\"abc\", \"edf\"}, 1): true"},
			delimiterString = ":")
	void testIsDependentTextCommand(String expression, boolean expected) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement(expression);
		assertEquals(expected, PlacementProperty.isDependentTextCommand(geo));
	}

	@Test
	void testBooleanPlacementOption() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoBoolean geoBoolean = evaluateGeoElement("true");
		PlacementProperty placementProperty = assertDoesNotThrow(() ->
				new PlacementProperty(getLocalization(), geoBoolean));
		assertEquals(List.of(ABSOLUTE_POSITION_ON_SCREEN), placementProperty.getValues());
	}

	@Test
	void testTextPlacementOptions() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		PlacementProperty placementProperty = assertDoesNotThrow(() ->
				new PlacementProperty(getLocalization(), geoText));
		assertEquals(
				List.of(ABSOLUTE_POSITION_ON_SCREEN, STARTING_POINT),
				placementProperty.getValues());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Slider(-5, 5, 1)", // number slider
			"Slider(0, 10, 0.1, 0.1, 100, true, true, false, false)" // angle slider
	})
	void testSliderPlacementOptions(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric geoNumeric = evaluateGeoElement(expression);
		PlacementProperty placementProperty = assertDoesNotThrow(() ->
				new PlacementProperty(getLocalization(), geoNumeric));
		assertEquals(
				List.of(ABSOLUTE_POSITION_ON_SCREEN, STARTING_POINT),
				placementProperty.getValues());
	}

	@Test
	void testImagePlacementOptions() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		PlacementProperty placementProperty = assertDoesNotThrow(() ->
				new PlacementProperty(getLocalization(), geoImage));
		assertEquals(
				List.of(ABSOLUTE_POSITION_ON_SCREEN, CORNERS, CENTER_IMAGE),
				placementProperty.getValues());
	}

	@Test
	void testSwitchingBetweenPlacementOptions() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		PlacementProperty placementProperty = assertDoesNotThrow(() ->
				new PlacementProperty(getLocalization(), geoImage));

		placementProperty.setValue(ABSOLUTE_POSITION_ON_SCREEN);
		assertEquals(ABSOLUTE_POSITION_ON_SCREEN, placementProperty.getValue());

		placementProperty.setValue(CORNERS);
		assertEquals(CORNERS, placementProperty.getValue());

		placementProperty.setValue(CENTER_IMAGE);
		assertEquals(CENTER_IMAGE, placementProperty.getValue());
	}
}
