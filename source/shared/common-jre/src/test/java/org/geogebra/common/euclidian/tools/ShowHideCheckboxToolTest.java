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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DELETE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.EventAccumulator;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests checkbox dialog handoff, positioning, dragging and visibility control. */
class ShowHideCheckboxToolTest extends BaseToolTest {

	@Test
	void enteringAndHoveringShouldNotOpenDialogOrChangeConstruction() {
		GeoPoint point = add("A=(2,-2)");
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			moveMouseRW(2, -2);
			moveMouseRW(4, -4);

			verifyNoDialog(dialogs);
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
			assertTrue(point.isEuclidianVisible());
		}
	}

	@Test
	@Issue("APPS-7920")
	void missingMouseLocationShouldNotOpenDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			ec.processMode(new Hits(), false, false);

			verifyNoDialog(dialogs);
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
			assertTrue(getConstructionSnapshot().isEmpty());
		}
	}

	@Test
	@Issue("APPS-7920")
	void missingDialogManagerShouldNotThrowOrCreateObjects() {
		assertNull(getApp().getDialogManager());
		setMode(MODE_SHOW_HIDE_CHECKBOX);

		click(150, 120);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void clickShouldRequestNewCheckboxAtScreenPositionForEachPointer(
			@NonNull PointerEventType pointer) {
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			click(150, 120, pointer);

			assertDialogAt(dialogs, 150, 120);
			assertEquals(MODE_MOVE, getApp().getMode());
			assertTrue(getConstructionSnapshot().isEmpty());
			assertFalse(getKernel().undoPossible());
		}
	}

	@Test
	void viewScaleAndOffsetShouldNotChangeRequestedScreenPosition() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			click(150, 120);

			assertDialogAt(dialogs, 150, 120);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void outsideClickShouldClampRequestedLocationToView(boolean beyondEnd) {
		int width = getApp().getActiveEuclidianView().getViewWidth();
		int height = getApp().getActiveEuclidianView().getViewHeight();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			click(beyondEnd ? width + 20 : -20, beyondEnd ? height + 20 : -20);

			assertDialogAt(dialogs, beyondEnd ? width : 0, beyondEnd ? height : 0);
		}
	}

	@Test
	void clickOnPointShouldOpenCreationDialogWithoutMovingOrHidingPoint() {
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			clickRW(2, -2);

			assertDialogAt(dialogs, 100, 100);
			assertEquals(Set.of(point), getConstructionSnapshot());
			assertTrue(point.isEuclidianVisible());
			assertEquals(2, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
		}
	}

	@Test
	void subsequentMoveClickShouldNotRequestAnotherDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);
			click(150, 120);

			click(250, 220);

			assertDialogAt(dialogs, 150, 120);
			assertEquals(MODE_MOVE, getApp().getMode());
		}
	}

	@Test
	void reactivatingToolShouldRequestIndependentDialogPosition() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);
			click(150, 120);
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			click(250, 220);

			ArgumentCaptor<GPoint> positions = ArgumentCaptor.forClass(GPoint.class);
			Mockito.verify(dialog(dialogs), Mockito.times(2))
					.showBooleanCheckboxCreationDialog(positions.capture(), Mockito.isNull());
			assertEquals(new GPoint(150, 120), positions.getAllValues().get(0));
			assertEquals(new GPoint(250, 220), positions.getAllValues().get(1));
			assertTrue(getConstructionSnapshot().isEmpty());
		}
	}

	@Test
	void requestedPositionShouldRemainStableAfterMouseMovement() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);
			click(150, 120);
			ArgumentCaptor<GPoint> position = ArgumentCaptor.forClass(GPoint.class);
			Mockito.verify(dialog(dialogs))
					.showBooleanCheckboxCreationDialog(position.capture(), Mockito.isNull());

			moveMouseRW(5, -5);

			assertEquals(new GPoint(150, 120), position.getValue());
		}
	}

	@Test
	void rightClickShouldNotRequestCreationDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);
			TestEvent event = new TestEvent(150, 120, PointerEventType.MOUSE, true);

			ec.wrapMousePressed(event);
			ec.wrapMouseReleased(event);

			verifyNoDialog(dialogs);
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
			assertTrue(getConstructionSnapshot().isEmpty());
		}
	}

	@Test
	@Issue("APPS-7920")
	void disabledRightDragShouldNotMoveCheckboxOrChangeControllerMode() {
		GeoBoolean checkbox = checkbox();
		getApp().setRightClickEnabled(false);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			dragStart(110, 110, true);
			dragEnd(210, 160, true);

			verifyNoDialog(dialogs);
			assertEquals(100, checkbox.getAbsoluteScreenLocX());
			assertEquals(100, checkbox.getAbsoluteScreenLocY());
			assertTrue(checkbox.getBoolean());
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, ec.getMode());
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
		}
	}

	@Test
	@Issue("APPS-7920")
	void rectangleSelectionShouldBeAvailableWhenDialogOpens() {
		GeoPoint first = add("A=(2,-2)");
		GeoPoint second = add("B=(3,-3)");
		GeoPoint outside = add("C=(6,-6)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);
			drag(70, 70, 180, 180);
			verifyNoDialog(dialogs);
			assertEquals(
					Set.of(first, second), Set.copyOf(getApp().getSelectionManager().getSelectedGeos()));
			List<GeoElement> selectedAtOpen = new ArrayList<>();
			Mockito.doAnswer(invocation -> {
						selectedAtOpen.addAll(getApp().getSelectionManager().getSelectedGeos());
						return null;
					})
					.when(dialog(dialogs))
					.showBooleanCheckboxCreationDialog(Mockito.any(), Mockito.isNull());

			click(250, 220);

			assertDialogAt(dialogs, 250, 220);
			assertEquals(Set.of(first, second), Set.copyOf(selectedAtOpen));
			assertEquals(Set.of(first, second, outside), getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@CsvSource({"false,false", "false,true", "true,false", "true,true"})
	@Issue("APPS-7920")
	void draggingExistingCheckboxShouldMoveItWithoutTogglingOrOpeningDialog(
			boolean fixed, boolean value) {
		GeoBoolean checkbox = checkbox();
		checkbox.setCheckboxFixed(fixed);
		checkbox.setValue(value);
		checkbox.updateRepaint();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			drag(110, 110, 210, 160);

			verifyNoDialog(dialogs);
			assertEquals(200, checkbox.getAbsoluteScreenLocX());
			assertEquals(150, checkbox.getAbsoluteScreenLocY());
			assertEquals(value, checkbox.getBoolean());
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
			assertEquals(Set.of(checkbox), getConstructionSnapshot());
		}
	}

	@Test
	@Issue("APPS-7920")
	void draggingOtherObjectShouldNotMoveItOrOpenDialog() {
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			drag(100, 100, 200, 200);

			verifyNoDialog(dialogs);
			assertEquals(2, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
			assertEquals(MODE_SHOW_HIDE_CHECKBOX, getApp().getMode());
		}
	}

	@Test
	@Issue("APPS-7920")
	void overlappingPointShouldNotPreventCheckboxDragOrMoveThePoint() {
		GeoBoolean checkbox = checkbox();
		GeoPoint point = add("A=(2.2,-2.2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			drag(110, 110, 210, 160);

			verifyNoDialog(dialogs);
			assertEquals(200, checkbox.getAbsoluteScreenLocX());
			assertEquals(150, checkbox.getAbsoluteScreenLocY());
			assertTrue(checkbox.getBoolean());
			assertEquals(2.2, point.getInhomX(), 1E-8);
			assertEquals(-2.2, point.getInhomY(), 1E-8);
		}
	}

	@Test
	@Issue("APPS-7920")
	void overlappingLabelShouldNotPreventCheckboxDragOrMoveTheLabel() {
		GeoBoolean checkbox = checkbox();
		GeoPoint point = add("A=(0,-2)");
		point.setLabelVisible(true);
		point.labelOffsetX = 100;
		point.labelOffsetY = 20;
		point.updateRepaint();
		getDrawable(point)
				.draw(getApp()
						.getActiveEuclidianView()
						.getTempGraphics2D(getApp().getActiveEuclidianView().getFontPoint()));
		assertSame(
				point,
				getApp()
						.getActiveEuclidianView()
						.getLabelHit(new GPoint(107, 105), PointerEventType.MOUSE));
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			drag(107, 105, 207, 155);

			verifyNoDialog(dialogs);
			assertEquals(200, checkbox.getAbsoluteScreenLocX());
			assertEquals(150, checkbox.getAbsoluteScreenLocY());
			assertEquals(100, point.labelOffsetX);
			assertEquals(20, point.labelOffsetY);
		}
	}

	@Test
	@Issue("APPS-7920")
	void draggingOntoAnotherCheckboxShouldNotToggleEitherCheckbox() {
		GeoBoolean first = checkbox();
		first.setCheckboxFixed(false);
		GeoBoolean second = add("other=Checkbox()");
		second.setValue(false);
		second.setAbsoluteScreenLoc(200, 150);
		second.updateRepaint();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			drag(110, 110, 210, 160);

			verifyNoDialog(dialogs);
			assertEquals(200, first.getAbsoluteScreenLocX());
			assertEquals(150, first.getAbsoluteScreenLocY());
			assertTrue(first.getBoolean());
			assertFalse(second.getBoolean());
			assertEquals(200, second.getAbsoluteScreenLocX());
			assertEquals(150, second.getAbsoluteScreenLocY());
		}
	}

	@ParameterizedTest
	@CsvSource({"false,false", "false,true", "true,false", "true,true"})
	@Issue("APPS-7920")
	void clickingExistingCheckboxShouldRequestNewDialogWithoutToggling(boolean fixed, boolean value) {
		GeoBoolean checkbox = checkbox();
		checkbox.setCheckboxFixed(fixed);
		checkbox.setValue(value);
		checkbox.updateRepaint();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			click(110, 110);

			assertDialogAt(dialogs, 110, 110);
			assertEquals(value, checkbox.getBoolean());
			assertEquals(100, checkbox.getAbsoluteScreenLocX());
			assertEquals(100, checkbox.getAbsoluteScreenLocY());
			assertEquals(Set.of(checkbox), getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void pointerJitterShouldRemainCreationClickWithoutMovingOrTogglingCheckbox(boolean value) {
		GeoBoolean checkbox = checkbox();
		checkbox.setCheckboxFixed(false);
		checkbox.setValue(value);
		checkbox.updateRepaint();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			dragStart(110, 110);
			drag(111, 111);
			pointerRelease(111, 111);

			assertDialogAt(dialogs, 111, 111);
			assertEquals(100, checkbox.getAbsoluteScreenLocX());
			assertEquals(100, checkbox.getAbsoluteScreenLocY());
			assertEquals(value, checkbox.getBoolean());
		}
	}

	@Test
	@Issue("APPS-7920")
	void dragShouldStartAfterPointerMovesBeyondInitialJitter() {
		GeoBoolean checkbox = checkbox();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_SHOW_HIDE_CHECKBOX);

			dragStart(110, 110);
			drag(111, 111);
			drag(210, 160);
			pointerRelease(210, 160);

			verifyNoDialog(dialogs);
			assertEquals(200, checkbox.getAbsoluteScreenLocX());
			assertEquals(150, checkbox.getAbsoluteScreenLocY());
			assertTrue(checkbox.getBoolean());
		}
	}

	@ParameterizedTest
	@CsvSource({"1,false", "1,true", "2,false", "2,true", "3,false", "3,true"})
	@Issue("APPS-7920")
	void checkboxDragShouldBeUndoableWithoutChangingItsValue(
			int dragEvents, boolean rightClickEnabled) {
		GeoBoolean checkbox = checkbox();
		getApp().setRightClickEnabled(rightClickEnabled);
		enableUndo();
		setMode(MODE_SHOW_HIDE_CHECKBOX);

		dragStart(110, 110);
		for (int i = 0; i < dragEvents; i++) {
			drag(210, 160);
		}
		pointerRelease(210, 160);

		assertEquals(200, checkbox.getAbsoluteScreenLocX());
		assertTrue(checkbox.getBoolean());
		getKernel().undo();
		GeoBoolean restored = (GeoBoolean) lookup("show");
		assertEquals(100, restored.getAbsoluteScreenLocX());
		assertEquals(100, restored.getAbsoluteScreenLocY());
		assertTrue(restored.getBoolean());
		getKernel().redo();
		GeoBoolean redone = (GeoBoolean) lookup("show");
		assertEquals(200, redone.getAbsoluteScreenLocX());
		assertEquals(150, redone.getAbsoluteScreenLocY());
		assertTrue(redone.getBoolean());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void checkboxInMoveModeShouldToggleAllLinkedObjectsOnce(boolean fixed)
			throws CircularDefinitionException {
		GeoBoolean checkbox = checkbox();
		checkbox.setCheckboxFixed(fixed);
		GeoPoint point = add("A=(4,-4)");
		GeoText text = add("text=\"linked\"");
		point.setShowObjectCondition(checkbox);
		text.setShowObjectCondition(checkbox);
		EventAccumulator events = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(events);
		setMode(MODE_MOVE);

		click(110, 110);

		assertFalse(checkbox.getBoolean());
		assertFalse(point.isEuclidianVisible());
		assertFalse(text.isEuclidianVisible());
		assertEquals(1, events.getEvents().stream().filter("UPDATE show"::equals).count());
		resetMouseLocation();
		click(110, 110);
		assertTrue(checkbox.getBoolean());
		assertTrue(point.isEuclidianVisible());
		assertTrue(text.isEuclidianVisible());
	}

	@Test
	void hitBooleanWithDynamicCaptionShouldBeOneEvent() {
		GeoBoolean checkbox = checkbox();
		GeoText caption = add("caption=\"foo\"");
		checkbox.setDynamicCaption(caption);
		checkbox.updateRepaint();
		EventAccumulator events = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(events);
		setMode(MODE_MOVE);

		click(101, 101);

		assertFalse(checkbox.getBoolean());
		assertEquals(
				List.of("UPDATE show"),
				events.getEvents().stream().filter(event -> event.startsWith("UPDATE")).toList());
	}

	@Test
	void removingCheckboxShouldRemoveVisibilityConditionWithoutDeletingLinkedPoint()
			throws CircularDefinitionException {
		GeoBoolean checkbox = checkbox();
		GeoPoint point = add("A=(4,-4)");
		point.setShowObjectCondition(checkbox);
		checkbox.setValue(false);
		checkbox.updateRepaint();
		assertFalse(point.isEuclidianVisible());
		setMode(MODE_DELETE);

		click(110, 110);

		assertEquals(Set.of(point), getConstructionSnapshot());
		assertNull(point.getShowObjectCondition());
		assertTrue(point.isEuclidianVisible());
	}

	@Test
	void togglingCheckboxShouldBeUndoableWithLinkedVisibility() throws CircularDefinitionException {
		GeoBoolean checkbox = checkbox();
		GeoPoint point = add("A=(4,-4)");
		point.setShowObjectCondition(checkbox);
		enableUndo();
		setMode(MODE_MOVE);

		click(110, 110);

		assertFalse(point.isEuclidianVisible());
		getKernel().undo();
		GeoBoolean restored = (GeoBoolean) lookup("show");
		assertTrue(restored.getBoolean());
		assertSame(restored, lookup("A").getShowObjectCondition());
		assertTrue(lookup("A").isEuclidianVisible());
		getKernel().redo();
		GeoBoolean redone = (GeoBoolean) lookup("show");
		assertFalse(redone.getBoolean());
		assertFalse(lookup("A").isEuclidianVisible());
	}

	@Test
	void xmlRoundTripShouldPreserveCheckboxLocationCaptionAndLinks()
			throws CircularDefinitionException {
		GeoBoolean checkbox = checkbox();
		checkbox.setCaption("Show A");
		GeoPoint point = add("A=(4,-4)");
		point.setShowObjectCondition(checkbox);
		checkbox.setValue(false);
		checkbox.updateRepaint();

		getApp().setXML(getApp().getXML(), true);

		GeoBoolean restored = (GeoBoolean) lookup("show");
		assertEquals("Show A", restored.getRawCaption());
		assertEquals(100, restored.getAbsoluteScreenLocX());
		assertEquals(100, restored.getAbsoluteScreenLocY());
		assertFalse(restored.getBoolean());
		assertSame(restored, lookup("A").getShowObjectCondition());
		assertFalse(lookup("A").isEuclidianVisible());
		setMode(MODE_MOVE);
		click(110, 110);
		assertTrue(restored.getBoolean());
		assertTrue(lookup("A").isEuclidianVisible());
	}

	private @NonNull GeoBoolean checkbox() {
		GeoBoolean checkbox = add("show=Checkbox()");
		checkbox.setAbsoluteScreenLoc(100, 100);
		checkbox.updateRepaint();
		return checkbox;
	}

	private void enableUndo() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
	}

	private @NonNull MockedConstruction<DialogManagerNoGui> mockDialogs() {
		MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class);
		getApp().initDialogManager(false);
		return dialogs;
	}

	private @NonNull DialogManagerNoGui dialog(
			@NonNull MockedConstruction<DialogManagerNoGui> dialogs) {
		assertEquals(1, dialogs.constructed().size());
		return dialogs.constructed().get(0);
	}

	private void assertDialogAt(
			@NonNull MockedConstruction<DialogManagerNoGui> dialogs, int x, int y) {
		ArgumentCaptor<GPoint> point = ArgumentCaptor.forClass(GPoint.class);
		Mockito.verify(dialog(dialogs))
				.showBooleanCheckboxCreationDialog(point.capture(), Mockito.isNull());
		assertEquals(new GPoint(x, y), point.getValue());
	}

	private void verifyNoDialog(@NonNull MockedConstruction<DialogManagerNoGui> dialogs) {
		Mockito.verify(dialog(dialogs), Mockito.never())
				.showBooleanCheckboxCreationDialog(Mockito.any(), Mockito.any());
	}
}
