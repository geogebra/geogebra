package org.geogebra.keyboard.web;

import org.geogebra.keyboard.base.Action;

import com.google.gwt.user.client.Timer;

public class ButtonRepeater extends Timer {

	private static final int START_ACTION_DELAY = 600;
	private static final int REPEAT_ACTION_DELAY = 80;

	private TabbedKeyboard keyboard;
	private Action action;
	private boolean firstRun;

	public ButtonRepeater(Action action, TabbedKeyboard keyboard) {
		this.action = action;
		this.keyboard = keyboard;
	}

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
