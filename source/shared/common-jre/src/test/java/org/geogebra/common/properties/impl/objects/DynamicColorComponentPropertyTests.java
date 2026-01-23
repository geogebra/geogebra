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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class DynamicColorComponentPropertyTests extends BaseAppTestSetup {
	@Test
	public void testStaticColorComponentValueChanging() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_RGB);
		DynamicColorComponentProperty redColorComponentProperty =
				DynamicColorComponentProperty.forRed(getLocalization(), geoElement);

		redColorComponentProperty.setValue("0.5");
		assertEquals("0.5", redColorComponentProperty.getValue());
		assertEquals("0.5", geoElement.getColorFunction().get(0)
				.getLabel(StringTemplate.editTemplate));

		redColorComponentProperty.setValue("0");
		assertEquals("0", redColorComponentProperty.getValue());
		assertEquals("0", geoElement.getColorFunction().get(0)
				.getLabel(StringTemplate.editTemplate));
	}

	@Test
	public void testDynamicColorComponentValueChanging() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 1, 0.1)");
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_RGB);
		DynamicColorComponentProperty redColorComponentProperty =
				DynamicColorComponentProperty.forRed(getLocalization(), geoElement);

		redColorComponentProperty.setValue("a");
		assertEquals("a", redColorComponentProperty.getValue());
		assertEquals("a", geoElement.getColorFunction().get(0)
				.getLabel(StringTemplate.editTemplate));
		assertEquals("0", geoElement.getColorFunction().get(0)
				.toValueString(StringTemplate.editTemplate));

		slider.setValue(0.7);
		slider.updateRepaint();
		assertEquals("a", redColorComponentProperty.getValue());
		assertEquals("a", geoElement.getColorFunction().get(0)
				.getLabel(StringTemplate.editTemplate));
		assertEquals("0.7", geoElement.getColorFunction().get(0)
				.toValueString(StringTemplate.editTemplate));
	}

	@Test
	public void testSettingHSLColorComponentValueWithActiveRGBColorSpace() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_RGB);
		DynamicColorComponentProperty lightnessColorComponentProperty =
				DynamicColorComponentProperty.forLightness(getLocalization(), geoElement);

		assertTrue(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));
		assertEquals(GeoElement.COLORSPACE_RGB, geoElement.getColorSpace());
		assertFalse(lightnessColorComponentProperty.isAvailable());

		lightnessColorComponentProperty.setValue("0.5");

		assertTrue(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));
		assertEquals(GeoElement.COLORSPACE_HSL, geoElement.getColorSpace());
		assertTrue(lightnessColorComponentProperty.isAvailable());
		assertEquals("0.5", lightnessColorComponentProperty.getValue());
	}

	@Test
	public void testSettingHSLColorComponentValueWithInactiveDynamicColorMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorComponentProperty lightnessColorComponentProperty =
				DynamicColorComponentProperty.forLightness(getLocalization(), geoElement);

		assertFalse(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));
		assertFalse(lightnessColorComponentProperty.isAvailable());

		lightnessColorComponentProperty.setValue("0.5");

		assertTrue(DynamicColorModeProperty.isDynamicColorModeActivated(geoElement));
		assertEquals(GeoElement.COLORSPACE_HSL, geoElement.getColorSpace());
		assertTrue(lightnessColorComponentProperty.isAvailable());
		assertEquals("0.5", lightnessColorComponentProperty.getValue());
	}

	@Test
	public void testDynamicColorComponentPropertyAvailabilityInRGBMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_RGB);

		assertTrue(DynamicColorComponentProperty.forRed(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forGreen(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forBlue(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forHueHSB(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forHueHSL(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forSaturationHSB(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forSaturationHSL(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forBrightness(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forLightness(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forOpacity(getLocalization(), geoElement)
				.isAvailable());
	}

	@Test
	public void testDynamicColorComponentPropertyAvailabilityInHSLMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_HSL);

		assertFalse(DynamicColorComponentProperty.forRed(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forGreen(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forBlue(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forHueHSB(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forHueHSL(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forSaturationHSB(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forSaturationHSL(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forBrightness(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forLightness(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forOpacity(getLocalization(), geoElement)
				.isAvailable());
	}

	@Test
	public void testDynamicColorComponentPropertyAvailabilityInHSBMode() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("(1, 2)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		geoElement.setColorSpace(GeoElement.COLORSPACE_HSB);

		assertFalse(DynamicColorComponentProperty.forRed(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forGreen(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forBlue(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forHueHSB(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forHueHSL(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forSaturationHSB(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forSaturationHSL(getLocalization(), geoElement)
				.isAvailable());
		assertTrue(DynamicColorComponentProperty.forBrightness(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forLightness(getLocalization(), geoElement)
				.isAvailable());
		assertFalse(DynamicColorComponentProperty.forOpacity(getLocalization(), geoElement)
				.isAvailable());
	}

	@Test
	public void testOpacityColorComponentAvailabilityForFillableObjects() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("Circle((0, 0), 5)");
		DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		DynamicColorComponentProperty opacityColorComponentProperty =
				DynamicColorComponentProperty.forOpacity(getLocalization(), geoElement);

		geoElement.setColorSpace(GeoElement.COLORSPACE_RGB);
		assertTrue(opacityColorComponentProperty.isAvailable());

		geoElement.setColorSpace(GeoElement.COLORSPACE_HSB);
		assertTrue(opacityColorComponentProperty.isAvailable());

		geoElement.setColorSpace(GeoElement.COLORSPACE_HSL);
		assertTrue(opacityColorComponentProperty.isAvailable());
	}
}
