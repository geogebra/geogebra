package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AxisVisibilityPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	public void testBackgroundColor() {
		AxisVisibilityProperty axisProperty = new AxisVisibilityProperty(getApp().getLocalization(),
				getApp().getEuclidianView1().getSettings(), 0, "xAxis");
		assertFalse(axisProperty.getValue());
		axisProperty.setValue(true);
		assertTrue(getApp().getEuclidianView1().getShowAxis(0));
		assertTrue(axisProperty.getValue());
	}
}
