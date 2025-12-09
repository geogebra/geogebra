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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AxesVisibilityPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void propertyDependency() {
		LocalizationJre localization = getApp().getLocalization();
		EuclidianSettings evSettings = getApp().getSettings().getEuclidian(1);
		AxisVisibilityProperty xAxis = new AxisVisibilityProperty(localization,
				evSettings, 0, "x");
		AxisVisibilityProperty yAxis = new AxisVisibilityProperty(localization,
				evSettings, 1, "y");
		AxesVisibilityProperty axes = new AxesVisibilityProperty(localization,
				evSettings);
		// initial state
		assertTrue(xAxis.getValue());
		assertTrue(yAxis.getValue());
		assertTrue(axes.getValue());
		// parent -> child sync
		axes.setValue(false);
		assertFalse(xAxis.getValue());
		assertFalse(yAxis.getValue());
		// child -> parent sync
		xAxis.setValue(true);
		assertTrue(axes.getValue());
	}

}