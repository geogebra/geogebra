package org.geogebra.web.html5.sound;

import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.gwtproject.timer.client.Timer;

/**
 * Timer for Web
 */
public class GTimerW implements GTimer {
	private Timer timer;
	private int delay;

	/**
	 * @param listener
	 *            listener
	 * @param delay
	 *            delay or interval in ms
	 */
	public GTimerW(final GTimerListener listener, int delay) {
		timer = new Timer() {

			@Override
			public void run() {
				listener.onRun();
			}
		};
		setDelay(delay);
	}

	@Override
	public void start() {
		timer.schedule(delay);
	}

	@Override
	public void startRepeat() {
		if (!isRunning()) {
			timer.scheduleRepeating(delay);
		}
	}

	@Override
	public void stop() {
		timer.cancel();
	}

	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}

	@Override
	public void setDelay(int delay) {
		this.delay = delay;
		if (isRunning()) {
			// note that this will stop the current schedule
			// and start a new one with the new delay
			timer.scheduleRepeating(delay);
		}
	}

}
