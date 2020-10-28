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
				.onTouchMoveNow(pointerEvent, coordinate.getTime(), false);
	}

	private void endTouch() {
		mouseTouchGestureController.onTouchEnd();
		fpsProfiler.notifyTouchEnd();
	}

	void initiateDrawingWithTouchStart() {
		shouldStartTouch = true;
	}
}
