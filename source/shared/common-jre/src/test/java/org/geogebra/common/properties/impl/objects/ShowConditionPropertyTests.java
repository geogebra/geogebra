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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ShowConditionPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingCondition() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint point = evaluateGeoElement("A = (0, 0)");
		ShowConditionProperty showConditionProperty =
				new ShowConditionProperty(getLocalization(), point);

		showConditionProperty.setValue("1 > 2");
		assertEquals("1 > 2", showConditionProperty.getValue());
		assertFalse(point.isEuclidianVisible());

		showConditionProperty.setValue("3 > 2");
		assertEquals("3 > 2", showConditionProperty.getValue());
		assertTrue(point.isEuclidianVisible());

		showConditionProperty.setValue("");
		assertEquals("", showConditionProperty.getValue());
	}

	@Test
	public void testDynamicCondition() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint point = evaluateGeoElement("A = (0, 0)");
		GeoNumeric slider = evaluateGeoElement("a = Slider(-5, 5, 0.1)");
		ShowConditionProperty showConditionProperty =
				new ShowConditionProperty(getLocalization(), point);
		showConditionProperty.setValue("a > 0");

		slider.setValue(0);
		slider.updateRepaint();
		assertFalse(point.isEuclidianVisible());

		slider.setValue(3);
		slider.updateRepaint();
		assertTrue(point.isEuclidianVisible());

		slider.setValue(-1);
		slider.updateRepaint();
		assertFalse(point.isEuclidianVisible());

	}
}
