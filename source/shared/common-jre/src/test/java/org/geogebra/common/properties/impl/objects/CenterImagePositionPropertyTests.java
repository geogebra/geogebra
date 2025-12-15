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

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CenterImagePositionPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"a = 1 + 2",
			"\"abc\"",
	})
	public void testNotApplicableForAnythingOtherThanImages(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new CenterImagePositionProperty(
				getLocalization(), evaluateGeoElement(expression)));
	}

	@Test
	public void testApplicableForImages() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		assertDoesNotThrow(() -> new CenterImagePositionProperty(getLocalization(), geoImage));
	}

	@Test
	public void testSettingCustomCenterPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		geoImage.setCentered(true);
		CenterImagePositionProperty centerImagePositionProperty = assertDoesNotThrow(() ->
				new CenterImagePositionProperty(getLocalization(), geoImage));

		centerImagePositionProperty.setValue("(1, 2)");
		assertEquals("(1, 2)", centerImagePositionProperty.getValue());
		assertEquals(1.0, geoImage.getStartPoint(3).getX(), 0.001);
		assertEquals(2.0, geoImage.getStartPoint(3).getY(), 0.001);

		centerImagePositionProperty.setValue("(-5, 9)");
		assertEquals("(-5, 9)", centerImagePositionProperty.getValue());
		assertEquals(-5.0, geoImage.getStartPoint(3).getX(), 0.001);
		assertEquals(9.0, geoImage.getStartPoint(3).getY(), 0.001);
	}

	@Test
	public void testCenterPointSuggestions() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		geoImage.setCentered(true);
		CenterImagePositionProperty centerImagePositionProperty = assertDoesNotThrow(() ->
				new CenterImagePositionProperty(getLocalization(), geoImage));

		evaluate("A = (1, 2)");
		evaluate("B = (3, 4)");
		evaluate("C = (5, 6)");
		assertEquals(List.of("A", "B", "C"), centerImagePositionProperty.getSuggestions());
	}

	@Test
	public void testSettingSuggestedCenterPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		geoImage.setCentered(true);
		CenterImagePositionProperty centerImagePositionProperty = assertDoesNotThrow(() ->
				new CenterImagePositionProperty(getLocalization(), geoImage));
		evaluate("A = (1, 2)");
		evaluate("B = (3, 4)");

		centerImagePositionProperty.setValue("A");
		assertEquals("A", centerImagePositionProperty.getValue());
		assertEquals(1.0, geoImage.getStartPoint(3).getX(), 0.001);
		assertEquals(2.0, geoImage.getStartPoint(3).getY(), 0.001);

		centerImagePositionProperty.setValue("B");
		assertEquals("B", centerImagePositionProperty.getValue());
		assertEquals(3.0, geoImage.getStartPoint(3).getX(), 0.001);
		assertEquals(4.0, geoImage.getStartPoint(3).getY(), 0.001);
	}
}
