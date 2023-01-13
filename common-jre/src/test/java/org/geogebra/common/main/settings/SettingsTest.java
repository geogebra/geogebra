package org.geogebra.common.main.settings;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class SettingsTest extends BaseUnitTest {

	@Test
	public void resetEuclidianSettings() {
		EuclidianSettings euclidian = getApp().getSettings().getEuclidian(1);
		assertTrue(euclidian.axisShown());
		euclidian.setShowAxes(false);
		getApp().getSettings().resetSettings(getApp());
		assertTrue(euclidian.axisShown());
	}
}
