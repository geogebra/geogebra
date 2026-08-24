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

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChartDataPropertyCollectionTests extends BaseAppTestSetup {

	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testLineChart() {
		GeoElement lineChart = evaluateGeoElement("LineGraph({1,2,3},{2,4,3})");
		ChartDataPropertyCollection collection = assertDoesNotThrow(
				() -> new ChartDataPropertyCollection(propertiesFactory,
						getAlgebraProcessor(), getLocalization(), List.of(lineChart)));
		assertEquals(2, collection.getProperties().length); // x, y
	}

	@Test
	void testHistogram() {
		GeoElement histogram = evaluateGeoElement("Histogram({0,1,2,3},{2,3,1})");
		ChartDataPropertyCollection collection = assertDoesNotThrow(
				() -> new ChartDataPropertyCollection(propertiesFactory,
						getAlgebraProcessor(), getLocalization(), List.of(histogram)));
		assertEquals(7, collection.getProperties().length);
	}
}
