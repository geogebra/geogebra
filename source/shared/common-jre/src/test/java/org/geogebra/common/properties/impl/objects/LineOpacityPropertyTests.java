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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class LineOpacityPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingOpacityForLine() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoLine geoLine = evaluateGeoElement("x = 0");
		LineOpacityProperty lineOpacityProperty = assertDoesNotThrow(() ->
				LineOpacityProperty.forLine(getLocalization(), geoLine));

		lineOpacityProperty.setValue(100);
		assertEquals(100, lineOpacityProperty.getValue());
		assertEquals(255, geoLine.getLineOpacity());

		lineOpacityProperty.setValue(0);
		assertEquals(0, lineOpacityProperty.getValue());
		assertEquals(0, geoLine.getLineOpacity());

		lineOpacityProperty.setValue(50);
		assertEquals(50, lineOpacityProperty.getValue());
		assertEquals(128, geoLine.getLineOpacity());
	}

	@Test
	public void testInitialOpacityForSliderTrack() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 10)");
		LineOpacityProperty property = assertDoesNotThrow(() ->
				LineOpacityProperty.forSlider(getLocalization(), slider));
		assertEquals(GeoNumeric.DEFAULT_SLIDER_LINE_OPACITY, slider.getLineOpacity());
		assertEquals(Math.round(slider.getLineOpacity() / 255f * 100), property.getValue());
	}

	@Test
	public void testChangingOpacityForSliderTrack() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 10)");
		LineOpacityProperty property = assertDoesNotThrow(() ->
				LineOpacityProperty.forSlider(getLocalization(), slider));

		property.setValue(100);
		assertEquals(100, property.getValue());
		assertEquals(255, slider.getLineOpacity());
		assertNull(slider.getBackgroundColor());

		property.setValue(0);
		assertEquals(0, property.getValue());
		assertEquals(0, slider.getLineOpacity());
		assertNull(slider.getBackgroundColor());

		property.setValue(56);
		assertEquals(56, property.getValue());
		assertEquals(Math.round(56 / 100f * 255), slider.getLineOpacity());
		assertNull(slider.getBackgroundColor());
	}

	@Test
	public void testChangingOpacityDoesNotEnableTrackColor() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 10)");
		LineOpacityProperty opacityProperty = assertDoesNotThrow(() ->
				LineOpacityProperty.forSlider(getLocalization(), slider));
		SliderTrackColorEnabledProperty colorEnabledProperty = assertDoesNotThrow(() ->
				new SliderTrackColorEnabledProperty(getLocalization(), slider));
		SliderTrackColorProperty colorProperty = assertDoesNotThrow(() ->
				new SliderTrackColorProperty(getLocalization(), slider));

		assertFalse(colorEnabledProperty.getValue());
		assertFalse(colorProperty.isEnabled());

		opacityProperty.setValue(56);

		assertFalse(colorEnabledProperty.getValue());
		assertFalse(colorProperty.isEnabled());
		assertNull(slider.getBackgroundColor());
	}

	@Test
	public void testChangingCustomColorPreservesOpacity() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("a = Slider(0, 10)");
		LineOpacityProperty opacityProperty = assertDoesNotThrow(() ->
				LineOpacityProperty.forSlider(getLocalization(), slider));
		SliderTrackColorEnabledProperty colorEnabledProperty = assertDoesNotThrow(() ->
				new SliderTrackColorEnabledProperty(getLocalization(), slider));
		SliderTrackColorProperty colorProperty = assertDoesNotThrow(() ->
				new SliderTrackColorProperty(getLocalization(), slider));

		opacityProperty.setValue(56);
		colorEnabledProperty.setValue(true);
		colorProperty.setValue(GColor.RED);

		assertEquals(56, opacityProperty.getValue());
		assertEquals(Math.round(56 / 100f * 255), slider.getLineOpacity());
		assertEquals(255, slider.getBackgroundColor().getAlpha());
	}
}
