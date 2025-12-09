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

package org.geogebra.common.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.util.GTimer;

import com.google.j2objc.annotations.Weak;

public class DropDownList {

	public static final int BOX_ROUND = 2;
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