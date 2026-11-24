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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TEXTFIELD_ACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GlobalKeyDispatcherHeadless;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests Input Box creation, linking, positioning and persistence through the tool. */
class InputBoxToolTest extends BaseDialogToolTest {
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void buttonGestureShouldSwitchToMoveAndExecuteButtonScript(boolean drag) {
		add("n=0");
		new ButtonDialogModel(getApp(), 100, 100, false).apply("Run", "SetValue(n,n+1)");
		GeoButton button = getOnlyNewObject(Set.of(), GeoButton.class);
		getApp().setRightClickEnabled(false);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			assertEquals(MODE_TEXTFIELD_ACTION, ec.getMode());
			assertEquals(MODE_TEXTFIELD_ACTION, getApp().getMode());

			if (drag) {
				drag(110, 110, 210, 160);
			} else {
				click(110, 110);
			}

			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(MODE_MOVE, getApp().getMode());
			assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
			assertEquals(100, button.getAbsoluteScreenLocX());
			assertEquals(100, button.getAbsoluteScreenLocY());
		}
	}

	@BeforeEach
	void installTextField() {
		getApp().getActiveEuclidianView().setViewTextField(new TextFieldCommonJre());
		SymbolicEditor editor = Mockito.mock(SymbolicEditor.class);
		Mockito.when(editor.getDescription()).thenReturn("2");
		((EuclidianViewNoGui) getApp().getActiveEuclidianView()).setSymbolicEditor(editor);
	}

	@Test
	void enteringAndHoveringShouldNotOpenDialog() {
		add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			moveMouseRW(2, -2);
			verifyNoWidgetCreationDialog(dialogs);
			assertTrue(ec.modeNeedsKeyboard());
			assertFalse(ec.isUndoableMode());
		}
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void clickShouldRequestInputBoxAtScreenPosition(@NonNull PointerEventType type) {
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			click(150, 120, type);
			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(150, 120, true);
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@Test
	void missingMouseLocationShouldNotOpenDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			ec.processMode(new Hits(), false, false);
			verifyNoWidgetCreationDialog(dialogs);
		}
	}

	@Test
	void missingDialogManagerShouldNotCreateAnything() {
		assertNull(getApp().getDialogManager());
		setMode(MODE_TEXTFIELD_ACTION);
		click(150, 120);
		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	void clickingPointShouldRequestDialogWithoutChoosingLinkedObject() {
		GeoPoint point = add("A=(2,-2)");
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_TEXTFIELD_ACTION);
			clickRW(2, -2);
			assertEquals(1, models.size());
			assertTrue(models.get(0).isTextField());
			assertEquals("Input Box", models.get(0).getTitle());
			assertEquals("", models.get(0).getInitString());
			assertEquals("", models.get(0).getClickScript());
			assertEquals(Set.of(point), getConstructionSnapshot());
			models.get(0).apply("Value", "");
			assertTrue(onlyBox().getLinkedGeo().isGeoText());
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void dialogReturnWithoutSubmissionShouldNotCreateInputBox(boolean result) {
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			Mockito.when(dialog(dialogs).showButtonCreationDialog(150, 120, true)).thenReturn(result);
			setMode(MODE_TEXTFIELD_ACTION);
			click(150, 120);
			Mockito.verify(dialog(dialogs)).showButtonCreationDialog(150, 120, true);
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@ParameterizedTest
	@CsvSource(
			value = {"' Value '|Value", "''|''", "'   '|''", "'Δx > 0'|'Δx > 0'"},
			delimiter = '|')
	void submissionShouldCreateVisibleFixedInputBox(
			@NonNull String caption, @NonNull String expectedCaption) {
		GeoElement linked = add("n=2");
		List<ButtonDialogModel> models = new ArrayList<>();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			captureModels(dialog(dialogs), models);
			setMode(MODE_TEXTFIELD_ACTION);
			click(150, 120);
			models.get(0).setLinkedGeo(linked);
			models.get(0).apply(caption, "");
			GeoInputBox box = onlyBox();
			assertSame(linked, box.getLinkedGeo());
			assertTrue(box.isEuclidianVisible());
			assertTrue(box.isLabelVisible());
			assertTrue(box.isLocked());
			assertEquals(150, box.getAbsoluteScreenLocX());
			assertEquals(120, box.getAbsoluteScreenLocY());
			assertEquals(expectedCaption, box.getRawCaption());
			assertEquals(Set.of(linked, box), getConstructionSnapshot());
		}
	}

	@Test
	void submissionWithoutLinkShouldCreateIndependentText() {
		new ButtonDialogModel(getApp(), 150, 120, true).apply("Value", "");
		GeoInputBox box = onlyBox();
		assertTrue(box.getLinkedGeo().isGeoText());
		box.updateLinkedGeo("hello");
		assertEquals("hello", box.getLinkedGeo().toValueString(StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@ValueSource(strings = {"n=2", "A=(2,-2)", "f(x)=x^2"})
	void chosenLinkShouldBePreserved(@NonNull String definition) {
		GeoElement linked = add(definition);
		GeoElement list = add("list=Sequence(3)");
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, true);
		assertTrue(model.getLinkableObjects().contains(linked));
		assertFalse(model.getLinkableObjects().contains(list));
		assertNull(model.getLinkableObjects().get(0));
		model.setLinkedGeo(linked);
		model.apply("Value", "");
		assertSame(linked, onlyBox().getLinkedGeo());
	}

	@Test
	void numericInputShouldUpdateLinkedGeoAndDependents() {
		GeoElement linked = add("n=2");
		add("m=2n");
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, true);
		model.setLinkedGeo(linked);
		model.apply("Value", "");
		onlyBox().updateLinkedGeo("7");
		assertEquals(7, linked.evaluateDouble(), 1E-8);
		assertEquals(14, lookup("m").evaluateDouble(), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({
		"false,false,false", "false,true,false", "true,false,false", "true,true,false",
		"false,false,true", "false,true,true", "true,false,true", "true,true,true"
	})
	@Issue("APPS-7920")
	void creationToolShouldDragFixedAndMovableInputBoxes(
			boolean fixed, boolean rightEnabled, boolean symbolic) {
		GeoInputBox box = box();
		box.setSymbolicMode(symbolic, false);
		box.updateRepaint();
		getDrawable(box).draw(new GGraphicsCommon());
		box.setFixed(fixed);
		getApp().setRightClickEnabled(rightEnabled);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			dragStart(110, 110);
			drag(210, 160);
			pointerRelease(210, 160);
			assertEquals(200, box.getAbsoluteScreenLocX());
			assertEquals(150, box.getAbsoluteScreenLocY());
			verifyNoWidgetCreationDialog(dialogs);
			assertEquals(MODE_TEXTFIELD_ACTION, getApp().getMode());
		}
	}

	@Test
	void overlappingPointShouldNotPreventInputBoxDrag() {
		GeoInputBox box = box();
		GeoPoint point = add("A=(2.2,-2.2)");
		setMode(MODE_TEXTFIELD_ACTION);
		drag(110, 110, 210, 160);
		assertEquals(200, box.getAbsoluteScreenLocX());
		assertEquals(150, box.getAbsoluteScreenLocY());
		assertEquals(2.2, point.getInhomX(), 1E-8);
		assertEquals(-2.2, point.getInhomY(), 1E-8);
	}

	@Test
	void inputBoxClickAndDragShouldNotRunEnterScript() {
		add("n=0");
		GeoInputBox box = box();
		box.setClickScript(getApp().createScript(ScriptType.GGBSCRIPT, "SetValue(n,n+1)", true));
		setMode(MODE_TEXTFIELD_ACTION);
		click(110, 110);
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		resetMouseLocation();
		drag(110, 110, 210, 160);
		assertEquals(200, box.getAbsoluteScreenLocX());
		assertEquals(150, box.getAbsoluteScreenLocY());
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
	}

	@Test
	void enterAndFocusLossShouldSubmitValueAndExecuteScriptOnce() {
		add("n=0");
		GeoInputBox box = box();
		box.setSymbolicMode(false, false);
		box.setClickScript(getApp().createScript(ScriptType.GGBSCRIPT, "SetValue(n,n+1)", true));
		box.updateRepaint();
		setMode(MODE_TEXTFIELD_ACTION);
		click(110, 110);
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);

		AutoCompleteTextFieldC field =
				(AutoCompleteTextFieldC) ((DrawInputBox) getDrawable(box)).getTextField();
		assertSame(box, field.getInputBox());
		field.setText("7");
		field.onEnter();
		assertEquals(7, box.getLinkedGeo().evaluateDouble(), 1E-8);
		assertEquals(0, lookup("n").evaluateDouble(), 1E-8);
		field.blur();

		assertEquals(7, box.getLinkedGeo().evaluateDouble(), 1E-8);
		assertEquals(1, lookup("n").evaluateDouble(), 1E-8);
	}

	@Test
	void defaultCaptionShouldNotPreventDraggingByInputField() {
		GeoElement linked = add("value=2");
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, true);
		model.setLinkedGeo(linked);
		model.apply("Value", "");
		GeoInputBox box = onlyBox();
		DrawInputBox drawable = (DrawInputBox) getDrawable(box);
		drawable.draw(new GGraphicsCommon());
		GRectangle bounds = drawable.getInputFieldBounds();
		int x = (int) bounds.getX() + 10;
		int y = (int) bounds.getY() + 10;
		setMode(MODE_TEXTFIELD_ACTION);

		drag(x, y, x + 100, y + 50);

		assertEquals(200, box.getAbsoluteScreenLocX());
		assertEquals(150, box.getAbsoluteScreenLocY());
		assertEquals("Value", box.getRawCaption());
		assertSame(linked, box.getLinkedGeo());
	}

	@Test
	void rightClickShouldNotOpenCreationDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_TEXTFIELD_ACTION);
			TestEvent event = new TestEvent(100, 100, PointerEventType.MOUSE, true);
			ec.wrapMousePressed(event);
			ec.wrapMouseReleased(event);
			verifyNoWidgetCreationDialog(dialogs);
		}
	}

	@Test
	void creationShouldBeUndoableAndRestoreLinkOnRedo() {
		GeoElement linked = add("n=2");
		enableUndo();
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, true);
		model.setLinkedGeo(linked);
		model.apply("Value", "");
		String label = onlyBox().getLabelSimple();
		getKernel().undo();
		assertNull(lookup(label));
		assertEquals(2, lookup("n").evaluateDouble(), 1E-8);
		getKernel().redo();
		assertSame(lookup("n"), onlyBox().getLinkedGeo());
		assertEquals("Value", onlyBox().getRawCaption());
	}

	@Test
	void draggingShouldBeUndoableWithoutChangingLinkedValue() {
		box();
		enableUndo();
		setMode(MODE_TEXTFIELD_ACTION);
		drag(110, 110, 210, 160);
		assertEquals(200, onlyBox().getAbsoluteScreenLocX());
		assertEquals(150, onlyBox().getAbsoluteScreenLocY());

		getKernel().undo();
		assertEquals(100, onlyBox().getAbsoluteScreenLocX());
		assertEquals(100, onlyBox().getAbsoluteScreenLocY());
		getKernel().redo();
		assertEquals(200, onlyBox().getAbsoluteScreenLocX());
		assertEquals(150, onlyBox().getAbsoluteScreenLocY());
		assertEquals(2, onlyBox().getLinkedGeo().evaluateDouble(), 1E-8);
	}

	@Test
	void xmlRoundTripShouldPreserveLinkCaptionAndPosition() {
		GeoInputBox box = box();
		box.setCaption("Value");
		String linkedLabel = box.getLinkedGeo().getLabelSimple();
		getApp().setXML(getApp().getXML(), true);
		GeoInputBox restored = onlyBox();
		assertSame(lookup(linkedLabel), restored.getLinkedGeo());
		assertEquals("Value", restored.getRawCaption());
		assertEquals(100, restored.getAbsoluteScreenLocX());
		assertEquals(100, restored.getAbsoluteScreenLocY());
		assertTrue(restored.isLocked());
	}

	@Test
	void fixedInputBoxShouldMoveByArrowKeysInOwnTool() {
		GeoInputBox box = box();
		setMode(MODE_TEXTFIELD_ACTION);
		assertTrue(new GlobalKeyDispatcherHeadless(getApp())
				.handleArrowKeyMovement(List.of(box), new double[] {1, 1, 0}));
		assertEquals(150, box.getAbsoluteScreenLocX());
		assertEquals(50, box.getAbsoluteScreenLocY());
	}

	private @NonNull GeoInputBox box() {
		ButtonDialogModel model = new ButtonDialogModel(getApp(), 100, 100, true);
		model.setLinkedGeo(add("value=2"));
		model.apply("", "");
		GeoInputBox box = onlyBox();
		box.setLabelVisible(false);
		box.updateRepaint();
		getDrawable(box).draw(new GGraphicsCommon());
		assertTrue(
				getDrawable(box).hit(110, 110, 0),
				() -> "Input box bounds: " + ((DrawInputBox) getDrawable(box)).getInputFieldBounds());
		return box;
	}

	private @NonNull GeoInputBox onlyBox() {
		return getOnlyNewObject(Set.of(), GeoInputBox.class);
	}
}
