package org.geogebra.web.web.util;

import org.geogebra.common.util.GTimer;

import com.google.gwt.user.client.Timer;

public class GTimerW implements GTimer {
	private Timer timer;
	private int delay;
	private GTimerListener listener;

	public GTimerW(GTimerListener listener, int delay) {
		this.listener = listener;
		timer = new Timer() {

			@Override
			public void run() {
				GTimerW.this.listener.onRun();
			}

		};
		setDelay(delay);
	}
	public void start() {
		timer.schedule(delay);
	}

	public void startRepeat() {
		timer.scheduleRepeating(delay);
	}

	public void stop() {
		timer.cancel();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
