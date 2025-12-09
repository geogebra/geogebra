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
