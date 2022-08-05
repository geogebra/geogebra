package org.geogebra.web.full.main;

import static org.junit.Assert.assertTrue;

import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class SuiteTest {

	@Before
	public void assertions() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void startApp() {
		AppMocker.mockApplet(new AppletParameters("suite"));
	}

	@Test
	public void examMode() {
		AppWFull app = AppMocker.mockApplet(new AppletParameters("suite"));
		app.setNewExam();
		app.startExam();
		app.switchToSubapp("geometry");
		app.endExam();
		assertTrue(app.getSettings().getCasSettings().isEnabled());
	}
}
