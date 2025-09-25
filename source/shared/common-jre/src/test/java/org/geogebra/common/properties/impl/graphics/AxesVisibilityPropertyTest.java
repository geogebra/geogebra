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