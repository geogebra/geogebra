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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class PieChartCenterPositionPropertyTests extends BaseAppTestSetup {

	@Test
	public void testChangingCenterWithConstantValue() {
		setupApp(SuiteSubApp.GRAPHING);
		PieChartCenterPositionProperty pieChartCenterPositionProperty = assertDoesNotThrow(
				() -> new PieChartCenterPositionProperty(getLocalization(),
						evaluateGeoElement("a = PieChart({1, 2, 3})")));

		pieChartCenterPositionProperty.setValue("(3, 4)");
		assertEquals("PieChart({1, 2, 3}, (3, 4))",
				lookup("a").getDefinition(StringTemplate.defaultTemplate));
		assertEquals("(3, 4)", assertDoesNotThrow(() ->
				new PieChartCenterPositionProperty(getLocalization(), lookup("a"))).getValue());
		assertEquals(3, ((GeoPieChart) lookup("a")).getCenter().x);
		assertEquals(4, ((GeoPieChart) lookup("a")).getCenter().y);
	}

	@Test
	public void testChangingCenterWithDynamicPoint() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint geoPoint = evaluateGeoElement("A = (1, 2)");
		PieChartCenterPositionProperty pieChartCenterPositionProperty = assertDoesNotThrow(
				() -> new PieChartCenterPositionProperty(getLocalization(),
						evaluateGeoElement("a = PieChart({1, 2, 3})")));

		pieChartCenterPositionProperty.setValue("A");
		assertEquals("PieChart({1, 2, 3}, A)",
				lookup("a").getDefinition(StringTemplate.defaultTemplate));
		assertEquals("A", assertDoesNotThrow(() ->
				new PieChartCenterPositionProperty(getLocalization(), lookup("a"))).getValue());
		assertEquals(1, ((GeoPieChart) lookup("a")).getCenter().x);
		assertEquals(2, ((GeoPieChart) lookup("a")).getCenter().y);

		geoPoint.setCoords(5, -7, 1);
		geoPoint.updateRepaint();
		assertEquals(5, ((GeoPieChart) lookup("a")).getCenter().x);
		assertEquals(-7, ((GeoPieChart) lookup("a")).getCenter().y);
	}
}
