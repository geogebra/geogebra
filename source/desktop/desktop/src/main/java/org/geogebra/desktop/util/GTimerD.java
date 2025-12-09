/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
