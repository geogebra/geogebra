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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_BUTTON_ACTION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GlobalKeyDispatcherHeadless;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests button dialog handoff, real model submission, movement and click scripts. */
class ButtonToolTest extends BaseDialogToolTest {
	private static final String INCREMENT = "SetValue(n,n+1)";

	@Test
	void enteringAndHoveringShouldNotOpenDialogOrCreateObjects() {
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			moveMouseRW(2, -2);
			moveMouseRW(4, -4);

			verifyNoWidgetCreationDialog(dialogs);
			assertTrue(ec.modeNeedsKeyboard());
			assertEquals(MODE_BUTTON_ACTION, getApp().getMode());
			assertEquals(Set.of(point), getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void clickShouldRequestButtonInsteadOfInputBoxAtScreenPosition(@NonNull PointerEventType type) {
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			click(150, 120, type);

			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(150, 120, false);
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@Test
	void missingMouseLocationShouldNotRequestDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			ec.processMode(new Hits(), false, false);

			verifyNoWidgetCreationDialog(dialogs);
			assertTrue(getConstructionSnapshot().isEmpty());
		}
	}

	@Test
	@Issue("APPS-7920")
	void missingDialogManagerShouldNotThrowOrCreateObjects() {
		assertNull(getApp().getDialogManager());
		setMode(MODE_BUTTON_ACTION);

		click(150, 120);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertEquals(MODE_BUTTON_ACTION, getApp().getMode());
	}

	@Test
	void clickOnPointShouldRequestDialogWithoutMovingPoint() {
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			clickRW(2, -2);

			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(100, 100, false);
			assertEquals(Set.of(point), getConstructionSnapshot());
			assertEquals(2, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void dialogResultAloneShouldNotCreateButtonOrUndo(boolean result) {
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			Mockito.when(dialog(dialogs).showButtonCreationDialog(150, 120, false)).thenReturn(result);
			setMode(MODE_BUTTON_ACTION);

			click(150, 120);

			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(150, 120, false);
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@Test
	void unsubmittedModelShouldNotChangeConstructionOrUndo() {
		enableUndo();
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_BUTTON_ACTION);

			click(150, 120);

			assertEquals(1, models.size());
			assertFalse(models.get(0).isTextField());
			assertEquals("", models.get(0).getInitString());
			assertEquals("", models.get(0).getClickScript());
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@ParameterizedTest
	@CsvSource(
			value = {"' Run '|Run", "''|''", "'   '|''", "'Δx > 0'|'Δx > 0'"},
			delimiter = '|')
	void applyingDialogShouldCreateVisibleFixedButtonWithTrimmedCaption(
			@NonNull String caption, @NonNull String expectedCaption) {
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_BUTTON_ACTION);
			click(150, 120);

			models.get(0).apply(caption, "");

			GeoButton button = onlyButton();
			assertFalse(button.isGeoInputBox());
			assertTrue(button.isEuclidianVisible());
			assertTrue(button.isLabelVisible());
			assertTrue(button.isLocked());
			assertEquals(150, button.getAbsoluteScreenLocX());
			assertEquals(120, button.getAbsoluteScreenLocY());
			assertEquals(
					expectedCaption.isEmpty() ? button.getLabelSimple() : expectedCaption,
					button.getCaption(StringTemplate.defaultTemplate));
			assertEquals(Set.of(button), getConstructionSnapshot());
		}
	}

	@Test
	void modelShouldKeepScreenPositionAfterViewAndPointerMove() {
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_BUTTON_ACTION);
			click(150, 120);
			getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
			moveMouseRW(3, -3);

			models.get(0).apply("Run", "");

			assertEquals(150, onlyButton().getAbsoluteScreenLocX());
			assertEquals(120, onlyButton().getAbsoluteScreenLocY());
		}
	}

	@Test
	void pendingDialogsShouldKeepIndependentPositionsAndCaptions() {
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_BUTTON_ACTION);
			click(150, 120);
			click(300, 220);
			assertEquals(2, models.size());
			assertTrue(getConstructionSnapshot().isEmpty());

			models.get(1).apply("Second", "");
			GeoButton second = onlyButton();
			Set<String> before = getObjectNames();
			models.get(0).apply("First", "");

