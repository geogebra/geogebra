/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.desktop.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
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

		assertEquals(Set.of("C", "b", "B", "c"), getLabels());
	}

	@Test
	@Issue("APPS-6479")
	public void testInsertingFileDoesRenameDuplicateObjects() {
		processCommand(fromApp, "C = (1, 2)", false);
		processCommand(fromApp, "b = 1 / 3", false);
		processCommand(toApp, "B = (4, 5)", false);
		processCommand(toApp, "C = 5", false);
		copy.insertFrom(fromApp, toApp, Set.of("C"), false);

		assertEquals(Set.of("C", "b", "B", "C_{1}"), getLabels());
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
				getLabels().contains("C"));
		assertFalse("There should be no indexed label <C_{1]>!",
				getLabels().contains("C_{1}"));
	}

	@Test
	@Issue("APPS-6665")
	public void insertIntoShouldNotCreateDuplicateLabels() {
		processCommand(fromApp, "A = (1, 1)", false);
		processCommand(fromApp, "A_1 = (1, 2)", false);
		processCommand(toApp, "A = (1, 3)", false);
		copy.insertFrom(fromApp, toApp, Set.of("A"), false);

		//assertThat(getLabeledGeosSize(toApp), equalTo(4));
		assertEquals(Set.of("A", "A_1", "A_{2}"), getLabels());
	}

	@Test
	@Issue("APPS-6665")
	public void insertIntoShouldKeepDependentObjects() {
		processCommand(toApp, "A = (1, 1)", false);
		processCommand(fromApp, "A = (1, 2)", false);
		processCommand(fromApp, "B = (1, 3)", false);
		processCommand(fromApp, "s = Segment(A,B)", false);
		copy.insertFrom(fromApp, toApp, Set.of("A"), true);
		assertEquals(Set.of("A", "B", "s"), getLabels());
		AlgoElement parentAlgorithm = toApp.getKernel().lookupLabel("s")
				.getParentAlgorithm();
		assertEquals(Commands.Segment, Objects.requireNonNull(parentAlgorithm).getClassName());
	}

	private void processCommand(AppCommon app, String command, boolean storeUndo) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(command, storeUndo);
	}

	private int getLabeledGeosSize(AppCommon app) {
		return app.getKernel().getConstruction().getGeoSetConstructionOrder().size();
	}

	private Set<String> getLabels() {
		return toApp.getKernel().getConstruction().getGeoSetConstructionOrder().stream()
				.map(GeoElement::getLabelSimple).collect(Collectors.toSet());
	}
}
