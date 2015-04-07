package org.geogebra.web.html5.gui.util;

import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;

/**
 * Singleton class for firing the long touch event. To use this class, implement
 * {@link LongTouchHandler}, get an instance with {@link #getInstance()} and
 * schedule the timer.
 */
public class LongTouchManager {

	private static LongTouchManager instance = new LongTouchManager();

	private LongTouchTimer timer;

	private LongTouchManager() {
	}

	/**
	 * @return a singleton instance of this class.
	 */
	public static LongTouchManager getInstance() {
		return instance;
	}

	/**
	 * Cancels the timer if it is running.
	 */
	public void cancelTimer() {
		if (timer == null) {
			return;
		}
		timer.cancelTimer();
	}

	/**
	 * Schedules the timer with a default delay value.
	 * 
	 * @param handler
	 *            long touch event handler
	 * @param x
	 *            the x-coordinate of the touch
	 * @param y
	 *            the y-coordinate of the touch
	 */
	public void scheduleTimer(LongTouchHandler handler, int x, int y) {
		if (timer == null) {
			timer = new LongTouchTimer();
		}
		timer.schedule(handler, x, y);
	}

	/**
	 * Schedules the timer with {@code delayMillis} delay value.
	 * 
	 * @param handler
	 *            long touch event handler
	 * @param x
	 *            the x-coordinate of the touch
	 * @param y
	 *            the y-coordinate of the touch
	 * @param delayMillis
	 *            delay value
	 */
	public void scheduleTimer(LongTouchHandler handler, int x, int y,
	        int delayMillis) {
		if (timer == null) {
			timer = new LongTouchTimer();
		}
		timer.schedule(handler, x, y, delayMillis);
	}

	/**
	 * Reschedules the timer if it is running, with a default delay value.
	 * 
	 * @param handler
	 *            long touch event handler
	 * @param x
	 *            the x-coordinate of the touch
	 * @param y
	 *            the y-coordinate of the touch
	 */
	public void rescheduleTimerIfRunning(LongTouchHandler handler, int x, int y) {
		rescheduleTimerIfRunning(handler, x, y, true);
	}

	/**
	 * Reschedules the timer if it is running, with a default delay value.
	 * 
	 * @param handler
	 *            long touch event handler
	 * @param x
	 *            the x-coordinate of the touch
	 * @param y
	 *            the y-coordinate of the touch
	 * @param shouldCancel
	 *            if true, the timer will be cancelled if the mouse moved too
	 *            much
	 */
	public void rescheduleTimerIfRunning(LongTouchHandler handler, int x,
	        int y, boolean shouldCancel) {
		if (timer == null) {
			return;
		}
		timer.rescheduleIfRunning(handler, x, y, shouldCancel);
	}

	/**
	 * Reschedules the timer if it is running, with {@code delayMillis} delay
	 * value.
	 * 
	 * @param handler
	 *            long touch event handler
	 * @param x
	 *            the x-coordinate of the touch
	 * @param y
	 *            the y-coordinate of the touch
	 * @param delayMillis
	 *            delay value
	 * @param shouldCancel
	 *            if true, the timer will be cancelled if the mouse moved too
	 *            much
	 */
	public void rescheduleTimerIfRunning(LongTouchHandler handler, int x,
	        int y, int delayMillis, boolean shouldCancel) {
		if (timer == null) {
			return;
		}
		timer.rescheduleIfRunning(handler, x, y, delayMillis, shouldCancel);
	}
}
