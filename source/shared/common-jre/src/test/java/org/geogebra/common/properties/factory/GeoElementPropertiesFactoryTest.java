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

import static org.junit.jupiter.api.Assertions.assertAll;
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
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.geogebra.common.properties.impl.objects.TextStylePropertyCollection;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoElementPropertiesFactoryTest extends BaseAppTestSetup {

	@BeforeEach
	public void setupApp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testPoint() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		GeoPoint onePoint = evaluateGeoElement("(1,1)");
		PropertiesArray propertiesArray = new GeoElementPropertiesFactory()
				.createGeoElementProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), List.of(zeroPoint, onePoint));
		Property[] pointProperties = propertiesArray.getProperties();

		assertAll(
				() -> assertEquals("Name", pointProperties[0].getName()),
				() -> assertEquals("Show", pointProperties[1].getName()),
				() -> assertEquals("Set color", pointProperties[2].getName()),
				() -> assertEquals("Point Style", pointProperties[3].getName()),
				() -> assertEquals("Size", pointProperties[4].getName()),
				() -> assertEquals("Set caption style", pointProperties[5].getName()),
				() -> assertEquals("Show trace", pointProperties[6].getName()),
				() -> assertEquals("Fixed", pointProperties[7].getName()),
				() -> assertEquals("Show in Algebra View", pointProperties[8].getName())
		);
	}

	@Test
	public void testPointStructured() {
		GeoPoint zeroPoint = evaluateGeoElement("(0,0)");
		GeoPoint onePoint = evaluateGeoElement("(1,1)");
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createStructuredProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), getApp().getImageManager(),
						List.of(zeroPoint, onePoint));
		List<String> basicProperties = Arrays.stream(propertiesArray.get(0).getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Name", "Caption", "Label", "Show", "Show trace", "Fix Object",
				"Auxiliary Object"), basicProperties);
	}

	@Test
	public void testAngleStructured() {
		GeoAngle angle = evaluateGeoElement("Angle[(0,1), (0,0), (1,0)]");
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createStructuredProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), getApp().getImageManager(), List.of(angle));
		List<String> basicProperties = Arrays.stream(propertiesArray.get(0).getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Name", "Definition", "Caption", "Label", "Show",
				"Auxiliary Object"), basicProperties);
	}

	@Test
	public void testNumberStructured() {
		GeoNumeric number = evaluateGeoElement("a=1.5");
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createStructuredProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), getApp().getImageManager(), List.of(number));
		List<String> basicProperties = Arrays.stream(propertiesArray.get(0).getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Name", "Definition", "Caption", "Label", "Show", "Fix Object",
				"Auxiliary Object"), basicProperties);
	}

	@Test
	public void testSliderIsFixed() {
		GeoNumeric numeric1 = evaluateGeoElement("a = 5");
		GeoNumeric numeric2 = evaluateGeoElement("a = 10");
		List<PropertiesArray> propertiesArray = new GeoElementPropertiesFactory()
				.createStructuredProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), getApp().getImageManager(),
						List.of(numeric1, numeric2));
		List<String> basicProperties = Arrays.stream(propertiesArray.get(0).getProperties())
				.map(Property::getName).collect(Collectors.toList());
		assertEquals(List.of("Name", "Caption", "Label", "Show", "Fix Object",
				"Auxiliary Object"), basicProperties);
	}

	@Test
	public void testEquationFormProperty() {
		GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

		GeoLine line = evaluateGeoElement("Line((-1,-1),(1,2))");
		PropertiesArray lineProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getApp().getLocalization(), List.of(line));
		assertTrue(containsLinearEquationFormProperty(lineProperties));
		assertFalse(containsQuadraticEquationFormProperty(lineProperties));

		GeoConic circle = evaluateGeoElement("xx+yy=1");
		PropertiesArray circleProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getApp().getLocalization(), List.of(circle));
		assertFalse(containsLinearEquationFormProperty(circleProperties));
		assertTrue(containsQuadraticEquationFormProperty(circleProperties));
	}

	@Test
	public void testInlineTableTextProperties() {
		GeoInlineTable table = new GeoInlineTable(getKernel().getConstruction(), new GPoint2D());
		try {
			TextStylePropertyCollection textStylePropertyCollection
					= new TextStylePropertyCollection(new GeoElementPropertiesFactory(),
					getLocalization(), List.of(table));
			List<String> textStylePropertyNames = Arrays.stream(
							textStylePropertyCollection.getProperties()).map(Property::getName)
					.toList();
			assertEquals(List.of("Color", "Font", "Font Size", "Text style", "Alignment",
					"Layout"), textStylePropertyNames);
		} catch (NotApplicablePropertyException e) {
			Log.alert("Inline table has no text style");
		}
	}

	@Test
	public void testInlineTextTextProperties() {
		GeoInlineText table = new GeoInlineText(getKernel().getConstruction(), new GPoint2D());
		try {
			TextStylePropertyCollection textStylePropertyCollection
					= new TextStylePropertyCollection(new GeoElementPropertiesFactory(),
					getLocalization(), List.of(table));
			List<String> textStylePropertyNames = Arrays.stream(
							textStylePropertyCollection.getProperties()).map(Property::getName)
					.toList();
			assertEquals(List.of("Color", "Font", "Font Size", "Text style", "Alignment"),
					textStylePropertyNames);
		} catch (NotApplicablePropertyException e) {
			Log.alert("Inline text has no text style");
		}
	}

	private boolean containsLinearEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyListFacade<?, ?>
						&& ((NamedEnumeratedPropertyListFacade<?, ?>) property)
						.getFirstProperty() instanceof LinearEquationFormProperty);
	}

	private boolean containsQuadraticEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.anyMatch(property -> property instanceof NamedEnumeratedPropertyListFacade<?, ?>
						&& ((NamedEnumeratedPropertyListFacade<?, ?>) property)
						.getFirstProperty() instanceof QuadraticEquationFormProperty);
	}
}