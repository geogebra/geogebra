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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class AlgebraViewVisibilityPropertyCollectionTests extends BaseAppTestSetup {
	private final GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

	@Test
	public void testSettingAlgebraViewSliderVisibility() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("Slider(-5, 5, 1)");
		AlgebraViewVisibilityPropertyCollection algebraViewVisibilityPropertyCollection =
				assertDoesNotThrow(() -> new AlgebraViewVisibilityPropertyCollection(
						propertiesFactory, getLocalization(), List.of(slider)));

		algebraViewVisibilityPropertyCollection.getProperties()[0].setValue(true);
		assertTrue(algebraViewVisibilityPropertyCollection.getProperties()[0].getValue());

		algebraViewVisibilityPropertyCollection.getProperties()[0].setValue(false);
		assertFalse(algebraViewVisibilityPropertyCollection.getProperties()[0].getValue());
	}

	@Test
	public void testDisabledAlgebraViewSliderVisibility() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoNumeric slider = evaluateGeoElement("Slider(-5, 5, 1)");
		AlgebraViewVisibilityPropertyCollection algebraViewVisibilityPropertyCollection =
				assertDoesNotThrow(() -> new AlgebraViewVisibilityPropertyCollection(
						propertiesFactory, getLocalization(), List.of(slider)));

		slider.setAlgebraVisible(false);
		slider.updateRepaint();

		assertFalse(algebraViewVisibilityPropertyCollection.getProperties()[0].isEnabled());
	}
}
