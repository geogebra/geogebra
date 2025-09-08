package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RulingGridLineStylePropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testEnabled() {
		EuclidianSettings euclidianSettings = getApp().getSettings().getEuclidian(1);
		RulingGridLineStyleProperty prop = new RulingGridLineStyleProperty(
				getApp().getLocalization(), euclidianSettings, false);
		assertTrue(prop.isEnabled(), "Should be enabled by default");
		euclidianSettings.setGridType(EuclidianView.GRID_DOTS);
		assertFalse(prop.isEnabled(), "Should be disabled for dots");
	}
}
