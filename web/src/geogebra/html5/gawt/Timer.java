package geogebra.html5.gawt;

import geogebra.html5.kernel.HasTimerAction;

public class Timer extends com.google.gwt.user.client.Timer {
	private HasTimerAction am;
	private int timerDelay; 
	private boolean isrunning;
	
	
	public Timer(int delay, HasTimerAction animationManager) {
		am = animationManager;
		timerDelay = delay;
		//scheduleRepeating(delay);
		isrunning = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		this.am.actionPerformed();
	}

	public boolean isRunning() {
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
		scheduleRepeating(delay);
	}

}
