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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChartDataCoordinatePropertyTests extends BaseAppTestSetup {

	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testLineChartFromInlineLists() {
		GeoElement lineChart = evaluateGeoElement("LineGraph({1,2,3},{2,3,1})");

		ChartDataCoordinateProperty x = assertDoesNotThrow(
				() -> new ChartDataCoordinateProperty(getLocalization(),
						lineChart, ChartDataCoordinateProperty.Axis.X));
		assertEquals("{1,2,3}", x.getValue());
		x.setValue("{4,5,6}");
		assertEquals("{4,5,6}", x.getValue());
		assertEquals("LineGraph({4, 5, 6}, {2, 3, 1})", lookup("f")
				.getDefinition(StringTemplate.testTemplate));

		ChartDataCoordinateProperty y = assertDoesNotThrow(
				() -> new ChartDataCoordinateProperty(getLocalization(),
						lineChart, ChartDataCoordinateProperty.Axis.Y));
		assertEquals("{2,3,1}", y.getValue());
		y.setValue("{7,8,9}");
		assertEquals("{7,8,9}", y.getValue());
		assertEquals("LineGraph({4, 5, 6}, {7, 8, 9})", lookup("f")
				.getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void testLineChartFromNamedLists() {
		GeoElement l1 = evaluateGeoElement("l1={1,2,3}");
		GeoElement l2 = evaluateGeoElement("l2={3,1,2}");
		GeoElement l3 = evaluateGeoElement("l3={4,5,6}");
		GeoElement l4 = evaluateGeoElement("l4={7,8,9}");
		GeoElement lineChart = evaluateGeoElement("LineGraph(l1,l2)");

		ChartDataCoordinateProperty x = assertDoesNotThrow(
				() -> new ChartDataCoordinateProperty(getLocalization(),
						lineChart, ChartDataCoordinateProperty.Axis.X));
		assertEquals("l1", x.getValue());
		x.setValue("l3");
		assertEquals("LineGraph(l3, l2)", lookup("f")
				.getDefinition(StringTemplate.testTemplate));

		ChartDataCoordinateProperty y = assertDoesNotThrow(
				() -> new ChartDataCoordinateProperty(getLocalization(),
						lineChart, ChartDataCoordinateProperty.Axis.Y));
		assertEquals("l2", y.getValue());
	}

	@Test
	void testValidate() {
		GeoElement l1 = evaluateGeoElement("l1={1,2,3}");
		GeoElement l2 = evaluateGeoElement("l2={3,1,2}");
		GeoElement l3 = evaluateGeoElement("l3={4,5,6}");
		GeoElement lineChart = evaluateGeoElement("LineGraph(l1,l2)");

		ChartDataCoordinateProperty x = assertDoesNotThrow(
				() -> new ChartDataCoordinateProperty(getLocalization(),
						lineChart, ChartDataCoordinateProperty.Axis.X));
		assertNull(x.validateValue("l3"));
		assertNull(x.validateValue("{7,8,9}"));
		assertNotNull(x.validateValue("f"));
	}
}
