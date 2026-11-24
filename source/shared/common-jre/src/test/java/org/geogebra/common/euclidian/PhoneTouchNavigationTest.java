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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.controller.PhoneTouchGestureTarget;
import org.geogebra.common.euclidian.controller.TouchGestureArbiter;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.EventAccumulator;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Two-finger navigation while a tool is active, through the touch path of the mobile apps.
 */
@Issue("APPS-8112")
class PhoneTouchNavigationTest extends BaseEuclidianControllerTest {

	private static final double DELTA = 1E-6;

	enum AppType {
		CLASSIC,
		GEOMETRY
	}

	private TouchGestureArbiter arbiter;

	@AfterEach
	void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	private void setUp(AppType appType) {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		if (appType == AppType.GEOMETRY) {
			setupApp(SuiteSubApp.GEOMETRY);
			ec = getApp().getActiveEuclidianView().getEuclidianController();
			reset();
		} else {
			setUpController();
		}
		ec.setDraggingDelay(0);
		arbiter = new TouchGestureArbiter(new PhoneTouchGestureTarget(ec));
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	/** A touch event that happened at the given time, in milliseconds. */
	private static TestEvent touchAt(int x, int y, long time) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false) {
			@Override
			public long getTime() {
				return time;
			}
		};
	}

	private void down(long id, int x, int y) {
		arbiter.pointerDown(id, touch(x, y), false);
	}

	private void move(long id, int x, int y) {
		arbiter.pointerMove(id, touch(x, y));
	}

	private void up(long id, int x, int y) {
		arbiter.pointerUp(id, touch(x, y));
	}

	private void tap(int x, int y) {
		down(1, x, y);
		up(1, x, y);
	}

	private void startDelayPasses() {
		arbiter.onRun();
	}

	/** Zoom in by 1.5 around the screen point (400, 300). */
	private void zoomWithTwoFingers() {
		down(1, 300, 300);
		down(2, 500, 300);
		move(1, 250, 300);
		move(2, 550, 300);
		up(1, 250, 300);
		up(2, 550, 300);
	}

	/** Move the view by (100, 50) pixels. */
	private void panWithTwoFingers() {
		down(1, 300, 300);
		down(2, 500, 300);
		move(1, 400, 350);
		move(2, 600, 350);
		up(2, 600, 350);
		up(1, 400, 350);
	}

	private EuclidianView view() {
		return getApp().getActiveEuclidianView();
	}

	private void assertZoomed() {
		assertEquals(75, view().getXscale(), DELTA);
		assertEquals(75, view().getYscale(), DELTA);
		assertEquals(-200, view().getXZero(), DELTA);
		assertEquals(-150, view().getYZero(), DELTA);
	}

	private void assertPanned() {
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(100, view().getXZero(), DELTA);
		assertEquals(50, view().getYZero(), DELTA);
	}

	private void assertViewUnchanged() {
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		assertEquals(0, view().getXZero(), DELTA);
		assertEquals(0, view().getYZero(), DELTA);
	}

	private String definition(String label) {
		return lookup(label).getDefinition(StringTemplate.defaultTemplate);
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void navigationShouldCreateNothingWithPointTool(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POINT);
		zoomWithTwoFingers();
		assertZoomed();
		checkContent();
		assertEquals(EuclidianConstants.MODE_POINT, ec.getMode());
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void nextTapShouldUseNewViewCoordinates(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POINT);
		zoomWithTwoFingers();
		tap(400, 150);
		checkContent("A = (8, -4)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void segmentShouldContinueAfterNavigation(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		zoomWithTwoFingers();
		assertZoomed();
		checkContent("A = (2, -2)");
		assertEquals(EuclidianConstants.MODE_SEGMENT, ec.getMode());

		tap(400, 150);
		checkContent("A = (2, -2)", "B = (8, -4)", "f = 6.32456");
		assertEquals("Segment(A, B)", definition("f"));
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void polygonShouldKeepInputOrderAcrossNavigation(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POLYGON);
		tap(100, 100);
		tap(200, 100);
		panWithTwoFingers();
		assertPanned();
		checkContent("A = (2, -2)", "B = (4, -2)");

		tap(200, 250);
		tap(200, 150);
		assertEquals("Polygon(A, B, C)", definition("t1"));
		assertEquals("(2, -4)", lookup("C").toValueString(StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void polygonPreviewShouldFollowNavigation(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POLYGON);
		tap(100, 100);
		tap(200, 100);
		panWithTwoFingers();

		// A and B moved from (100, 100) and (200, 100) to (200, 150) and (300, 150)
		Drawable preview = (Drawable) view().getPreviewDrawable();
		assertTrue(preview.hit(250, 150, 2));
		assertFalse(preview.hit(150, 100, 2));
	}

	@Test
	void viewShouldChangeCoordSystemBeforeControllerKnowsIt() {
		setUp(AppType.CLASSIC);
		// the iOS view changes its coordinate system before it calls setView
		ec.setView(null);
		view().setCoordSystem(100, 100, 60, 60);
		assertEquals(60, view().getXscale(), DELTA);
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void circleToolShouldContinueAfterNavigation(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		tap(100, 100);
		panWithTwoFingers();
		tap(300, 150);
		checkContent("A = (2, -2)", "B = (4, -2)", "c: (x - 2)² + (y + 2)² = 4");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void repeatedNavigationShouldKeepTool(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		panWithTwoFingers();
		panWithTwoFingers();
		assertEquals(200, view().getXZero(), DELTA);
		assertEquals(100, view().getYZero(), DELTA);
		assertEquals(EuclidianConstants.MODE_SEGMENT, ec.getMode());
		tap(400, 200);
		assertEquals("Segment(A, B)", definition("f"));
		assertEquals("(4, -2)", lookup("B").toValueString(StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingersOnCircleShouldNavigateWithConstructionTool(AppType appType) {
		setUp(appType);
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_POINT);
		down(1, 200, 100);
		down(2, 200, 300);
		move(1, 200, 50);
		move(2, 200, 350);
		up(1, 200, 50);
		up(2, 200, 350);
		checkContent("A = (4, -4)", "B = (6, -4)", "c: (x - 4)² + (y + 4)² = 4");
		assertEquals(75, view().getXscale(), DELTA);
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingersOnCircleShouldScaleCircleWithMoveTool(AppType appType) {
		setUp(appType);
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 200, 100);
		down(2, 200, 300);
		move(1, 200, 50);
		move(2, 200, 350);
		up(1, 200, 50);
		up(2, 200, 350);
		checkContent("A = (4, -4)", "B = (7, -4)", "c: (x - 4)² + (y + 4)² = 9");
		assertViewUnchanged();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void circleScaledWithMoveToolShouldBeOneUndoStep(AppType appType) {
		setUp(appType);
		getApp().setUndoActive(true);
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		getKernel().initUndoInfo();
		setMode(EuclidianConstants.MODE_MOVE);
		UndoManager undoManager = getConstruction().getUndoManager();
		int historySize = undoManager.getHistorySize();

		down(1, 200, 100);
		down(2, 200, 300);
		move(1, 200, 50);
		move(2, 200, 350);
		up(1, 200, 50);
		up(2, 200, 350);
		assertEquals(historySize + 1, undoManager.getHistorySize());

		undoManager.undo();
		checkContent("A = (4, -4)", "B = (6, -4)", "c: (x - 4)² + (y + 4)² = 4");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingersOnXAxisShouldZoomAxisWithConstructionTool(AppType appType) {
		setUp(appType);
		view().setShowAxes(true, true);
		view().setCoordSystem(400, 300, 50, 50);
		setMode(EuclidianConstants.MODE_POINT);
		down(1, 500, 300);
		down(2, 600, 300);
		move(2, 700, 300);
		up(2, 700, 300);
		up(1, 500, 300);
		assertEquals(100, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		checkContent();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingerOnPointShouldNotMoveItWhenSecondFingerFollows(AppType appType) {
		setUp(appType);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 100, 100);
		move(1, 130, 100);
		down(2, 300, 100);
		move(1, 80, 100);
		move(2, 320, 100);
		up(2, 320, 100);
		up(1, 80, 100);
		checkContent("A = (2, -2)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void navigationShouldNotDeleteWithDeleteTool(AppType appType) {
		setUp(appType);
		add("A = (6, -6)");
		add("B = (10, -6)");
		setMode(EuclidianConstants.MODE_DELETE);
		down(1, 300, 300);
		down(2, 500, 300);
		move(1, 250, 300);
		move(2, 550, 300);
		up(2, 550, 300);
		up(1, 250, 300);
		checkContent("A = (6, -6)", "B = (10, -6)");
		assertZoomed();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void navigationShouldKeepEarlierPenStrokes(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_PEN);
		down(1, 100, 100);
		move(1, 120, 120);
		move(1, 140, 120);
		up(1, 140, 120);
		String firstStroke = definition("stroke1");

		zoomWithTwoFingers();
		checkContentLabels("stroke1");
		assertEquals(firstStroke, definition("stroke1"));

		down(1, 100, 400);
		move(1, 120, 420);
		move(1, 140, 420);
		up(1, 140, 420);
		// the new stroke may join the same pen object, the earlier stroke stays as it was
		String earlierPart = firstStroke.substring(0, firstStroke.length() - 1);
		assertTrue(definition("stroke1").startsWith(earlierPart), definition("stroke1"));
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void navigationShouldSendNoObjectEvents(AppType appType) {
		setUp(appType);
		add("A = (6, -6)");
		EventAccumulator events = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(events);
		setMode(EuclidianConstants.MODE_POINT);
		events.reset();

		zoomWithTwoFingers();
		List<String> objectEvents = events.getEvents().stream()
				.filter(event -> !event.startsWith("VIEW_CHANGED"))
				.collect(Collectors.toList());
		assertEquals(List.of(), objectEvents);
		assertTrue(
				events.getEvents().stream().anyMatch(event -> event.startsWith("VIEW_CHANGED")),
				"view change should still be reported");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void navigationShouldBeOneUndoStepAndKeepConstruction(AppType appType) {
		setUp(appType);
		getApp().setUndoActive(true);
		getKernel().initUndoInfo();
		setMode(EuclidianConstants.MODE_POINT);
		tap(100, 100);
		UndoManager undoManager = getConstruction().getUndoManager();
		int historySize = undoManager.getHistorySize();

		zoomWithTwoFingers();
		assertEquals(historySize + 1, undoManager.getHistorySize());
		checkContent("A = (2, -2)");

		undoManager.undo();
		assertViewUnchanged();
		checkContent("A = (2, -2)");
		undoManager.redo();
		assertZoomed();
		checkContent("A = (2, -2)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void pointsShouldSnapToGridAfterNavigation(AppType appType) {
		setUp(appType);
		view().getSettings().setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_ON_GRID);
		setMode(EuclidianConstants.MODE_POINT);
		zoomWithTwoFingers();
		tap(410, 160);
		checkContent("A = (8, -4)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void lateSecondFingerShouldBeIgnoredWithPointTool(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POINT);
		down(1, 100, 100);
		startDelayPasses();
		down(2, 300, 100);
		move(2, 400, 100);
		up(2, 400, 100);
		move(1, 150, 100);
		move(1, 200, 100);
		up(1, 200, 100);
		checkContent("A = (4, -2)");
		assertViewUnchanged();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void lateSecondFingerShouldStillNavigateWithMoveTool(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 300, 300);
		startDelayPasses();
		down(2, 500, 300);
		move(1, 250, 300);
		move(2, 550, 300);
		up(2, 550, 300);
		up(1, 250, 300);
		assertEquals(75, view().getXscale(), DELTA);
		checkContent();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void heldDragShouldKeepItsMovesWithDefaultDragDelay(AppType appType) {
		setUp(appType);
		ec.setDraggingDelay(EuclidianConstants.DRAGGING_DELAY);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		// the finger landed a while ago and moved after the drag delay of 100 ms
		long landed = System.currentTimeMillis() - 1000;
		arbiter.pointerDown(1, touchAt(100, 100, landed), false);
		arbiter.pointerMove(1, touchAt(150, 100, landed + 120));
		arbiter.pointerMove(1, touchAt(200, 100, landed + 140));
		startDelayPasses();
		checkContent("A = (4, -2)");
		arbiter.pointerMove(1, touchAt(250, 100, landed + 160));
		arbiter.pointerUp(1, touchAt(250, 100, landed + 170));
		checkContent("A = (5, -2)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void heldMoveInsideDragDelayShouldBeDroppedAsBefore(AppType appType) {
		setUp(appType);
		ec.setDraggingDelay(EuclidianConstants.DRAGGING_DELAY);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		long landed = System.currentTimeMillis() - 1000;
		arbiter.pointerDown(1, touchAt(100, 100, landed), false);
		arbiter.pointerMove(1, touchAt(150, 100, landed + 50));
		startDelayPasses();
		checkContent("A = (2, -2)");
	}

	@Test
	void doubleTapShouldBeRecognizedFromTheTimeTheSecondFingerLanded() {
		setUp(AppType.CLASSIC);
		getApp().initDialogManager(false);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_POINT);
		// second finger lands 200 ms after the first tap and stays down for 140 ms
		long firstTap = System.currentTimeMillis() - 5000;
		arbiter.pointerDown(1, touchAt(100, 100, firstTap), false);
		arbiter.pointerUp(1, touchAt(100, 100, firstTap + 60));
		arbiter.pointerDown(2, touchAt(100, 100, firstTap + 260), false);
		arbiter.pointerUp(2, touchAt(100, 100, firstTap + 400));
		// a double tap on an object selects the Move tool
		assertEquals(EuclidianConstants.MODE_MOVE, ec.getMode());
	}

	@Test
	void slowSecondTapShouldNotBeDoubleTap() {
		setUp(AppType.CLASSIC);
		getApp().initDialogManager(false);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_POINT);
		long firstTap = System.currentTimeMillis() - 5000;
		arbiter.pointerDown(1, touchAt(100, 100, firstTap), false);
		arbiter.pointerUp(1, touchAt(100, 100, firstTap + 60));
		arbiter.pointerDown(2, touchAt(100, 100, firstTap + 460), false);
		arbiter.pointerUp(2, touchAt(100, 100, firstTap + 520));
		assertEquals(EuclidianConstants.MODE_POINT, ec.getMode());
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void quickTapWithSmallMoveShouldNotDragWithDefaultDragDelay(AppType appType) {
		setUp(appType);
		ec.setDraggingDelay(EuclidianConstants.DRAGGING_DELAY);
		add("A = (2, -2)");
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 100, 100);
		move(1, 130, 100);
		up(1, 130, 100);
		checkContent("A = (2, -2)");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void pinchShouldMoveNothingWhenNavigationIsDisabled(AppType appType) {
		setUp(appType);
		getApp().setShiftDragZoomEnabled(false);
		setMode(EuclidianConstants.MODE_POINT);
		zoomWithTwoFingers();
		panWithTwoFingers();
		assertViewUnchanged();
		checkContent();
		assertEquals(EuclidianConstants.MODE_POINT, ec.getMode());
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void lateSecondFingerShouldMoveNothingWithMoveToolWhenNavigationIsDisabled(AppType appType) {
		setUp(appType);
		getApp().setShiftDragZoomEnabled(false);
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 300, 300);
		startDelayPasses();
		down(2, 500, 300);
		move(1, 250, 300);
		move(2, 550, 300);
		up(2, 550, 300);
		up(1, 250, 300);
		assertViewUnchanged();
		checkContent();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingersOnXAxisShouldMoveNothingWhenNavigationIsDisabled(AppType appType) {
		setUp(appType);
		getApp().setShiftDragZoomEnabled(false);
		view().setShowAxes(true, true);
		view().setCoordSystem(400, 300, 50, 50);
		setMode(EuclidianConstants.MODE_POINT);
		down(1, 500, 300);
		down(2, 600, 300);
		move(2, 700, 300);
		up(2, 700, 300);
		up(1, 500, 300);
		assertEquals(50, view().getXscale(), DELTA);
		assertEquals(50, view().getYscale(), DELTA);
		checkContent();
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void fingersOnCircleShouldStillScaleItWithMoveToolWhenNavigationIsDisabled(AppType appType) {
		setUp(appType);
		getApp().setShiftDragZoomEnabled(false);
		add("A = (4, -4)");
		add("B = (6, -4)");
		add("c = Circle(A, B)");
		setMode(EuclidianConstants.MODE_MOVE);
		down(1, 200, 100);
		down(2, 200, 300);
		move(1, 200, 50);
		move(2, 200, 350);
		up(1, 200, 50);
		up(2, 200, 350);
		checkContent("A = (4, -4)", "B = (7, -4)", "c: (x - 4)² + (y + 4)² = 9");
	}

	@ParameterizedTest
	@EnumSource(AppType.class)
	void canceledNavigationShouldLeaveToolUsable(AppType appType) {
		setUp(appType);
		setMode(EuclidianConstants.MODE_POINT);
		down(1, 300, 300);
		down(2, 500, 300);
		move(2, 550, 300);
		arbiter.cancel();
		checkContent();

		view().setCoordSystem(0, 0, 50, 50);
		tap(100, 100);
		checkContent("A = (2, -2)");
	}
}
