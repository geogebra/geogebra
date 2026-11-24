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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Issue("APPS-8112")
class TouchGestureArbiterTest extends BaseEuclidianControllerTest {

	private static final boolean FINGER = false;
	private static final boolean STYLUS = true;

	private final List<String> log = new ArrayList<>();
	private boolean acceptLateSecondFinger;
	private AbstractEvent startEvent;
	private TouchGestureArbiter arbiter;

	private final TouchGestureArbiter.Target target = new TouchGestureArbiter.Target() {
		@Override
		public int getStartDelay() {
			return ec.getTouchStartDelay();
		}

		@Override
		public void touchStart(AbstractEvent event) {
			log.add("start " + at(event));
			startEvent = event;
		}

		@Override
		public void touchMove(AbstractEvent event) {
			log.add("move " + at(event));
		}

		@Override
		public void touchEnd(AbstractEvent event) {
			log.add("end " + at(event));
		}

		@Override
		public void navigationStart(double x1, double y1, double x2, double y2) {
			log.add("navigationStart " + at(x1, y1) + " " + at(x2, y2));
		}

		@Override
		public void navigationMove(double x1, double y1, double x2, double y2) {
			log.add("navigationMove " + at(x1, y1) + " " + at(x2, y2));
		}

		@Override
		public void navigationEnd() {
			log.add("navigationEnd");
		}

		@Override
		public boolean lateTwoTouchStart(double x1, double y1, double x2, double y2) {
			log.add("lateStart " + at(x1, y1) + " " + at(x2, y2));
			return acceptLateSecondFinger;
		}

		@Override
		public void lateTwoTouchMove(double x1, double y1, double x2, double y2) {
			log.add("lateMove " + at(x1, y1) + " " + at(x2, y2));
		}

		@Override
		public void lateTwoTouchEnd(AbstractEvent lifted, AbstractEvent remaining) {
			log.add("lateEnd " + at(lifted) + " " + at(remaining));
		}
	};

	@BeforeEach
	void setUp() {
		setUpController();
		arbiter = new TouchGestureArbiter(target);
	}

	private static String at(AbstractEvent event) {
		return at(event.getX(), event.getY());
	}

