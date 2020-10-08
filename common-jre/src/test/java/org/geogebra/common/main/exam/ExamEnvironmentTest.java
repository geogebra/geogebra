package org.geogebra.common.main.exam;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.settings.CASSettings;
import org.junit.Before;
import org.junit.Test;

public class ExamEnvironmentTest extends BaseUnitTest {

	private ExamEnvironment examEnvironment;
	private CASSettings casSettings;
	private CommandDispatcher commandDispatcher;

	@Before
	public void setUp() {
		examEnvironment = new ExamEnvironment(getLocalization());
		casSettings = getApp().getSettings().getCasSettings();
		commandDispatcher = getKernel().getAlgebraProcessor().getCommandDispatcher();
		examEnvironment.setCommandDispatcher(commandDispatcher);
	}

	@Test
	public void setCasEnabled() {
		testSetCasEnabled(true);
		testSetCasEnabled(false);
	}

	private void testSetCasEnabled(boolean enabled) {
		boolean casDefaultState = isCasEnabled();
		examEnvironment.setCasEnabled(enabled, casSettings);
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