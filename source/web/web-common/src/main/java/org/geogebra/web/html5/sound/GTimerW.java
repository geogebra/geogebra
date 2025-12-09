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

package org.geogebra.web.html5.sound;

import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.gwtproject.timer.client.Timer;

/**
 * Timer for Web
 */
public class GTimerW implements GTimer {
	private Timer timer;
	private int delay;

	/**
	 * @param listener
	 *            listener
	 * @param delay
	 *            delay or interval in ms
	 */
	public GTimerW(final GTimerListener listener, int delay) {
		timer = new Timer() {

			@Override
			public void run() {
				listener.onRun();
			}
		};
		setDelay(delay);
	}

	@Override
	public void start() {
		timer.schedule(delay);
	}

	@Override
	public void startRepeat() {
		if (!isRunning()) {
			timer.scheduleRepeating(delay);
		}
	}

	@Override
	public void stop() {
		timer.cancel();
	}

	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}

	@Override
	public void setDelay(int delay) {
		this.delay = delay;
		if (isRunning()) {
			// note that this will stop the current schedule
			// and start a new one with the new delay
			timer.scheduleRepeating(delay);
		}
	}

}
