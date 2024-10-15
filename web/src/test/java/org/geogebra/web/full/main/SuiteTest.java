package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class SuiteTest {
	private AppWFull app;

	@Test
	public void examMode() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		GlobalHeader.INSTANCE.setApp(app);
		app.startExam(ExamType.GENERIC, null);
		app.switchToSubapp("geometry");
		GlobalScope.examController.finishExam();
		app.endExam();
		assertTrue(app.getSettings().getCasSettings().isEnabled());
	}

	@Test
	public void filterTest() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		algebraProcessor.processAlgebraCommand("h(x)=x", false);
		algebraProcessor.processAlgebraCommand("l={1}*2", false);
		assertThat(getValueString("h"), equalTo("h(x) = x"));
		assertThat(getValueString("l"), equalTo("l = {2}"));
		app.switchToSubapp("scientific");
		algebraProcessor.processAlgebraCommand("h(x)=x", false);
		algebraProcessor.processAlgebraCommand("l={1}*2", false);
		assertThat(app.getKernel().lookupLabel("h"), nullValue());
		assertThat(app.getKernel().lookupLabel("l"), nullValue());
	}

	private String getValueString(String label) {
		return app.getKernel().lookupLabel(label).toString(StringTemplate.testTemplate);
	}
}
