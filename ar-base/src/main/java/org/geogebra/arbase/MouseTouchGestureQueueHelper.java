package org.geogebra.arbase;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MouseTouchGestureQueueHelper {

    private final BlockingDeque<ARMotionEvent> queuedMotionEvent = new
            LinkedBlockingDeque<>(32);

    private ARMotionEvent lastExecutedMotionEvent;

    public MouseTouchGestureQueueHelper() {
        lastExecutedMotionEvent = null;
    }

    public void add(ARMotionEvent event) {
        ARMotionEvent mEvent = event;
        if (mEvent != null) {
            if (mEvent.getAction() == ARMotionEvent.ON_MOVE) {
                // remove another not necessary ACTION_MOVE in a queue
                try {
                    if (queuedMotionEvent.getLast().getAction() == ARMotionEvent
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

    public ARMotionEvent poll() {
        ARMotionEvent ret = queuedMotionEvent.poll();
        if (ret != null) {
            lastExecutedMotionEvent = ret;
        }
        return ret;
    }

    public boolean isCurrentlyUp() {
        return lastExecutedMotionEvent == null
                || lastExecutedMotionEvent.getAction() == ARMotionEvent.FIRST_FINGER_UP;
    }
}
