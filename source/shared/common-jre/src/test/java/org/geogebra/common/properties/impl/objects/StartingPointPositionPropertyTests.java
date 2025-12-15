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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StartingPointPositionPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"Slider(-5, 5, 1)",
			"\"abc\"",
	})
	public void testApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new StartingPointPositionProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"true",
			"(1, 2)",
	})
	public void testNotApplicableObjects(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new StartingPointPositionProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testSettingCustomStartingPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		StartingPointPositionProperty startingPointPositionProperty = assertDoesNotThrow(() ->
				new StartingPointPositionProperty(getLocalization(), geoText));

		startingPointPositionProperty.setValue("(1, 2)");
		assertEquals("(1, 2)", startingPointPositionProperty.getValue());
		assertEquals(1.0, ((GeoPoint) geoText.getStartPoint()).getX());
		assertEquals(2.0, ((GeoPoint) geoText.getStartPoint()).getY());
	}

	@Test
	public void testSettingSuggestedStartingPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		StartingPointPositionProperty startingPointPositionProperty = assertDoesNotThrow(() ->
				new StartingPointPositionProperty(getLocalization(), geoText));
		evaluateGeoElement("A = (1, 2)");

		startingPointPositionProperty.setValue("A");
		assertEquals("A", startingPointPositionProperty.getValue());
		assertEquals(1.0, ((GeoPoint) geoText.getStartPoint()).getX());
		assertEquals(2.0, ((GeoPoint) geoText.getStartPoint()).getY());
	}
}
