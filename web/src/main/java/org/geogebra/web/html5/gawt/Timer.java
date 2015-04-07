package org.geogebra.web.html5.gawt;

import org.geogebra.web.html5.kernel.HasTimerAction;

public class Timer extends com.google.gwt.user.client.Timer {
	private HasTimerAction am;
	private int timerDelay;
	private boolean isrunning;

	public Timer(int delay, HasTimerAction animationManager) {
		am = animationManager;
		timerDelay = delay;
		// scheduleRepeating(delay);
		isrunning = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		this.am.actionPerformed();
	}

	/**
	 * TODO this method somehow doubles isRunning which is implemented in GWT
	 * 2.6 We shall remove it once we switch to GWT >= 2.6.
	 * 
	 * @return whether timer is running or not
	 */
	public boolean isGgbRunning() {
		return isrunning;
	}

	public void start() {
		if (!isrunning) {
			scheduleRepeating(timerDelay);
			isrunning = true;
		}

	}

	public void stop() {
		cancel();
		isrunning = false;
	}

	public void setDelay(int delay) {
		timerDelay = delay;
		if (isGgbRunning()) {
			// note that this will stop the current schedule
			// and start a new one with the new delay
			scheduleRepeating(delay);
		}
	}

}
