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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class DynamicColorSpacePropertyTests extends BaseAppTestSetup {
	@Test
	public void testChangingColorSpace() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		DynamicColorSpaceProperty dynamicColorSpaceProperty =
				new DynamicColorSpaceProperty(getLocalization(), geoElement);

		dynamicColorSpaceProperty.setValue(GeoElement.COLORSPACE_RGB);
		assertEquals(GeoElement.COLORSPACE_RGB, dynamicColorSpaceProperty.getValue());
		assertEquals(GeoElement.COLORSPACE_RGB, geoElement.getColorSpace());

		dynamicColorSpaceProperty.setValue(GeoElement.COLORSPACE_HSL);
		assertEquals(GeoElement.COLORSPACE_HSL, dynamicColorSpaceProperty.getValue());
		assertEquals(GeoElement.COLORSPACE_HSL, geoElement.getColorSpace());
	}

	@Test
	public void testChangingColorSpaceWithInactiveDynamicColorMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorSpaceProperty dynamicColorSpaceProperty =
				new DynamicColorSpaceProperty(getLocalization(), geoElement);

		assertFalse(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));

		dynamicColorSpaceProperty.setValue(GeoElement.COLORSPACE_HSB);

		assertTrue(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));
		assertEquals(GeoElement.COLORSPACE_HSB, dynamicColorSpaceProperty.getValue());
	}
}
