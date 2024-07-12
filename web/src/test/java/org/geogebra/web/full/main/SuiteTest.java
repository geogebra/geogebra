package org.geogebra.web.full.main;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class SuiteTest {

	@Test
	public void startApp() {
		AppMocker.mockApplet(new AppletParameters("suite"));
	}

	@Test
	public void examMode() {
		AppWFull app = AppMocker.mockApplet(new AppletParameters("suite"));
		GlobalHeader.INSTANCE.setApp(app);
		app.startExam(ExamType.GENERIC);
		app.switchToSubapp("geometry");
		GlobalScope.examController.finishExam();
		app.endExam();
		assertTrue(app.getSettings().getCasSettings().isEnabled());
	}
}
