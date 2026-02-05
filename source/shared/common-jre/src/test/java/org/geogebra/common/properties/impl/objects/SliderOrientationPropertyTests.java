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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.SliderOrientationProperty.SliderOrientation;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class SliderOrientationPropertyTests extends BaseAppTestSetup {
	@Test
	public void testChangingOrientation() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("Slider(-5, 5, 1)");
		SliderOrientationProperty sliderOrientationProperty = assertDoesNotThrow(() ->
				new SliderOrientationProperty(getLocalization(), slider));

		sliderOrientationProperty.setValue(SliderOrientation.HORIZONTAL);
		assertEquals(SliderOrientation.HORIZONTAL, sliderOrientationProperty.getValue());
		assertTrue(slider.isSliderHorizontal());

		sliderOrientationProperty.setValue(SliderOrientation.VERTICAL);
		assertEquals(SliderOrientation.VERTICAL, sliderOrientationProperty.getValue());
		assertFalse(slider.isSliderHorizontal());
	}
}
