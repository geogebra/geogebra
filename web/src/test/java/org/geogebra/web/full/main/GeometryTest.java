package org.geogebra.web.full.main;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class GeometryTest {
	private static AppW app;
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void startApp() {
		app = AppMocker
				.mockApplet(new AppletParameters("geometry"));
	}

}
