package org.geogebra.common.util;

public interface GTimer {
	public interface GTimerListener {
		void onRun();
	}

	void start();

	void startRepeat();

	void stop();

	boolean isRunning();


	void setDelay(int delay);
}
