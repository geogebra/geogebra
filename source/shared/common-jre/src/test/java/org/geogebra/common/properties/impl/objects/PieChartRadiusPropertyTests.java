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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class PieChartRadiusPropertyTests extends BaseAppTestSetup {

	@Test
	public void testNotApplicable() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart element = evaluateGeoElement("Element({PieChart({1, 2, 3})}, 1)");
		assertThrows(NotApplicablePropertyException.class, () ->
				new PieChartRadiusProperty(getAlgebraProcessor(), getLocalization(), element));
	}

	@Test
	public void testChangingRadiusWithConstantValue() {
		setupApp(SuiteSubApp.GRAPHING);
		PieChartRadiusProperty property = assertDoesNotThrow(
				() -> new PieChartRadiusProperty(getAlgebraProcessor(), getLocalization(),
						evaluateGeoElement("a = PieChart({1, 2, 3})")));
		property.setValue("9");
		assertEquals("PieChart({1, 2, 3}, (0, 0), 9)",
				lookup("a").getDefinition(StringTemplate.defaultTemplate));
		assertEquals("9", assertDoesNotThrow(() -> new PieChartRadiusProperty(getAlgebraProcessor(),
				getLocalization(), lookup("a"))).getValue());
	}

	@Test
	public void testChangingRadiusWithDynamicValue() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(1, 10, 1)");
		slider.setValue(5);
		slider.updateRepaint();
		PieChartRadiusProperty pieChartRadiusProperty = assertDoesNotThrow(
				() -> new PieChartRadiusProperty(getAlgebraProcessor(), getLocalization(),
						evaluateGeoElement("b = PieChart({1, 2, 3})")));

		pieChartRadiusProperty.setNumberValue(slider);
		assertEquals("PieChart({1, 2, 3}, (0, 0), a)",
				lookup("b").getDefinition(StringTemplate.defaultTemplate));
		assertEquals("a", assertDoesNotThrow(() -> new PieChartRadiusProperty(getAlgebraProcessor(),
						getLocalization(), lookup("b"))).getValue());
		assertEquals(5, ((GeoPieChart) lookup("b")).getRadius());

		slider.setValue(8);
		slider.updateRepaint();
		assertEquals(8, ((GeoPieChart) lookup("b")).getRadius());

		slider.setValue(10);
		slider.updateRepaint();
		assertEquals(10, ((GeoPieChart) lookup("b")).getRadius());
	}
}
