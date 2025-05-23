package org.geogebra.desktop.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class CopyPasteDTest {

	private AppCommon fromApp;
	private AppCommon toApp;
	private CopyPasteD copy;

	@Before
	public void setup() {
		fromApp = AppCommonFactory.create3D();
		fromApp.setUndoRedoMode(UndoRedoMode.GUI);
		fromApp.setUndoActive(true);
		toApp = AppCommonFactory.create3D();
		toApp.setUndoRedoMode(UndoRedoMode.GUI);
		toApp.setUndoActive(true);
		copy = new CopyPasteD();
	}

	@Test
	public void clipboardStringShouldDisappearOnInsert() {
		processCommand(fromApp, "s:Sequence(2k,k,1,3)", true);
		processCommand(fromApp, "c:Curve(sin(t),cos(t),t,0,2)", true);
		copy.insertFrom(fromApp, toApp, Collections.emptySet(), false);

		assertEquals(Arrays.asList("s", "c"), Arrays.asList(toApp.getGgbApi().getAllObjectNames()));
		assertThat(toApp.getKernel().lookupLabel("s").getDefinitionForEditor(),
				equalTo("s=Sequence(2 k,k,1,3)"));
		assertThat(toApp.getKernel().lookupLabel("c").getDefinitionForEditor(),
				equalTo("c=Curve(sin(t),cos(t),t,0,2)"));
		assertThat(toApp.getKernel().lookupLabel("c").toString(StringTemplate.testTemplate),
				equalTo("c:(sin(t), cos(t))"));
	}

	@Test
	@Issue("APPS-6479")
	public void testInsertingFileDoesNotRenameObjects() {
		processCommand(fromApp, "C = (1, 2)", false);
		processCommand(fromApp, "b = 1 / 3", false);
		processCommand(toApp, "B = (4, 5)", false);
		processCommand(toApp, "c = 5", false);
		copy.insertFrom(fromApp, toApp, Collections.emptySet(), false);

		assertThat(getLabeledGeosSize(toApp), equalTo(4));
		assertTrue("The labels <C>, <b>, <B>, and <c> should all exist!",
				containsLabels(toApp, Set.of("C", "b", "B", "c")));
	}

	@Test
	@Issue("APPS-6479")
	public void testInsertingFileDoesRenameDuplicateObjects() {
		processCommand(fromApp, "C = (1, 2)", false);
		processCommand(fromApp, "b = 1 / 3", false);
		processCommand(toApp, "B = (4, 5)", false);
		processCommand(toApp, "C = 5", false);
		copy.insertFrom(fromApp, toApp, Set.of("C"), false);

		assertThat(getLabeledGeosSize(toApp), equalTo(4));
		assertTrue("The labels <C>, <b>, <B>, and <C_{1}> should all exist!",
				containsLabels(toApp, Set.of("C", "b", "B", "C_{1}")));
	}

	@Test
	@Issue("APPS-6479")
	public void testInsertingFileDoesOverwriteDuplicateObjects() {
		processCommand(fromApp, "C = (1, 2)", false);
		processCommand(fromApp, "b = 1 / 3", false);
		processCommand(toApp, "B = (4, 5)", false);
		processCommand(toApp, "C = (3, 4)", false);
		copy.insertFrom(fromApp, toApp, Set.of("C"), true);

		assertThat(getLabeledGeosSize(toApp), equalTo(3));
		assertThat(toApp.getKernel().lookupLabel("C").getDefinitionForEditor(),
				equalTo("C=$point(1,2)"));
		assertTrue("The element labeled <C> should exist!",
				containsLabels(toApp, Set.of("C")));
		assertFalse("There should be no indexed label <C_{1]>!",
				containsLabels(toApp, Set.of("C_{1}")));
	}

	private void processCommand(AppCommon app, String command, boolean storeUndo) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(command, storeUndo);
	}

	private int getLabeledGeosSize(AppCommon app) {
		return app.getKernel().getConstruction().getGeoSetConstructionOrder().size();
	}

	private boolean containsLabels(AppCommon app, Set<String> labels) {
		return app.getKernel().getConstruction().getAllGeoLabels().containsAll(labels);
	}
}
