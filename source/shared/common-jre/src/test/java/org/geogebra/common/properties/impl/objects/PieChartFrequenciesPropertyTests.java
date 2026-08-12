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

public class PieChartFrequenciesPropertyTests extends BaseAppTestSetup {

	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testPieChartFromInlineList() {
		GeoElement pieChart = evaluateGeoElement("PieChart({1,2,3},(1,1),3)");

		PieChartFrequenciesProperty frequencies = assertDoesNotThrow(
				() -> new PieChartFrequenciesProperty(getLocalization(), pieChart));
		assertEquals("{1,2,3}", frequencies.getValue());
		frequencies.setValue("{4,5,6}");
		assertEquals("{4,5,6}", frequencies.getValue());
		assertEquals("PieChart({4, 5, 6}, (1, 1), 3)", lookup("a")
				.getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void testPieChartFromNamedLists() {
		GeoElement l1 = evaluateGeoElement("l1={1,2,3}");
		GeoElement l2 = evaluateGeoElement("l2={3,1,2}");
		GeoElement pieChart = evaluateGeoElement("PieChart(l1,(1,1),3)");

		PieChartFrequenciesProperty frequencies = assertDoesNotThrow(
				() -> new PieChartFrequenciesProperty(getLocalization(), pieChart));
		assertEquals("l1", frequencies.getValue());
		frequencies.setValue("l2");
		assertEquals("PieChart(l2, (1, 1), 3)", lookup("a")
				.getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void testValidate() {
		GeoElement l1 = evaluateGeoElement("l1={1,2,3}");
		GeoElement l2 = evaluateGeoElement("l2={3,1,2}");
		GeoElement pieChart = evaluateGeoElement("PieChart(l1,(1,1),3)");

		PieChartFrequenciesProperty frequencies = assertDoesNotThrow(
				() -> new PieChartFrequenciesProperty(getLocalization(), pieChart));
		assertNull(frequencies.validateValue("l2"));
		assertNull(frequencies.validateValue("{7,8,9}"));
		assertNotNull(frequencies.validateValue("f"));
	}
}

