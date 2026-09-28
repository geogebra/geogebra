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

package org.geogebra.web.test;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.GTimer;

public class MockTimer implements GTimer {
	private boolean running;
	private static final List<MockTimer> instances = new ArrayList<>();

	/**
	 * @return number of currently running timers
	 */
	public static long countRunning() {
		return instances.stream().filter(MockTimer::isRunning).count();
	}

	/**
	 * Remove all instances.
	 */
	public static void clearInstances() {
		instances.clear();
	}

	private static void addInstance(MockTimer timer) {
		instances.add(timer);
	}

	public MockTimer() {
		addInstance(this);
	}

	@Override
	public void start() {
		running = true;
	}

	@Override
	public void startRepeat() {
		running = true;
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void setDelay(int delay) {
		// not needed
	}
}
