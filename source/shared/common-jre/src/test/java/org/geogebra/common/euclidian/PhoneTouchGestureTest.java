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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the behaviour of the touch entry points used by Android and iOS
 * ({@code touchStartPhone}, {@code touchMovePhone}, {@code touchEndPhone},
 * {@code twoTouchStartPhone}, {@code twoTouchMovePhone}).
 */
@Issue("APPS-8112")
class PhoneTouchGestureTest extends BaseEuclidianControllerTest {

	private static final double DELTA = 1E-6;

	@BeforeEach
	void setUp() {
		setUpController();
		ec.setDraggingDelay(0);
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	private void touchStart(int x, int y) {
		ec.touchStartPhone(touch(x, y));
	}

	private void touchMove(int x, int y) {
		ec.touchMovePhone(touch(x, y));
	}

	private void touchEnd(int x, int y) {
		ec.touchEndPhone(touch(x, y));
	}

	private void tap(int x, int y) {
		touchStart(x, y);
		touchEnd(x, y);
	}

	private EuclidianView view() {
		return getApp().getActiveEuclidianView();
	}

	private void showAxesWithOriginAt(int x, int y) {
		view().setShowAxes(true, true);
		view().setCoordSystem(x, y, 50, 50);
	}

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
	void polygonToolTapsShouldCreateTriangle() {
		setMode(EuclidianConstants.MODE_POLYGON);
		tap(100, 100);
		tap(200, 100);
		tap(100, 200);
		tap(100, 100);
		checkContent(
				"A = (2, -2)", "B = (4, -2)", "C = (2, -4)", "t1 = 2", "c = 2", "a = 2.82843", "b = 2");
	}

	@Test
	void circleToolTwoTapsShouldCreateCircle() {
		setMode(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		tap(100, 100);
		tap(200, 100);
		checkContent("A = (2, -2)", "B = (4, -2)", "c: (x - 2)² + (y + 2)² = 4");
	}

	@Test
	void moveToolDragShouldMovePoint() {
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(100, 100);
		touchMove(150, 100);
		touchMove(200, 100);
		touchEnd(200, 100);
		checkContent("A = (4, -2)");
	}

	@Test
	void deleteToolTapShouldDeleteObject() {
		add("A = (2, -2)");
		add("B = (4, -2)");
		setMode(EuclidianConstants.MODE_DELETE);
		tap(100, 100);
		checkContent("B = (4, -2)");
	}

	@Test
	void penToolDragShouldCreateStroke() {
		setMode(EuclidianConstants.MODE_PEN);
		touchStart(100, 100);
		touchMove(150, 150);
		touchMove(200, 150);
		touchEnd(200, 150);
		checkContentLabels("stroke1");
	}

	@Test
	void twoFingersOnBackgroundShouldZoomInMoveTool() {
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(100, 300);
		ec.twoTouchStartPhone(100, 300, 300, 300);
		ec.twoTouchMovePhone(50, 300, 350, 300);
		assertEquals(75, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		// the point under the centre of the gesture stays where it is
		assertEquals(4, view().toRealWorldCoordX(200), DELTA);
		assertEquals(-6, view().toRealWorldCoordY(300), DELTA);
	}

	@Test
	void twoFingersOnBackgroundShouldPanAndZoomInMoveTool() {
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(100, 300);
		ec.twoTouchStartPhone(100, 300, 300, 300);
		ec.twoTouchMovePhone(90, 340, 390, 340);
		assertEquals(75, view().getXscale(), DELTA);
		// the point that was under the centre followed the centre of the gesture
		assertEquals(4, view().toRealWorldCoordX(240), DELTA);
		assertEquals(-6, view().toRealWorldCoordY(340), DELTA);
	}

	@Test
	void pinchShouldZoomWhenNavigationIsDisabledAndPreviewFeatureIsOff() {
		getApp().setShiftDragZoomEnabled(false);
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(100, 300);
		ec.twoTouchStartPhone(100, 300, 300, 300);
		ec.twoTouchMovePhone(50, 300, 350, 300);
		assertEquals(75, view().getXscale(), DELTA);
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
		touchStart(100, 300);
		ec.twoTouchStartPhone(100, 300, 300, 300);
		ec.twoTouchMovePhone(50, 300, 350, 300);
		touchEnd(50, 300);
		assertEquals(historySize + 1, undoManager.getHistorySize());
		checkContent("A = (2, -2)");

		undoManager.undo();
		assertEquals(50, view().getXscale(), DELTA);
		checkContent("A = (2, -2)");
		undoManager.undo();
		checkContent();
	}

	@Test
	void twoFingersOnCircleShouldScaleCircleInMoveTool() {
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(200, 100);
		ec.twoTouchStartPhone(200, 100, 200, 300);
		ec.twoTouchMovePhone(200, 50, 200, 350);
		checkContent("A = (4, -4)", "B = (7, -4)", "c: (x - 4)² + (y + 4)² = 9");
		assertEquals(50, view().getXscale(), DELTA);
	}

	@Test
	void twoFingersOnXAxisShouldZoomXAxisOnly() {
		showAxesWithOriginAt(400, 300);
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(500, 300);
		ec.twoTouchStartPhone(500, 300, 600, 300);
		ec.twoTouchMovePhone(500, 300, 700, 300);
		assertEquals(100, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		assertEquals(400, view().getXZero(), DELTA);
		assertEquals(300, view().getYZero(), DELTA);
	}

	@Test
	void twoFingersOnYAxisShouldZoomYAxisOnly() {
		showAxesWithOriginAt(400, 300);
		setMode(EuclidianConstants.MODE_MOVE);
		touchStart(400, 100);
		ec.twoTouchStartPhone(400, 100, 400, 200);
		ec.twoTouchMovePhone(400, 100, 400, 250);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		assertEquals(400, view().getXZero(), DELTA);
		assertEquals(300, view().getYZero(), DELTA);
	}
}
