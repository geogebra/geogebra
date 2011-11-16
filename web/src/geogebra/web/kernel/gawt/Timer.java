package geogebra.web.kernel.gawt;

import geogebra.web.kernel.HasTimerAction;

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
		// TODO Auto-generated method stub

	}

	public boolean isRunning() {
		// TODO Auto-generated method stub
		return isrunning;
	}

	public void start() {
		if (!isrunning) {
			scheduleRepeating(timerDelay);
			isrunning = true;
		}
		
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		cancel();
		isrunning = false;
		// TODO Auto-generated method stub
		
	}

	public void setDelay(int delay) {
		scheduleRepeating(delay);
		// TODO Auto-generated method stub
		
	}

}
