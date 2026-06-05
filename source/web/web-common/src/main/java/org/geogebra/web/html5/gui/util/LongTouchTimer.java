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

package org.geogebra.web.html5.gui.util;

import org.gwtproject.timer.client.Timer;

/**
 * Class used in view controllers to handle long touches.
 */
public class LongTouchTimer extends Timer {

	public static final int SHOW_CONTEXT_MENU_DELAY = 500;

	private static final int MOVE_THRESHOLD = 10;

	private LongTouchHandler touchHandler;
	private double mX;
	private double mY;

	/**
	 * Interface for handling long touches.
	 */
	public interface LongTouchHandler {
		/**
		 * Handles the long touch event.
		 * 
		 * @param x
		 *            the x coordinate of the long touch
		 * @param y
		 *            the y coordinate of the long touch
		 */
		void handleLongTouch(double x, double y);
	}

	public LongTouchTimer() {
		this(null);
	}

	/**
	 * @param handler
	 *            used when the timer elapsed.
	 */
	public LongTouchTimer(LongTouchHandler handler) {
		this.touchHandler = handler;
		this.mX = 0;
		this.mY = 0;
	}

	@Override
	public void run() {
		if (touchHandler == null) {
			return;
		}
		touchHandler.handleLongTouch(mX, mY);
	}

	/**
	 * Schedules the timer with a default delay value.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 */
	public void schedule(LongTouchHandler handler, double x, double y) {
		schedule(handler, x, y, SHOW_CONTEXT_MENU_DELAY);
	}

	/**
	 * Schedules the timer with {@code delayMillis} ms.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 * @param delayMillis
	 *            how long to wait before the timer elapses, in milliseconds
	 */
	public void schedule(LongTouchHandler handler, double x, double y,
			int delayMillis) {
		this.touchHandler = handler;
		this.mX = x;
		this.mY = y;
		schedule(delayMillis);
	}

	/**
	 * Reschedules the timer if it is running, with {@code delayMillis} ms.
	 * 
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 * @param delayMillis
	 *            how long to wait before the timer elapses, in milliseconds
	 */
	public void rescheduleIfRunning(LongTouchHandler handler, double x, double y,
			int delayMillis) {
		if (isRunning()) {
			cancel();
			if (pointWithinLimit(x, y)) {
				schedule(handler, x, y, delayMillis);
			}
		}
	}

	/**
	 * Cancel the timer if dragging happened.
	 *
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void cancelIfDragged(double x, double y) {
		if (!pointWithinLimit(x, y)) {
			cancel();
		}
	}

	/**
	 * Cancel the running timer.
	 */
	public void cancelTimer() {
		mX = 0;
		mY = 0;
		touchHandler = null;
		cancel();
	}

	private boolean pointWithinLimit(double nx, double ny) {
		return Math.abs(nx - mX) < MOVE_THRESHOLD
				&& Math.abs(ny - mY) < MOVE_THRESHOLD;
	}
}
