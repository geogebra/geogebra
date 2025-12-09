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