			GeoButton first = getOnlyNewObject(before, GeoButton.class);
			assertEquals("First", first.getRawCaption());
			assertEquals(150, first.getAbsoluteScreenLocX());
			assertEquals(120, first.getAbsoluteScreenLocY());
			assertEquals("Second", second.getRawCaption());
			assertEquals(300, second.getAbsoluteScreenLocX());
			assertEquals(220, second.getAbsoluteScreenLocY());
			assertEquals(Set.of(first, second), getConstructionSnapshot());
		}
	}

	@Test
	void disabledScriptingShouldNotExecuteNormalMoveClick() {
		add("n=0");
		button(INCREMENT);
		getApp().setScriptingDisabled(true);
		setMode(MODE_MOVE);

		click(110, 110);

		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({
		"'',GGBSCRIPT",
		"'SetValue(n,n+1)',GGBSCRIPT",
		"'ggbApplet.setValue(\"n\",1);',JAVASCRIPT"
	})
	void modelShouldStoreScriptWithoutExecutingIt(@NonNull String script, @NonNull ScriptType type) {
		add("n=0");
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, false);

		model.apply("Run", script);

		GeoButton button = onlyButton();
		assertEquals(type, button.getScript(EventType.CLICK).getType());
		assertEquals(script, button.getScript(EventType.CLICK).getText());
		assertEquals(script, model.getClickScript());
		assertEquals("Run", model.getInitString());
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({"false,false", "false,true", "true,false", "true,true"})
	void normalMoveClickShouldExecuteScriptOnce(boolean fixed, boolean rightEnabled) {
		add("n=0");
		GeoButton button = button(INCREMENT);
		button.setFixed(fixed);
		getApp().setRightClickEnabled(rightEnabled);
		setMode(MODE_MOVE);

		click(110, 110);

		assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
		assertEquals(100, button.getAbsoluteScreenLocX());
		assertEquals(100, button.getAbsoluteScreenLocY());
		click(110, 110);
		assertEquals(2, lookup("n").evaluateDouble(), 1E-8);
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	@Issue("APPS-7920")
	void fixedButtonDragShouldNotRunScriptWithAnyPointer(@NonNull PointerEventType type) {
		add("n=0");
		GeoButton button = button(INCREMENT);
		getApp().setRightClickEnabled(false);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);
			ec.setDraggingDelay(0);

			ec.wrapMousePressed(new TestEvent(110, 110, type, false));
			TestEvent end = new TestEvent(210, 160, type, false);
			ec.wrapMouseDragged(end, true);
			ec.wrapMouseReleased(end);

			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(200, button.getAbsoluteScreenLocX());
			assertEquals(150, button.getAbsoluteScreenLocY());
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
			assertEquals(MODE_BUTTON_ACTION, getApp().getMode());
		}
	}

	@Test
	@Issue("APPS-7920")
	void switchingToMoveAfterCreationDragShouldReenableClickScripts() {
		add("n=0");
		button(INCREMENT);
		getApp().setRightClickEnabled(false);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);
			drag(110, 110, 210, 160);
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);

			setMode(MODE_MOVE);
			click(210, 160);
			assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
			verifyNoWidgetCreationDialog(dialogs);

			setMode(MODE_BUTTON_ACTION);
			resetMouseLocation();
			click(210, 160);
			assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(210, 160, false);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void scriptDispatchShouldUseExplicitArgumentOrButtonLabel(boolean explicitArgument) {
		GeoButton source = button("SetCaption(%0,\"Clicked\")");
		GeoButton target = button("");

		getApp().runScripts(source, explicitArgument ? target.getLabelSimple() : null);

		assertEquals(explicitArgument ? "Run" : "Clicked", source.getRawCaption());
		assertEquals(explicitArgument ? "Clicked" : "Run", target.getRawCaption());
	}

	@Test
	@Issue("APPS-7920")
	void creationClickOnExistingButtonShouldNotExecuteScript() {
		add("n=0");
		GeoButton button = button(INCREMENT);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			click(110, 110);

			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(110, 110, false);
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
			assertEquals(100, button.getAbsoluteScreenLocX());
		}
	}

	@ParameterizedTest
	@CsvSource({"false,false", "false,true", "true,false", "true,true"})
	@Issue("APPS-7920")
	void creationDragShouldMoveButtonWithoutRunningScript(boolean fixed, boolean rightEnabled) {
		add("n=0");
		GeoButton button = button(INCREMENT);
		button.setFixed(fixed);
		getApp().setRightClickEnabled(rightEnabled);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			dragStart(110, 110);
			drag(210, 160);
			pointerRelease(210, 160);

			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(200, button.getAbsoluteScreenLocX());
			assertEquals(150, button.getAbsoluteScreenLocY());
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
			assertEquals(MODE_BUTTON_ACTION, getApp().getMode());
		}
	}

	@Test
	@Issue("APPS-7920")
	void pointerJitterShouldRemainCreationClickWithoutMovingOrRunningScript() {
		add("n=0");
		GeoButton button = button(INCREMENT);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			dragStart(110, 110);
			drag(111, 111);
			pointerRelease(111, 111);

			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(111, 111, false);
			assertEquals(100, button.getAbsoluteScreenLocX());
			assertEquals(100, button.getAbsoluteScreenLocY());
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void rightClickShouldNotRunScriptOrRequestDialog(boolean creationMode) {
		add("n=0");
		button(INCREMENT);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(creationMode ? MODE_BUTTON_ACTION : MODE_MOVE);
			TestEvent event = new TestEvent(110, 110, PointerEventType.MOUSE, true);

			ec.wrapMousePressed(event);
			ec.wrapMouseReleased(event);

			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		}
	}

	@Test
	void ordinaryPointDragShouldNotMovePointOrOpenDialog() {
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_BUTTON_ACTION);

			drag(100, 100, 200, 200);

			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(2, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void overlappingPointShouldNotPreventButtonDrag(boolean fixed) {
		GeoButton button = button("");
		button.setFixed(fixed);
		GeoPoint point = add("A=(2.2,-2.2)");
		setMode(MODE_BUTTON_ACTION);

		drag(110, 110, 210, 160);

		assertEquals(200, button.getAbsoluteScreenLocX());
		assertEquals(150, button.getAbsoluteScreenLocY());
		assertEquals(2.2, point.getInhomX(), 1E-8);
		assertEquals(-2.2, point.getInhomY(), 1E-8);
	}

	@Test
	void creationAndScriptShouldHaveSeparateUndoSteps() {
		add("n=0");
		enableUndo();
		button(INCREMENT);
		String label = onlyButton().getLabelSimple();
		setMode(MODE_MOVE);
		click(110, 110);
		assertEquals(1, lookup("n").evaluateDouble(), 1E-8);

		getKernel().undo();
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		assertTrue(lookup(label).isGeoButton());
		getKernel().undo();
		assertNull(lookup(label));
		getKernel().redo();
		assertTrue(lookup(label).isGeoButton());
		getKernel().redo();
		assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
	}

	@Test
	void creationDragShouldBeUndoableWithoutRunningScript() {
		add("n=0");
		button(INCREMENT);
		enableUndo();
		setMode(MODE_BUTTON_ACTION);

		drag(110, 110, 210, 160);

		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		getKernel().undo();
		assertEquals(100, onlyButton().getAbsoluteScreenLocX());
		assertEquals(100, onlyButton().getAbsoluteScreenLocY());
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		getKernel().redo();
		assertEquals(200, onlyButton().getAbsoluteScreenLocX());
		assertEquals(150, onlyButton().getAbsoluteScreenLocY());
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
	}

	@Test
	void xmlRoundTripShouldPreserveCaptionLocationAndExecutableScript() {
		add("n=0");
		GeoButton button = button(INCREMENT);
		String label = button.getLabelSimple();

		getApp().setXML(getApp().getXML(), true);

		GeoButton restored = onlyButton();
		assertEquals(label, restored.getLabelSimple());
		assertEquals("Run", restored.getRawCaption());
		assertEquals(100, restored.getAbsoluteScreenLocX());
		assertEquals(100, restored.getAbsoluteScreenLocY());
		assertTrue(restored.isLocked());
		assertEquals(INCREMENT, restored.getScript(EventType.CLICK).getText());
		setMode(MODE_MOVE);
		click(110, 110);
		assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
	}

	@Test
	void fixedScreenButtonShouldMoveByArrowKeyInButtonTool() {
		GeoButton button = button("");
		button.setAbsoluteScreenLoc(100, 100);
		button.setFixed(true);
		setMode(MODE_BUTTON_ACTION);
		moveByArrowKey(button);
		assertEquals(150, button.getAbsoluteScreenLocX());
		assertEquals(50, button.getAbsoluteScreenLocY());
	}

	@Test
	void fixedWorldButtonShouldMoveByArrowKeyInButtonTool() {
		GeoButton button = button("");
		button.setAbsoluteScreenLocActive(false);
		button.setRealWorldLoc(1, 1);
		button.setFixed(true);
		setMode(MODE_BUTTON_ACTION);
		moveByArrowKey(button);
		assertEquals(2, button.getRealWorldLocX(), 1E-8);
		assertEquals(2, button.getRealWorldLocY(), 1E-8);
	}

	private @NonNull GeoButton button(@NonNull String script) {
		Set<String> before = getObjectNames();
		new ButtonDialogModel(getApp(), 100, 100, false).apply("Run", script);
		return getOnlyNewObject(before, GeoButton.class);
	}

	private @NonNull GeoButton onlyButton() {
		return getOnlyNewObject(Set.of(), GeoButton.class);
	}

	private void moveByArrowKey(@NonNull GeoElement geo) {
		assertTrue(new GlobalKeyDispatcherHeadless(getApp())
				.handleArrowKeyMovement(List.of(geo), new double[] {1, 1, 0}));
	}
}
