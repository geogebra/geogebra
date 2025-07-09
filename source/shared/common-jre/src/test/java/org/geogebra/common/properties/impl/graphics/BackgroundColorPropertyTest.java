package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BackgroundColorPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GEOMETRY);
	}

	@Test
	public void testBackgroundColor() {
		BackgroundColorProperty bgProperty = new BackgroundColorProperty(getApp().getLocalization(),
				getApp().getEuclidianView1().getSettings());
		assertEquals(GColor.WHITE, bgProperty.getValue());
		bgProperty.setValue(GColor.GREEN);
		assertEquals(GColor.GREEN, getApp().getEuclidianView1().getBackgroundCommon());
		assertEquals(GColor.GREEN, bgProperty.getValue());
	}
}