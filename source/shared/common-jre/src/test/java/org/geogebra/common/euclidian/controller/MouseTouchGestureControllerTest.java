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

package org.geogebra.common.euclidian.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the two-finger behaviour of {@link MouseTouchGestureController} (the path used by Web)
 * and the single-finger tool flows with touch events.
 */
@Issue("APPS-8112")
class MouseTouchGestureControllerTest extends BaseEuclidianControllerTest {

	private static final double DELTA = 1E-6;

	private MouseTouchGestureController gestureController;

	@AfterEach
	void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@BeforeEach
	void setUp() {
		setUpController();
		ec.setDraggingDelay(0);
		gestureController = new MouseTouchGestureController(getApp(), ec);
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	private void tap(int x, int y) {
		click(x, y, PointerEventType.TOUCH);
	}

	private EuclidianView view() {
		return getApp().getActiveEuclidianView();
	}

	private void showAxesWithOriginAt(int x, int y) {
		view().setShowAxes(true, true);
		view().setCoordSystem(x, y, 50, 50);
	}

	// single finger

	@Test
	void pointToolTapShouldCreatePoint() {
		setMode(EuclidianConstants.MODE_POINT);
		tap(100, 100);
		checkContent("A = (2, -2)");
	}

	@Test
	void segmentToolTwoTapsShouldCreateSegment() {
		setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		tap(200, 100);
		checkContent("A = (2, -2)", "B = (4, -2)", "f = 2");
	}

	@Test
	void customToolTwoTapsShouldCreateOutput() {
		GeoElement start = add("P = (0, 0)");
		GeoElement end = add("Q = (1, 0)");
		GeoElement midpoint = add("M = Midpoint(P, Q)");
		ToolCreationDialogModel macroBuilder = new ToolCreationDialogModel(getApp(), () -> {
			/* no UI to update */
		});
		macroBuilder.addToInput(start);
		macroBuilder.addToInput(end);
		macroBuilder.addToOutput(midpoint);
		macroBuilder.createTool();
		macroBuilder.finish(getApp(), "Mid", "Mid", "2 points", false, null);
		Macro macro = getKernel().getMacro("Mid");
		getKernel().clearConstruction(false);

		setMode(EuclidianConstants.MACRO_MODE_ID_OFFSET + getKernel().getMacroID(macro));
		tap(100, 100);
		tap(200, 100);
		checkContent("A = (2, -2)", "B = (4, -2)", "C = (3, -2)");
	}

	@Test
	void moveToolDragShouldMovePoint() {
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		ec.wrapMousePressed(touch(100, 100));
		ec.wrapMouseDragged(touch(150, 100), true);
		ec.wrapMouseDragged(touch(200, 100), true);
		ec.wrapMouseReleased(touch(200, 100));
		checkContent("A = (4, -2)");
	}

	@Test
	void moveToolDragOnBackgroundShouldPanView() {
		setMode(EuclidianConstants.MODE_MOVE);
		ec.wrapMousePressed(touch(100, 100));
		ec.wrapMouseDragged(touch(130, 120), true);
		ec.wrapMouseDragged(touch(160, 140), true);
		ec.wrapMouseReleased(touch(160, 140));
		assertEquals(60, view().getXZero(), DELTA);
		assertEquals(40, view().getYZero(), DELTA);
		assertEquals(50, view().getXscale(), DELTA);
	}

	// two fingers: navigation

	@Test
	void twoFingersOnBackgroundShouldZoomInMoveTool() {
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(100, 300, 300, 300);
		gestureController.twoTouchMove(50, 300, 350, 300);
		assertEquals(75, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		// the point under the centre of the gesture stays where it is
		assertEquals(4, view().toRealWorldCoordX(200), DELTA);
		assertEquals(-6, view().toRealWorldCoordY(300), DELTA);
	}

	@Test
	void twoFingersOnBackgroundShouldPanInMoveTool() {
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(100, 300, 300, 300);
		gestureController.twoTouchMove(140, 340, 340, 340);
		assertEquals(40, view().getXZero(), DELTA);
		assertEquals(40, view().getYZero(), DELTA);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
	}

	@Test
	void twoFingersOnBackgroundShouldPanAndZoomInOneMove() {
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(100, 300, 300, 300);
		gestureController.twoTouchMove(90, 340, 390, 340);
		// zoom by 1.5 around the new centre (240, 340), then pan by the centre's movement (40, 40)
		assertEquals(75, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		assertEquals(-80, view().getXZero(), DELTA);
		assertEquals(-130, view().getYZero(), DELTA);
	}

	@Test
	void twoFingersShouldNotNavigateWhenZoomIsDisabled() {
		getApp().setShiftDragZoomEnabled(false);
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(100, 300, 300, 300);
		gestureController.twoTouchMove(90, 340, 390, 340);
		assertEquals(0, view().getXZero(), DELTA);
		assertEquals(0, view().getYZero(), DELTA);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
	}

	@Test
	void twoFingersOnXAxisShouldZoomXAxisOnly() {
		showAxesWithOriginAt(400, 300);
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(500, 300, 600, 300);
		gestureController.twoTouchMove(500, 300, 700, 300);
		assertEquals(100, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		assertEquals(400, view().getXZero(), DELTA);
		assertEquals(300, view().getYZero(), DELTA);
	}

	@Test
	void twoFingersOnYAxisShouldZoomYAxisOnly() {
		showAxesWithOriginAt(400, 300);
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(400, 100, 400, 200);
		gestureController.twoTouchMove(400, 100, 400, 250);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		assertEquals(400, view().getXZero(), DELTA);
		assertEquals(300, view().getYZero(), DELTA);
	}

	@Test
	void pinchShouldBeOneUndoStepThatRestoresTheView() {
		getApp().setUndoActive(true);
		getKernel().initUndoInfo();
		setMode(EuclidianConstants.MODE_POINT);
		tap(100, 100);
		UndoManager undoManager = getConstruction().getUndoManager();
		int historySize = undoManager.getHistorySize();

		setMode(EuclidianConstants.MODE_MOVE);
		ec.wrapMousePressed(touch(100, 300));
		gestureController.twoTouchStart(100, 300, 300, 300);
		gestureController.twoTouchMove(50, 300, 350, 300);
		ec.wrapMouseReleased(touch(50, 300));
		ec.wrapMouseReleased(touch(350, 300));
		assertEquals(historySize + 1, undoManager.getHistorySize());
		checkContent("A = (2, -2)");

		undoManager.undo();
		assertEquals(50, view().getXscale(), DELTA);
		checkContent("A = (2, -2)");
		undoManager.undo();
		checkContent();
	}

	// navigation that the tool got no input from

	private void navigate(
			double x1,
			double y1,
			double x2,
			double y2,
			double newX1,
			double newY1,
			double newX2,
			double newY2) {
		gestureController.touchNavigationStart(x1, y1, x2, y2);
		gestureController.twoTouchMove(newX1, newY1, newX2, newY2);
		ec.touchNavigationEnd();
	}

	@Test
	void segmentShouldContinueAfterNavigation() {
		setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		navigate(300, 300, 500, 300, 250, 300, 550, 300);
		assertEquals(75, view().getXscale(), DELTA);
		assertEquals(-200, view().getXZero(), DELTA);
		assertEquals(-150, view().getYZero(), DELTA);
		checkContent("A = (2, -2)");
		assertEquals(EuclidianConstants.MODE_SEGMENT, ec.getMode());

		tap(400, 150);
		checkContent("A = (2, -2)", "B = (8, -4)", "f = 6.32456");
	}

	@Test
	void polygonShouldKeepInputOrderAcrossNavigation() {
		setMode(EuclidianConstants.MODE_POLYGON);
		tap(100, 100);
		tap(200, 100);
		navigate(300, 300, 500, 300, 400, 350, 600, 350);
		assertEquals(100, view().getXZero(), DELTA);
		assertEquals(50, view().getYZero(), DELTA);

		tap(200, 250);
		tap(200, 150);
		checkContent(
				"A = (2, -2)", "B = (4, -2)", "C = (2, -4)", "t1 = 2", "c = 2", "a = 2.82843", "b = 2");
	}

	@Test
	void navigationOverCircleShouldNotScaleItWithConstructionTool() {
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_POINT);
		navigate(200, 100, 200, 300, 200, 50, 200, 350);
		checkContent("A = (4, -4)", "B = (6, -4)", "c: (x - 4)² + (y + 4)² = 4");
		assertEquals(75, view().getXscale(), DELTA);
	}

	@Test
	void navigationOverSegmentShouldNotMoveItWithConstructionTool() {
		add("A = (2, -4)");
		add("B = (6, -4)");
		add("f = Segment(A, B)");
		setMode(EuclidianConstants.MODE_POINT);
		navigate(150, 200, 250, 200, 150, 250, 250, 250);
		checkContent("A = (2, -4)", "B = (6, -4)", "f = 4");
		assertEquals(50, view().getYZero(), DELTA);
	}

	@Test
	void navigationOverCircleShouldScaleItWithMoveTool() {
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		navigate(200, 100, 200, 300, 200, 50, 200, 350);
		checkContent("A = (4, -4)", "B = (7, -4)", "c: (x - 4)² + (y + 4)² = 9");
		assertEquals(50, view().getXscale(), DELTA);
	}

	@Test
	void navigationOverCircleWithMoveToolShouldBeOneUndoStep() {
		getApp().setUndoActive(true);
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		getKernel().initUndoInfo();
		setMode(EuclidianConstants.MODE_MOVE);
		UndoManager undoManager = getConstruction().getUndoManager();
		int historySize = undoManager.getHistorySize();

		navigate(200, 100, 200, 300, 200, 50, 200, 350);
		assertEquals(historySize + 1, undoManager.getHistorySize());

		undoManager.undo();
		checkContent("A = (4, -4)", "B = (6, -4)", "c: (x - 4)² + (y + 4)² = 4");
	}

	@Test
	void navigationOnXAxisShouldZoomAxisWithConstructionTool() {
		showAxesWithOriginAt(400, 300);
		setMode(EuclidianConstants.MODE_POINT);
		navigate(500, 300, 600, 300, 500, 300, 700, 300);
		assertEquals(100, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		checkContent();
	}

	@Test
	void navigationShouldBeOneUndoStepWithoutPress() {
		getApp().setUndoActive(true);
		getKernel().initUndoInfo();
		setMode(EuclidianConstants.MODE_POINT);
		tap(100, 100);
		UndoManager undoManager = getConstruction().getUndoManager();
		int historySize = undoManager.getHistorySize();

		navigate(300, 300, 500, 300, 250, 300, 550, 300);
		assertEquals(historySize + 1, undoManager.getHistorySize());
		checkContent("A = (2, -2)");
		undoManager.undo();
		assertEquals(50, view().getXscale(), DELTA);
		checkContent("A = (2, -2)");
	}

	@Test
	void navigationShouldNotNavigateWhenZoomIsDisabled() {
		getApp().setShiftDragZoomEnabled(false);
		setMode(EuclidianConstants.MODE_POINT);
		navigate(300, 300, 500, 300, 250, 300, 550, 300);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(0, view().getXZero(), DELTA);
		checkContent();
	}

	@Test
	void touchGestureArbiterShouldBeUsedOnlyAsPreviewFeature() {
		assertFalse(ec.usesTouchGestureArbiter());
		assertNull(gestureController.beginPhoneTouch());

		PreviewFeature.setPreviewFeaturesEnabled(true);
		assertTrue(ec.usesTouchGestureArbiter());
		assertNotNull(gestureController.beginPhoneTouch());
	}

	@Test
	void notesShouldNotUseTouchGestureArbiter() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		getApp().setNotesConfig();
		assertFalse(ec.usesTouchGestureArbiter());
		assertNull(gestureController.beginPhoneTouch());
	}

	@Test
	void view3DShouldNotUseTouchGestureArbiter() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		EuclidianController controller3D = getApp().getEuclidianView3D().getEuclidianController();
		assertFalse(controller3D.usesTouchGestureArbiter());
	}

	// two fingers on the same object in the Move tool

	@Test
	void twoFingersOnCircleThroughPointShouldScaleCircleInMoveTool() {
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(200, 100, 200, 300);
		gestureController.twoTouchMove(200, 50, 200, 350);
		checkContent("A = (4, -4)", "B = (7, -4)", "c: (x - 4)² + (y + 4)² = 9");
		assertEquals(50, view().getXscale(), DELTA);
	}

	@Test
	void twoFingersOnCircleWithRadiusShouldScaleCircleInMoveTool() {
		add("A = (4, -4)");
		add("c = Circle(A, 2)");
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(200, 100, 200, 300);
		gestureController.twoTouchMove(200, 50, 200, 350);
		checkContent("A = (4, -4)", "c: (x - 4)² + (y + 4)² = 9");
		assertEquals(50, view().getXscale(), DELTA);
	}

	@Test
	void twoFingersOnSegmentShouldMoveSegmentInMoveTool() {
		add("A = (2, -4)");
		add("B = (6, -4)");
		add("f = Segment(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		gestureController.twoTouchStart(150, 200, 250, 200);
		gestureController.twoTouchMove(150, 250, 250, 250);
		GeoPoint start = (GeoPoint) lookup("A");
		GeoPoint end = (GeoPoint) lookup("B");
		assertTrue(start.getInhomY() < -4.5, "segment should follow the fingers");
		assertEquals(start.getInhomY(), end.getInhomY(), DELTA);
		assertEquals(2, start.getInhomX(), DELTA);
		assertEquals(6, end.getInhomX(), DELTA);
		assertEquals(0, view().getYZero(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
	}
}
