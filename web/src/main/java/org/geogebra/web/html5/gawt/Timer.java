package org.geogebra.web.html5.gawt;

import org.geogebra.web.html5.kernel.HasTimerAction;

public class Timer extends com.google.gwt.user.client.Timer {
	private HasTimerAction am;
	private int timerDelay;

	public Timer(int delay, HasTimerAction animationManager) {
		am = animationManager;
		timerDelay = delay;
		// scheduleRepeating(delay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		this.am.actionPerformed();
	}

	

	public void start() {
		if (!isRunning()) {
			scheduleRepeating(timerDelay);
		}

	}

	public void stop() {
		cancel();
	}

	public void setDelay(int delay) {
		timerDelay = delay;
		if (isRunning()) {
			// note that this will stop the current schedule
			// and start a new one with the new delay
			scheduleRepeating(delay);
		}
	}

}
