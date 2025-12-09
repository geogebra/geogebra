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

package org.geogebra.keyboard.web;

import org.geogebra.keyboard.base.Action;
import org.gwtproject.timer.client.Timer;

/**
 * Timer for repeating keyboard actions
 */
public class ButtonRepeater extends Timer {

	private static final int START_ACTION_DELAY = 600;
	private static final int REPEAT_ACTION_DELAY = 80;

	private TabbedKeyboard keyboard;
	private Action action;
	private boolean firstRun;

	/**
	 * @param action
	 *            key action
	 * @param keyboard
	 *            keyboard
	 */
	public ButtonRepeater(Action action, TabbedKeyboard keyboard) {
		this.action = action;
		this.keyboard = keyboard;
	}

	/**
	 * Execute immediately and initiate timer
	 */
	public void start() {
		execute();
		this.firstRun = true;
		schedule(START_ACTION_DELAY);
	}

	@Override
	public void run() {
		execute();
		if (firstRun) {
			firstRun = false;
			scheduleRepeating(REPEAT_ACTION_DELAY);
		}
	}

	private void execute() {
		keyboard.executeOnce(action);
	}

}
