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
 
package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StickToEdgePropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void stickPropertyShouldDisableCrossAt() {
		LocalizationJre loc = getApp().getLocalization();
		EuclidianSettings evSettings = getApp().getSettings().getEuclidian(1);
		EuclidianView euclidianView = getApp().getEuclidianView1();
		StickToEdgeProperty stickToEdgeProperty = new StickToEdgeProperty(loc, 0, evSettings,
				euclidianView);
		assertFalse(stickToEdgeProperty.getValue());
		CrossAtProperty crossAtProperty = new CrossAtProperty(loc, evSettings, euclidianView, 0);
		assertEquals(0, evSettings.getAxesCross()[0], 1E-8);
		crossAtProperty.setValue("1");
		assertEquals(1, evSettings.getAxesCross()[0], 1E-8);
		stickToEdgeProperty.setValue(true);
		assertFalse(crossAtProperty.isEnabled(), "Stick to edge should disable cross");
		stickToEdgeProperty.setValue(false);
		assertTrue(crossAtProperty.isEnabled(), "Removing stick to edge should enable cross");
		assertEquals(0, evSettings.getAxesCross()[0], 1E-8);
	}
}
