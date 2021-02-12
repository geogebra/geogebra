package org.geogebra.xr;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MouseTouchGestureQueueHelper {

	private final BlockingDeque<XRMotionEvent> queuedMotionEvent = new
			LinkedBlockingDeque<>(32);

	private XRMotionEvent lastExecutedMotionEvent;

	public MouseTouchGestureQueueHelper() {
		lastExecutedMotionEvent = null;
	}

	public void add(XRMotionEvent event) {
		XRMotionEvent mEvent = event;
		if (mEvent != null) {
			if (mEvent.getAction() == XRMotionEvent.ON_MOVE) {
				// remove another not necessary ACTION_MOVE in a queue
				try {
					if (queuedMotionEvent.getLast().getAction() == XRMotionEvent
							.ON_MOVE) {
						queuedMotionEvent.removeLast();
					}
				} catch (NoSuchElementException ignored) {
				}
			}
			try {
				queuedMotionEvent.add(mEvent);
			} catch (IllegalStateException e) {
			}
		}
	}

	public XRMotionEvent poll() {
		XRMotionEvent ret = queuedMotionEvent.poll();
		if (ret != null) {
			lastExecutedMotionEvent = ret;
		}
		return ret;
	}

	public boolean isCurrentlyUp() {
		return lastExecutedMotionEvent == null
				|| lastExecutedMotionEvent.getAction() == XRMotionEvent.FIRST_FINGER_UP;
	}
}
