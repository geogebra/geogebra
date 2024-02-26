package org.geogebra.common.gui;

import java.util.ArrayList;

import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

class FlushableTimer implements GTimer {
	private GTimerListener listener;
	private static ArrayList<FlushableTimer> instances = new ArrayList<>();

	public static void flush() {
		for (FlushableTimer timer : instances) {
			timer.listener.onRun();
		}
	}

	public FlushableTimer(GTimerListener listener) {
		this.listener = listener;
		instances.add(this);
	}

	@Override
	public void start() {

	}

	@Override
	public void startRepeat() {

	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public void setDelay(int delay) {

	}
}
