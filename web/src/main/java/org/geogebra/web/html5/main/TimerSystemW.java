package org.geogebra.web.html5.main;

import com.google.gwt.user.client.Timer;

public class TimerSystemW {

	/**
	 * delay between two timer performs
	 */
	final public static int MAIN_LOOP_DELAY = 16;

	/**
	 * loops to wait before performing a repaint
	 */
	final public static int EUCLIDIAN_LOOPS = 1;

	final public static int ALGEBRA_LOOPS = 20;

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

	public TimerSystemW(AppW app) {
		this.app = app;
		this.idle = 0;

		repaintTimer = new Timer() {
			@Override
			public void run() {
				browserSkipped = 0;
				if (!suggestRepaint()) {
					idle++;
				}
				if (idle > 30) {
					idle = 0;
					this.cancel();
				}
			}
		};

		repaintTimer.scheduleRepeating(MAIN_LOOP_DELAY);
	}

	/**
	 * suggests views to repaint
	 */
	boolean suggestRepaint() {
		if (app.getKernel() == null) {
			return false;
		}
		return app.getKernel().notifySuggestRepaint();
	}

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

	// static public long loopsNeeded(long delay){
	// return delay/MAIN_LOOP_DELAY;
	// }

}
