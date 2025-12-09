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

package org.geogebra.web.html5.euclidian.profiler.drawer;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.profiler.FpsProfiler;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.event.PointerEvent;
import org.gwtproject.timer.client.Timer;

/**
 * Schedules the drawing of a line.
 */
class Drawer extends Timer {

	private MouseTouchGestureControllerW mouseTouchGestureController;
	private Coordinate coordinate;
	private PointerEvent pointerEvent;
	private FpsProfiler fpsProfiler;
	private boolean shouldStartTouch;

	Drawer(MouseTouchGestureControllerW mouseTouchGestureController, Coordinate coordinate) {
		this.mouseTouchGestureController = mouseTouchGestureController;
		this.coordinate = coordinate;
		fpsProfiler = mouseTouchGestureController.getApp().getFpsProfiler();
	}

	@Override
	public void run() {
		initPointerEvent();
		if (shouldStartTouch) {
			startTouch();
		}
		moveTouch();
		if (coordinate.isTouchEnd()) {
			endTouch();
		}
	}

	private void initPointerEvent() {
		pointerEvent =
				new PointerEvent(
						coordinate.getX(), coordinate.getY(),
						PointerEventType.TOUCH,
						mouseTouchGestureController);
	}

	private void startTouch() {
		fpsProfiler.notifyTouchStart();
		mouseTouchGestureController.onPointerEventStart(pointerEvent);
	}

	private void moveTouch() {
		mouseTouchGestureController
				.onMouseMoveNow(pointerEvent, coordinate.getTime(), false);
	}

	private void endTouch() {
		mouseTouchGestureController.onPointerEventEnd(pointerEvent);
		fpsProfiler.notifyTouchEnd();
	}

	void initiateDrawingWithTouchStart() {
		shouldStartTouch = true;
	}
}
