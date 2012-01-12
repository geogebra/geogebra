package geogebra.web.kernel.gawt;

import geogebra.common.main.AbstractApplication;
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
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	public boolean isRunning() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return isrunning;
	}

	public void start() {
		if (!isrunning) {
			scheduleRepeating(timerDelay);
			isrunning = true;
		}
		
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		
	}

	public void stop() {
		cancel();
		isrunning = false;
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		
	}

	public void setDelay(int delay) {
		scheduleRepeating(delay);
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		
	}

}
