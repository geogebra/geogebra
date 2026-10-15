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

package org.geogebra.common.properties.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.geogebra.common.properties.impl.objects.TextStylePropertyCollection;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoElementPropertiesFactoryTest extends BaseAppTestSetup {

	@BeforeEach
	void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testBasicPointProperties() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		GeoPoint onePoint = evaluateGeoElement("(1,1)");
		List<String> basicProperties = getPropertiesList(List.of(zeroPoint, onePoint), 0);
		assertEquals(
				List.of(
						"Name",
						"Caption",
						"Label",
						"Show Object",
						"Show trace",
						"Fix Object",
						"Auxiliary Object"),
				basicProperties);
	}

	@Test
	void testPointStyleProperties() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		List<String> basicProperties = getPropertiesList(List.of(zeroPoint), 1);
		assertEquals(List.of("Style"), basicProperties);
	}

	@Test
	void testPointAdvancedProperties() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		List<String> basicProperties = getPropertiesList(List.of(zeroPoint), 2);
		assertEquals(
				List.of("Interaction", "Algebra", "Visibility", "Dynamic Colors", "Layer"),
				basicProperties);
	}

	@Test
	void testAngleProperties() {
		GeoAngle angle = evaluateGeoElement("Angle[(0,1), (0,0), (1,0)]");
		List<String> basicProperties = getPropertiesList(List.of(angle), 0);
		assertEquals(
				List.of("Name", "Definition", "Caption", "Label", "Show Object", "Auxiliary Object"),
				basicProperties);
	}

	@Test
	void testNumberProperties() {
		GeoNumeric number = evaluateGeoElement("a=1.5");
		List<String> basicProperties = getPropertiesList(List.of(number), 0);
		assertEquals(
				List.of(
						"Name",
						"Definition",
						"Caption",
						"Label",
						"Show Object",
						"Fix Object",
						"Auxiliary Object"),
				basicProperties);
	}

	@Test
	void testSliderIsFixed() {
		GeoNumeric numeric1 = evaluateGeoElement("a = 5");
		GeoNumeric numeric2 = evaluateGeoElement("a = 10");
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createProperties(
						getKernel().getAlgebraProcessor(),
						getApp().getLocalization(),
						getApp().getImageManager(),
						true,
						List.of(numeric1, numeric2));
		List<String> basicProperties = Arrays.stream(propertiesArray.get(0).getProperties())
				.map(Property::getName)
				.collect(Collectors.toList());
		assertEquals(
				List.of("Name", "Caption", "Label", "Show Object", "Fix Object", "Auxiliary Object"),
				basicProperties);
	}

	@Test
	void testEquationFormProperty() {
		GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

		GeoLine line = evaluateGeoElement("Line((-1,-1),(1,2))");
		List<PropertiesArray> lineProperties = propertiesFactory.createProperties(
				getAlgebraProcessor(), getApp().getLocalization(), null, false, List.of(line));
		PropertiesArray advanced = lineProperties.get(2);
		assertTrue(containsLinearEquationFormProperty(advanced));
		assertFalse(containsQuadraticEquationFormProperty(advanced));

		GeoConic circle = evaluateGeoElement("xx+yy=1");
		List<PropertiesArray> circleProperties = propertiesFactory.createProperties(
				getAlgebraProcessor(), getApp().getLocalization(), null, false, List.of(circle));
		advanced = circleProperties.get(2);
		assertFalse(containsLinearEquationFormProperty(advanced));
		assertTrue(containsQuadraticEquationFormProperty(advanced));
	}

	@Test
	void testInlineTableTextProperties() {
		GeoInlineTable table = new GeoInlineTable(getKernel().getConstruction(), new GPoint2D());
		try {
			TextStylePropertyCollection textStylePropertyCollection = new TextStylePropertyCollection(
					new GeoElementPropertiesFactory(), getLocalization(), List.of(table));
			List<String> textStylePropertyNames = Arrays.stream(
							textStylePropertyCollection.getProperties())
					.map(Property::getName)
					.toList();
			assertEquals(
					List.of("Color", "Font", "Font Size", "Text style", "Alignment", "Layout"),
					textStylePropertyNames);
		} catch (NotApplicablePropertyException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	void testInlineTextTextProperties() {
		GeoInlineText table = new GeoInlineText(getKernel().getConstruction(), new GPoint2D());
		try {
			TextStylePropertyCollection textStylePropertyCollection = new TextStylePropertyCollection(
					new GeoElementPropertiesFactory(), getLocalization(), List.of(table));
			List<String> textStylePropertyNames = Arrays.stream(
							textStylePropertyCollection.getProperties())
					.map(Property::getName)
					.toList();
			assertEquals(
					List.of("Color", "Font", "Font Size", "Text style", "Alignment"), textStylePropertyNames);
		} catch (NotApplicablePropertyException e) {
			throw new AssertionError(e);
		}
	}

	private boolean containsLinearEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.flatMap(collection -> Arrays.stream(((PropertyCollection<?>) collection).getProperties()))
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyListFacade<?, ?>
						&& ((NamedEnumeratedPropertyListFacade<?, ?>) property).getFirstProperty()
								instanceof LinearEquationFormProperty);
	}

	private boolean containsQuadraticEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.flatMap(collection -> Arrays.stream(((PropertyCollection<?>) collection).getProperties()))
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyListFacade<?, ?>
						&& ((NamedEnumeratedPropertyListFacade<?, ?>) property).getFirstProperty()
								instanceof QuadraticEquationFormProperty);
	}

	private List<String> getPropertiesList(List<GeoElement> zeroPoint, int tab) {
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createProperties(
						getKernel().getAlgebraProcessor(),
						getApp().getLocalization(),
						getApp().getImageManager(),
						true,
						zeroPoint);
		return Arrays.stream(propertiesArray.get(tab).getProperties())
				.map(Property::getName)
				.collect(Collectors.toList());
	}
}