	private static String at(double x, double y) {
		return "(" + (int) x + "," + (int) y + ")";
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	private void down(long id, int x, int y) {
		arbiter.pointerDown(id, touch(x, y), FINGER);
	}

	private void move(long id, int x, int y) {
		arbiter.pointerMove(id, touch(x, y));
	}

	private void up(long id, int x, int y) {
		arbiter.pointerUp(id, touch(x, y));
	}

	private void startDelayPasses() {
		arbiter.onRun();
	}

	private void assertLog(String... expected) {
		assertEquals(List.of(expected), log);
	}

	@Test
	void fingerShouldBeHeldBackUntilItLifts() {
		down(1, 10, 10);
		assertLog();
		up(1, 10, 10);
		assertLog("start (10,10)", "end (10,10)");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void heldFingerShouldReachToolWithItsMovesWhenDelayPasses() {
		down(1, 10, 10);
		move(1, 20, 10);
		move(1, 30, 10);
		assertLog();
		startDelayPasses();
		assertLog("start (10,10)", "move (20,10)", "move (30,10)");
		assertTrue(arbiter.isToolActive());
		move(1, 40, 10);
		up(1, 40, 10);
		assertLog("start (10,10)", "move (20,10)", "move (30,10)", "move (40,10)", "end (40,10)");
	}

	@Test
	void heldFingerShouldReachToolWithItsMovesWhenItLifts() {
		down(1, 10, 10);
		move(1, 20, 10);
		up(1, 30, 10);
		assertLog("start (10,10)", "move (20,10)", "end (30,10)");
	}

	@Test
	void heldFingerShouldReachToolAsTheEventOfItsLanding() {
		TestEvent landing = touch(10, 10);
		arbiter.pointerDown(1, landing, FINGER);
		move(1, 20, 10);
		startDelayPasses();
		// the same event object, so the tool sees the time when the finger landed
		assertSame(landing, startEvent);
	}

	@Test
	void secondFingerInsideDelayShouldNavigateWithoutToolInput() {
		down(1, 10, 10);
		move(1, 12, 10);
		down(2, 50, 10);
		assertLog("navigationStart (12,10) (50,10)");
		move(1, 5, 10);
		move(2, 60, 10);
		up(1, 5, 10);
		up(2, 60, 10);
		assertLog(
				"navigationStart (12,10) (50,10)",
				"navigationMove (5,10) (50,10)",
				"navigationMove (5,10) (60,10)",
				"navigationEnd");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void delayPassingDuringNavigationShouldNotReachTool() {
		down(1, 10, 10);
		down(2, 50, 10);
		startDelayPasses();
		assertLog("navigationStart (10,10) (50,10)");
		assertFalse(arbiter.isToolActive());
	}

	@Test
	void liftingOneNavigatingFingerShouldNotResumeTool() {
		down(1, 10, 10);
		down(2, 50, 10);
		up(2, 50, 10);
		move(1, 20, 20);
		assertLog("navigationStart (10,10) (50,10)");
		up(1, 20, 20);
		assertLog("navigationStart (10,10) (50,10)", "navigationEnd");
	}

	@Test
	void navigationShouldContinueWithNewSecondFinger() {
		down(1, 10, 10);
		down(2, 50, 10);
		up(1, 10, 10);
		down(3, 90, 10);
		move(3, 95, 10);
		assertLog(
				"navigationStart (10,10) (50,10)",
				"navigationStart (50,10) (90,10)",
				"navigationMove (50,10) (95,10)");
	}

	@Test
	void thirdFingerShouldBeIgnoredUntilItReplacesLiftedFinger() {
		down(1, 10, 10);
		down(2, 50, 10);
		down(3, 90, 90);
		move(3, 95, 95);
		assertLog("navigationStart (10,10) (50,10)");
		up(2, 50, 10);
		assertLog("navigationStart (10,10) (50,10)", "navigationStart (10,10) (95,95)");
		up(1, 10, 10);
		up(3, 95, 95);
		assertLog(
				"navigationStart (10,10) (50,10)", "navigationStart (10,10) (95,95)", "navigationEnd");
	}

	@Test
	void freshContactAfterNavigationShouldBeToolInput() {
		down(1, 10, 10);
		down(2, 50, 10);
		up(1, 10, 10);
		up(2, 50, 10);
		log.clear();
		down(3, 70, 70);
		up(3, 70, 70);
		assertLog("start (70,70)", "end (70,70)");
	}

	@Test
	void lateSecondFingerShouldBeIgnoredWhenTargetDeclines() {
		down(1, 10, 10);
		startDelayPasses();
		down(2, 50, 10);
		move(2, 60, 10);
		up(2, 60, 10);
		move(1, 20, 10);
		up(1, 20, 10);
		assertLog("start (10,10)", "lateStart (10,10) (50,10)", "move (20,10)", "end (20,10)");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void ignoredFingerShouldStayIgnoredAfterToolFingerLifts() {
		down(1, 10, 10);
		startDelayPasses();
		down(2, 50, 10);
		up(1, 10, 10);
		log.clear();
		move(2, 60, 10);
		down(3, 90, 90);
		up(3, 90, 90);
		up(2, 60, 10);
		assertLog();
		assertTrue(arbiter.isIdle());
		down(4, 70, 70);
		up(4, 70, 70);
		assertLog("start (70,70)", "end (70,70)");
	}

	@Test
	void lateSecondFingerShouldActWithFirstWhenTargetAccepts() {
		acceptLateSecondFinger = true;
		down(1, 10, 10);
		startDelayPasses();
		down(2, 50, 10);
		move(2, 60, 10);
		up(2, 60, 10);
		move(1, 20, 10);
		up(1, 20, 10);
		assertLog(
				"start (10,10)",
				"lateStart (10,10) (50,10)",
				"lateMove (10,10) (60,10)",
				"lateEnd (60,10) (10,10)",
				"move (20,10)",
				"end (20,10)");
	}

	@Test
	void firstFingerLiftingFromAcceptedPairShouldLeaveSecondAsToolInput() {
		acceptLateSecondFinger = true;
		down(1, 10, 10);
		startDelayPasses();
		down(2, 50, 10);
		up(1, 10, 10);
		move(2, 60, 10);
		up(2, 60, 10);
		assertLog(
				"start (10,10)",
				"lateStart (10,10) (50,10)",
				"lateEnd (10,10) (50,10)",
				"move (60,10)",
				"end (60,10)");
	}

	@Test
	void stylusShouldNotBeHeldBack() {
		arbiter.pointerDown(1, touch(10, 10), STYLUS);
		assertLog("start (10,10)");
	}

	@Test
	void fingerShouldBeIgnoredWhileStylusIsDown() {
		arbiter.pointerDown(1, touch(10, 10), STYLUS);
		down(2, 50, 10);
		move(2, 60, 10);
		up(2, 60, 10);
		up(1, 10, 10);
		assertLog("start (10,10)", "end (10,10)");
	}

	@Test
	void stylusShouldTakeOverFromHeldFinger() {
		down(1, 10, 10);
		arbiter.pointerDown(2, touch(50, 10), STYLUS);
		move(1, 12, 12);
		move(2, 60, 10);
		up(2, 60, 10);
		up(1, 12, 12);
		assertLog("start (50,10)", "move (60,10)", "end (60,10)");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void stylusShouldNotJoinFingerActingOnTool() {
		acceptLateSecondFinger = true;
		down(1, 10, 10);
		startDelayPasses();
		arbiter.pointerDown(2, touch(50, 10), STYLUS);
		up(2, 50, 10);
		up(1, 10, 10);
		assertLog("start (10,10)", "end (10,10)");
	}

	@Test
	void cancelShouldDropHeldFinger() {
		down(1, 10, 10);
		arbiter.cancel();
		startDelayPasses();
		up(1, 10, 10);
		assertLog();
		assertTrue(arbiter.isIdle());
	}

	@Test
	void cancelShouldEndNavigation() {
		down(1, 10, 10);
		down(2, 50, 10);
		arbiter.cancel();
		assertLog("navigationStart (10,10) (50,10)", "navigationEnd");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void cancelShouldEndToolContactWhereItWasLast() {
		down(1, 10, 10);
		startDelayPasses();
		move(1, 20, 10);
		arbiter.cancel();
		assertLog("start (10,10)", "move (20,10)", "end (20,10)");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void cancelShouldEndLateTwoTouchAndToolContact() {
		acceptLateSecondFinger = true;
		down(1, 10, 10);
		startDelayPasses();
		down(2, 50, 10);
		arbiter.cancel();
		assertLog(
				"start (10,10)", "lateStart (10,10) (50,10)", "lateEnd (50,10) (10,10)", "end (10,10)");
		assertTrue(arbiter.isIdle());
	}

	@Test
	void zeroDelayShouldNotHoldBack() {
		ec.setTouchStartDelay(0);
		down(1, 10, 10);
		assertLog("start (10,10)");
	}
}
