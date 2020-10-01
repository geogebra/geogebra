package org.geogebra.web.html5.main;

import org.gwtproject.timer.client.Timer;

/**
 * Timer system for view repaints
 */
public class TimerSystemW {

	/**
	 * delay between two timer performs
	 */
	final public static int MAIN_LOOP_DELAY = 16;

	/**
	 * loops to wait before performing a repaint
	 */
	final public static int EUCLIDIAN_LOOPS = 0; // no wait, repaint every loop

	final public static int ALGEBRA_LOOPS = 5;

	final public static int SPREADSHEET_LOOPS = ALGEBRA_LOOPS;

	final public static int REPAINT_FLAG = 0;

	final public static int SLEEPING_FLAG = -1;

	/*
	 * public static int euclidianMillis = 34; // = 30 FPS, half of screen Hz
	 * public static int algebraMillis = 334; // = 3 FPS public static int
	 * spreadsheetMillis = 334; // = 3 FPS
	 */

	final AppW app;

	private Timer repaintTimer;

	private int idle;
	private long browserSkipped = 0;

	/**
	 * Create new timer system
	 * 
	 * @param app
	 *            application
	 */
	public TimerSystemW(AppW app) {
		this.app = app;
		this.idle = 0;

		repaintTimer = new Timer() {
			@Override
			public void run() {
				tick();
			}
		};

		repaintTimer.scheduleRepeating(MAIN_LOOP_DELAY);
	}

	/**
	 * Execute one timer tick.
	 */
	protected void tick() {
		browserSkipped = 0;
		if (!suggestRepaint()) {
			idle++;
		}
		if (idle > 30) {
			idle = 0;
			repaintTimer.cancel();
		}
	}

	/**
	 * suggests views to repaint
	 * 
	 * @return whether at least one view needed repaint
	 */
	boolean suggestRepaint() {
		if (app.getKernel() == null) {
			return false;
		}
		return app.getKernel().notifySuggestRepaint();
	}

	/**
	 * Make sure the clock is ticking
	 */
	public void ensureRunning() {
		if (!this.repaintTimer.isRunning()) {
			repaintTimer.scheduleRepeating(MAIN_LOOP_DELAY);
		} else {
			long time = System.currentTimeMillis();
			if (browserSkipped > 0 && time - browserSkipped > 50) {
				repaintTimer.run();
			} else if (browserSkipped == 0) {
				browserSkipped = time;
			}

		}
	}

}
