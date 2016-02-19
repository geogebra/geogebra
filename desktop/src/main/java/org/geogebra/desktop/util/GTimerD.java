package org.geogebra.desktop.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.util.GTimer;

public class GTimerD implements GTimer, ActionListener {
	private Timer timer;
	private GTimerListener listener;

	public GTimerD(GTimerListener listener, int delay) {
		this.listener = listener;
		timer = new Timer(delay, this);
	}
	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public void setDelay(int delay) {
		timer.setDelay(delay);
	}

	public void actionPerformed(ActionEvent e) {
		listener.onRun();
	}

	public void startRepeat() {
		timer.setRepeats(true);
		start();
	}

}
