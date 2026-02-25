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

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BackgroundAndBorderPropertyCollectionTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"(1, 2)",
			"f(x) = x^2",
			"a = 1 + 2",
	})
	public void testNotApplicableObjects(String expression) {
		GeoElement geoElement = evaluateGeoElement(expression);
		assertThrows(NotApplicablePropertyException.class, () ->
				new BackgroundAndBorderPropertyCollection(
						propertiesFactory, getLocalization(), List.of(geoElement)));
	}

	@Test
	public void testInlineTextBackgroundAndBorderProperties() {
		GeoInlineText inlineText = new GeoInlineText(getKernel().getConstruction(),
				new GPoint2D());
		BackgroundAndBorderPropertyCollection backgroundAndBorderPropertyCollection =
				assertDoesNotThrow(() -> new BackgroundAndBorderPropertyCollection(
						propertiesFactory, getLocalization(), List.of(inlineText)
				));
		List<String> backgroundAndBorderNames = Arrays.stream(
				backgroundAndBorderPropertyCollection.getProperties())
				.map(Property::getName).toList();
		assertEquals(List.of("Background Color", "No color", "Border color", "Border width"),
				backgroundAndBorderNames);
	}

	@Test
	public void testInlineTableBackgroundAndBorderProperties() {
		GeoInlineTable inlineTable = new GeoInlineTable(getKernel().getConstruction(),
				new GPoint2D());
		BackgroundAndBorderPropertyCollection backgroundAndBorderPropertyCollection =
				assertDoesNotThrow(() -> new BackgroundAndBorderPropertyCollection(
						propertiesFactory, getLocalization(), List.of(inlineTable)
				));
		List<String> backgroundAndBorderNames = Arrays.stream(
						backgroundAndBorderPropertyCollection.getProperties())
				.map(Property::getName).toList();
		assertEquals(List.of("Background Color", "No color", "Border style"),
				backgroundAndBorderNames);
	}

	@Test
	public void testMindMapBackgroundAndBorderProperties() {
		GeoMindMapNode mindMap = new GeoMindMapNode(getKernel().getConstruction(),
				new GPoint2D());
		BackgroundAndBorderPropertyCollection backgroundAndBorderPropertyCollection =
				assertDoesNotThrow(() -> new BackgroundAndBorderPropertyCollection(
						propertiesFactory, getLocalization(), List.of(mindMap)
				));
		List<String> backgroundAndBorderNames = Arrays.stream(
						backgroundAndBorderPropertyCollection.getProperties())
				.map(Property::getName).toList();
		assertEquals(List.of("Background Color", "No color", "Border color", "Border width"),
				backgroundAndBorderNames);
	}
}
