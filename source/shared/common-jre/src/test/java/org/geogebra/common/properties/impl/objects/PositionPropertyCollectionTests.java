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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PositionPropertyCollectionTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@Test
	public void testPropertyAvailabilityForImage() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		PositionPropertyCollection positionPropertyCollection = assertDoesNotThrow(() ->
				new PositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoImage)));
		assertNotNull(positionPropertyCollection.getAbsoluteScreenPositionPropertyCollection());
		assertNull(positionPropertyCollection.getStartingPointPositionProperty());
		assertNotNull(positionPropertyCollection.getCornerPositionProperties());
		assertNotNull(positionPropertyCollection.getCenterImagePositionProperty());
	}

	@Test
	public void testPropertyVisibilityForImage() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoImage geoImage = new GeoImage(getKernel().getConstruction());
		PositionPropertyCollection positionPropertyCollection = assertDoesNotThrow(() ->
				new PositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoImage)));

		PropertyCollection<StringProperty> absoluteScreenPositionPropertyCollection =
				positionPropertyCollection.getAbsoluteScreenPositionPropertyCollection();
		List<StringPropertyWithSuggestions> cornerPositionProperties = positionPropertyCollection
				.getCornerPositionProperties();

		positionPropertyCollection.getPlacementProperty().setValue(ABSOLUTE_POSITION_ON_SCREEN);
		assertTrue(absoluteScreenPositionPropertyCollection.getProperties()[0].isAvailable());
		assertTrue(absoluteScreenPositionPropertyCollection.getProperties()[1].isAvailable());
		assertFalse(cornerPositionProperties.stream().anyMatch(Property::isAvailable));
		assertFalse(positionPropertyCollection.getCenterImagePositionProperty().isAvailable());

		positionPropertyCollection.getPlacementProperty().setValue(CORNERS);
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[0].isAvailable());
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[1].isAvailable());
		assertTrue(cornerPositionProperties.stream().anyMatch(Property::isAvailable));
		assertFalse(positionPropertyCollection.getCenterImagePositionProperty().isAvailable());

		positionPropertyCollection.getPlacementProperty().setValue(CENTER_IMAGE);
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[0].isAvailable());
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[1].isAvailable());
		assertFalse(cornerPositionProperties.stream().anyMatch(Property::isAvailable));
		assertTrue(positionPropertyCollection.getCenterImagePositionProperty().isAvailable());
	}

	@Test
	public void testPropertyVisibilityForSlider() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(-5, 5, 1)");
		PositionPropertyCollection positionPropertyCollection = assertDoesNotThrow(() ->
				new PositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(slider)));

		PropertyCollection<StringProperty> absoluteScreenPositionPropertyCollection =
				positionPropertyCollection.getAbsoluteScreenPositionPropertyCollection();

		positionPropertyCollection.getPlacementProperty().setValue(ABSOLUTE_POSITION_ON_SCREEN);
		assertTrue(absoluteScreenPositionPropertyCollection.getProperties()[0].isAvailable());
		assertTrue(absoluteScreenPositionPropertyCollection.getProperties()[1].isAvailable());
		assertFalse(positionPropertyCollection.getStartingPointPositionProperty().isAvailable());

		positionPropertyCollection.getPlacementProperty().setValue(STARTING_POINT);
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[0].isAvailable());
		assertFalse(absoluteScreenPositionPropertyCollection.getProperties()[1].isAvailable());
		assertTrue(positionPropertyCollection.getStartingPointPositionProperty().isAvailable());
	}

	@Test
	public void testPropertyAvailabilityForBoolean() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoBoolean geoBoolean = evaluateGeoElement("true");
		PositionPropertyCollection positionPropertyCollection = assertDoesNotThrow(() ->
				new PositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoBoolean)));
		assertNotNull(positionPropertyCollection.getAbsoluteScreenPositionPropertyCollection());
		assertNull(positionPropertyCollection.getStartingPointPositionProperty());
		assertNull(positionPropertyCollection.getCornerPositionProperties());
		assertNull(positionPropertyCollection.getCenterImagePositionProperty());
	}

	@Test
	public void testPropertyAvailabilityForText() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"abc\"");
		PositionPropertyCollection positionPropertyCollection = assertDoesNotThrow(() ->
				new PositionPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoText)));
		assertNotNull(positionPropertyCollection.getAbsoluteScreenPositionPropertyCollection());
		assertNotNull(positionPropertyCollection.getStartingPointPositionProperty());
		assertNull(positionPropertyCollection.getCornerPositionProperties());
		assertNull(positionPropertyCollection.getCenterImagePositionProperty());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2",
			"1, 2",
			"x^2",
	})
	public void testPointExpressionValidationFailures(String pointExpression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertNotNull(PositionPropertyCollection.validatePointExpression(
				getKernel().getParser(), getLocalization(), pointExpression));
	}

	@Test
	public void testSuccessfulPointExpressionValidation() {
		setupApp(SuiteSubApp.GRAPHING);
		assertNull(PositionPropertyCollection.validatePointExpression(
				getKernel().getParser(), getLocalization(), "(1, 2)"));
	}
}
