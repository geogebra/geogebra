package org.geogebra.common.main.exam;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class ExamEnvironmentTest {

	private ExamEnvironment examEnvironment;
	private CASSettings casSettings;
	private CommandDispatcher commandDispatcher;

	@Before
	public void setUp() {
		App app = AppMocker.mockCas(getClass());
		examEnvironment = new ExamEnvironment(app);
		casSettings = app.getSettings().getCasSettings();
		commandDispatcher = app.getKernel().getAlgebraProcessor().getCommandDispatcher();
	}

	@Test
	public void exit() {
		boolean casDefaultState = isCasEnabled();
		examEnvironment.setupExamEnvironment();
		examEnvironment.exit();
		assertThat(isCasEnabled(), is(casDefaultState));
	}

	@Test
	public void setCasEnabled() {
		testSetCasEnabled(true);
		testSetCasEnabled(false);
	}

	private void testSetCasEnabled(boolean enabled) {
		boolean casDefaultState = isCasEnabled();
		examEnvironment.setCasEnabled(enabled);
		examEnvironment.setupExamEnvironment();
		if (enabled) {
			assertThat(isCasEnabled(), is(true));
		} else {
			assertThat(isCasDisabled(), is(true));
		}

		examEnvironment.exit();
		assertThat(isCasEnabled(), is(casDefaultState));
	}

	private boolean isCasEnabled() {
		return casSettings.isEnabled() && commandDispatcher.isCASAllowed();
	}

	private boolean isCasDisabled() {
		return !casSettings.isEnabled() && !commandDispatcher.isCASAllowed();
	}
}