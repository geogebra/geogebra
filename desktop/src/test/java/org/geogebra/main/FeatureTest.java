package org.geogebra.main;

import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Test;

public class FeatureTest {
	@Test
	public void checkFeatures() {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), false);
		app.testFeatures();
	}
}
