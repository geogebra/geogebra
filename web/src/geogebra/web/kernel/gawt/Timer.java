package geogebra.web.kernel.gawt;

import geogebra.common.main.App;
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
		App.debug("implementation needed"); // TODO Auto-generated

	}

	public boolean isRunning() {
		App.debug("implementation needed"); // TODO Auto-generated
		return isrunning;
	}

	public void start() {
		if (!isrunning) {
			scheduleRepeating(timerDelay);
			isrunning = true;
		}
		
		App.debug("implementation needed"); // TODO Auto-generated
		
	}

	public void stop() {
		cancel();
		isrunning = false;
		App.debug("implementation needed"); // TODO Auto-generated
		
	}

	public void setDelay(int delay) {
		scheduleRepeating(delay);
		App.debug("implementation needed"); // TODO Auto-generated
		
	}

}
