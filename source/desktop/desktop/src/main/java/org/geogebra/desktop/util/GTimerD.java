package org.geogebra.desktop.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

/**
 * Desktop timer
 */
public class GTimerD implements GTimer, ActionListener {
	private Timer timer;
	private GTimerListener listener;

	/**
	 * @param listener
	 *            action
	 * @param delay
	 *            delay to run (or between runs)
	 */
	public GTimerD(GTimerListener listener, int delay) {
		this.listener = listener;
		timer = new Timer(delay, this);
	}

	@Override
	public void start() {
		timer.start();
	}

	@Override
	public void stop() {
		timer.stop();
	}

	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}

	@Override
	public void setDelay(int delay) {
		timer.setDelay(delay);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		listener.onRun();
	}

	@Override
	public void startRepeat() {
		timer.setRepeats(true);
		start();
	}

}
