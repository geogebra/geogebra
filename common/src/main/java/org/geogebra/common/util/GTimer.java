package org.geogebra.common.util;

public interface GTimer {

	void start();

	void startRepeat();

	void stop();

	boolean isRunning();

	void setDelay(int delay);
}
