package org.geogebra.common.main.exam;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.common.move.ggtapi.models.Material;
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
		examEnvironment.setStart(0);
	}

	@Test
	public void setCasEnabled() {
		testSetCasEnabled(true);
		testSetCasEnabled(false);
	}

	@Test
	public void testTempMaterials() {
		assertThat(
				examEnvironment.getTempStorage().collectTempMaterials().size(),
				equalTo(0));

		Material a = examEnvironment.getTempStorage().newMaterial();
		a.setTitle("a");
		examEnvironment.getTempStorage().saveTempMaterial();
		assertThat(
				examEnvironment.getTempStorage().collectTempMaterials().size(),
				equalTo(1));

		Material b = examEnvironment.getTempStorage().newMaterial();
		b.setTitle("b");
		examEnvironment.getTempStorage().saveTempMaterial();
		assertThat(
				examEnvironment.getTempStorage().collectTempMaterials().size(),
				equalTo(2));

		examEnvironment.getTempStorage().saveTempMaterial();
		// should be overwritten because ids are equal and titles are equal
		assertThat(
				examEnvironment.getTempStorage().collectTempMaterials().size(),
				equalTo(2));

		b.setTitle("anotherTitle");
		examEnvironment.getTempStorage().saveTempMaterial();
		// should be saved as new material because the ids are equal but the titles are different
		assertThat(
				examEnvironment.getTempStorage().collectTempMaterials().size(),
				equalTo(3));
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