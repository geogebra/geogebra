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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CornerPositionPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"a = 1 + 2",
			"\"abc\"",
	})
	public void testNotApplicableForAnythingOtherThanImages(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class, () -> new CornerPositionProperty(
				getLocalization(), evaluateGeoElement(expression), 0));
		assertThrows(NotApplicablePropertyException.class, () -> new CornerPositionProperty(
				getLocalization(), evaluateGeoElement(expression), 1));
		assertThrows(NotApplicablePropertyException.class, () -> new CornerPositionProperty(
				getLocalization(), evaluateGeoElement(expression), 2));
	}

	@Test
	public void testApplicableForImages() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		assertDoesNotThrow(() -> new CornerPositionProperty(getLocalization(), geoImage, 0));
		assertDoesNotThrow(() -> new CornerPositionProperty(getLocalization(), geoImage, 1));
		assertDoesNotThrow(() -> new CornerPositionProperty(getLocalization(), geoImage, 2));
	}

	@Test
	public void testSettingCustomCornerPoints() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		CornerPositionProperty cornerPositionProperty = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 0));

		cornerPositionProperty.setValue("(1, 2)");
		assertEquals("(1, 2)", cornerPositionProperty.getValue());
		assertEquals(1.0, geoImage.getStartPoint(0).getX(), 0.001);
		assertEquals(2.0, geoImage.getStartPoint(0).getY(), 0.001);

		cornerPositionProperty.setValue("(3, -4)");
		assertEquals("(3, -4)", cornerPositionProperty.getValue());
		assertEquals(3.0, geoImage.getStartPoint(0).getX(), 0.001);
		assertEquals(-4.0, geoImage.getStartPoint(0).getY(), 0.001);
	}

	@Test
	public void testSettingSuggestedCornerPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		CornerPositionProperty cornerPositionProperty = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 0));
		evaluate("P = (1, 2)");

		cornerPositionProperty.setValue("P");
		assertEquals("P", cornerPositionProperty.getValue());
		assertEquals(1.0, geoImage.getStartPoint(0).getX(), 0.001);
		assertEquals(2.0, geoImage.getStartPoint(0).getY(), 0.001);
	}

	@Test
	public void testDefaultUnsetFourthCornerPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		CornerPositionProperty cornerPositionProperty = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 2));
		assertNull(cornerPositionProperty.getValue());
	}

	@Test
	public void testUnsettingCornerPoints() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		CornerPositionProperty cornerPositionProperty1 = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 0));
		CornerPositionProperty cornerPositionProperty2 = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 1));
		CornerPositionProperty cornerPositionProperty4 = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 2));

		cornerPositionProperty1.setValue("(0, 0)");
		cornerPositionProperty2.setValue("(0, 5)");
		cornerPositionProperty4.setValue("(3, 5)");
		assertNotNull(cornerPositionProperty1);
		assertNotNull(cornerPositionProperty2);
		assertNotNull(cornerPositionProperty4);

		cornerPositionProperty1.setValue("");
		cornerPositionProperty2.setValue("");
		cornerPositionProperty4.setValue("");
		assertNotNull(cornerPositionProperty1.getValue());
		assertNull(cornerPositionProperty2.getValue());
		assertNull(cornerPositionProperty4.getValue());
	}

	@Test
	public void testCornerPointSuggestions() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		geoImage.setCentered(true);
		CornerPositionProperty cornerPositionProperty = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 0));

		evaluate("A = (1, 2)");
		evaluate("B = (3, 4)");
		evaluate("C = (5, 6)");
		assertEquals(List.of("A", "B", "C"), cornerPositionProperty.getSuggestions());
	}

	@Test
	public void testDynamicValuesWithAnyObjectThatEvaluatesToNumber() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint pointA = evaluateGeoElement("A = (200, 0)");
		GeoPoint pointB = evaluateGeoElement("B = (0, 500)");
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		CornerPositionProperty cornerPositionProperty = assertDoesNotThrow(() ->
				new CornerPositionProperty(getLocalization(), geoImage, 0));

		cornerPositionProperty.setValue("(x(A), y(B))");
		assertEquals("(x(A), y(B))", cornerPositionProperty.getValue());
		assertEquals(200.0, geoImage.getStartPoint().getX());
		assertEquals(500.0, geoImage.getStartPoint().getY());

		editGeoElement(pointA, "A = (300, 0)");
		editGeoElement(pointB, "B = (0, 400)");
		assertEquals("(x(A), y(B))", cornerPositionProperty.getValue());
		assertEquals(300.0, geoImage.getStartPoint().getX());
		assertEquals(400.0, geoImage.getStartPoint().getY());

	}
}
