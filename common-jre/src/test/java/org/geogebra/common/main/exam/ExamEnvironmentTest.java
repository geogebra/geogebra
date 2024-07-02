package org.geogebra.common.main.exam;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.UserPublic;
import org.junit.Before;
import org.junit.Test;

@Deprecated
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
		TempStorage tempStorage = examEnvironment.getTempStorage();
		assertThat(tempStorage.collectTempMaterials().size(), equalTo(0));

		Material a = tempStorage.newMaterial();
		a.setTitle("a");
		tempStorage.saveTempMaterial(a);
		assertThat(tempStorage.collectTempMaterials().size(), equalTo(1));

		Material b = tempStorage.newMaterial();
		b.setTitle("b");
		tempStorage.saveTempMaterial(b);
		assertThat(tempStorage.collectTempMaterials().size(), equalTo(2));

		tempStorage.saveTempMaterial(b);
		// should be overwritten because ids are equal and titles are equal
		assertThat(tempStorage.collectTempMaterials().size(), equalTo(2));

		b.setTitle("anotherTitle");
		tempStorage.saveTempMaterial(b);
		// should be saved as new material because the ids are equal but the titles are different
		assertThat(tempStorage.collectTempMaterials().size(), equalTo(3));
	}

	@Test
	public void testTempMaterialsWithSameName() {
		TempStorage tempStorage = examEnvironment.getTempStorage();

		Material material = tempStorage.newMaterial();
		material.setCreator(new UserPublic(1, "author"));
		material.setTitle("a");
		tempStorage.saveTempMaterial(material);
		material.setTitle("b");
		tempStorage.saveTempMaterial(material);
		material.setTitle("a");
		tempStorage.saveTempMaterial(material);

		Material aFirstOpened = tempStorage.collectTempMaterials().iterator().next();
		aFirstOpened.setCreator(new UserPublic(1, "anotherAuthor"));
		tempStorage.saveTempMaterial(aFirstOpened);

		Iterator<Material> iterator = tempStorage.collectTempMaterials().iterator();
		iterator.next(); // a
		iterator.next(); // b
		Material aSecondAOpened = iterator.next(); // a
		assertThat(aSecondAOpened.getAuthor(), equalTo("author"));

		aFirstOpened = tempStorage.collectTempMaterials().iterator().next();
		assertThat(aFirstOpened.getAuthor(), equalTo("anotherAuthor"));
	}

	@Test
	public void testTempMaterialsOpen() {
		Material a = examEnvironment.getTempStorage().newMaterial();
		a.setTitle("a");
		examEnvironment.getTempStorage().saveTempMaterial(a);

		Material aOpened
				= examEnvironment.getTempStorage().collectTempMaterials().iterator().next();
		aOpened.setTitle("newTitle");

		Material aOpenedAgain =
				examEnvironment.getTempStorage().collectTempMaterials().iterator().next();

		assertThat(aOpenedAgain.getTitle(), equalTo("a"));
	}

	private void testSetCasEnabled(boolean enabled) {
		boolean casDefaultState = isCasEnabled();
		examEnvironment.setCasEnabled(enabled, casSettings);
		examEnvironment.prepareExamForStarting();
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