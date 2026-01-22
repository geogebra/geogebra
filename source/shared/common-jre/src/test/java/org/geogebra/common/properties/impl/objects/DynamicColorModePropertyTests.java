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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class DynamicColorModePropertyTests extends BaseAppTestSetup {
	@Test
	public void testSwitchingDynamicColorMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty dynamicColorModeProperty =
				new DynamicColorModeProperty(getLocalization(), geoElement);

		dynamicColorModeProperty.setValue(true);
		assertTrue(dynamicColorModeProperty.getValue());
		assertNotNull(geoElement.getColorFunction());

		dynamicColorModeProperty.setValue(false);
		assertFalse(dynamicColorModeProperty.getValue());
		assertNull(geoElement.getColorFunction());
	}

	@Test
	public void testDefaultRGBColor() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		GColor color = geoElement.getObjectColor();
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);

		assertEquals(GeoElement.COLORSPACE_RGB, geoElement.getColorSpace());
		assertEquals(color.getRed() / 255d,
				((GeoNumeric) geoElement.getColorFunction().get(0)).getValue(), 0.000001);
		assertEquals(color.getGreen() / 255d,
				((GeoNumeric) geoElement.getColorFunction().get(1)).getValue(), 0.000001);
		assertEquals(color.getBlue() / 255d,
				((GeoNumeric) geoElement.getColorFunction().get(2)).getValue(), 0.000001);
	}
}
