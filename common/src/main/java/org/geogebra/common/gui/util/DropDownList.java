package org.geogebra.common.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.util.GTimer;

import com.google.j2objc.annotations.Weak;

public class DropDownList {

	protected static final int BOX_ROUND = 8;
	public static final int MAX_WIDTH = 40;

	protected int scrollDelay = 100;
	protected int clickDelay = 500;

	private int mouseX = 0;
	private int mouseY = 0;
	private final GTimer clickTimer;
	private final GTimer scrollTimer;

	@Weak
	private final DropDownListener listener;

	/**
	 * @param app
	 *            application
	 * @param listener
	 *            selection listener
	 */
	public DropDownList(App app, DropDownListener listener) {
		this.listener = listener;
		clickTimer = app.newTimer(this::doRunClick, clickDelay);
		scrollTimer = app.newTimer(this::doScroll, scrollDelay);
	}

	/**
	 * Run click listener.
	 */
	public void doRunClick() {
		listener.onClick(mouseX, mouseY);
	}

	/**
	 * Run scroll listener.
	 */
	public void doScroll() {
		listener.onScroll(mouseX, mouseY);
	}

	private void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	/**
	 * Start click timer.
	 * 
	 * @param x
	 *            pointer x
	 * @param y
	 *            pointer y
	 */
	public void startClickTimer(int x, int y) {
		setMouse(x, y);
		// might be null eg Android, iOS
		if (clickTimer != null) {
			clickTimer.start();
		}
	}

	/**
	 * Start scroll timer.
	 * 
	 * @param x
	 *            pointer x
	 * @param y
	 *            pointer y
	 */
	public void startScrollTimer(int x, int y) {
		setMouse(x, y);
		if (scrollTimer != null) {
			scrollTimer.startRepeat();
		}
	}

	/**
	 * Stop click timer.
	 */
	public void stopClickTimer() {
		// might be null eg Android, iOS
		if (clickTimer != null) {
			clickTimer.stop();
		}
	}

	/**
	 * Stop scroll timer.
	 */
	public void stopScrollTimer() {
		// might be null eg Android, iOS
		if (scrollTimer != null) {
			scrollTimer.stop();
		}
	}

	/**
	 * @return whether click timer is running
	 */
	public boolean isClickTimerRunning() {
		// might be null eg Android, iOS
		return clickTimer != null && clickTimer.isRunning();
	}

	/**
	 * @return whether scroll timer is running
	 */
	public boolean isScrollTimerRunning() {
		// might be null eg Android, iOS
		return scrollTimer != null && scrollTimer.isRunning();
	}
}