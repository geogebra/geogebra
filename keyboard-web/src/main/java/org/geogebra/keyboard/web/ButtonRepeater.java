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
