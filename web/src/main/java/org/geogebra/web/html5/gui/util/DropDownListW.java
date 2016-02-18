package org.geogebra.web.html5.gui.util;

import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.user.client.Timer;

public class DropDownListW extends DropDownList {
	private Timer timScroll;
	private Timer timClick;


	public DropDownListW(DropDownListener listener) {
		super(listener);
		timClick = new Timer() {

			@Override
			public void run() {
				doRunClick();
			}
		};

		timScroll = new Timer() {

			@Override
			public void run() {
				doScroll();
			}
		};
	}

	protected void runClickTimer() {
		timClick.schedule(clickDelay);
	}

	protected void runScrollTimer() {
		timClick.cancel();
		timScroll.scheduleRepeating(scrollDelay);
	}

	public void stopClickTimer() {
		timClick.cancel();
		Log.debug("[COMBOSCROLLING] CLICK CANCELED");
	}

	public boolean isClickTimerRunning() {
		return timClick.isRunning();
	}


	public void stopScrollTimer() {
		timScroll.cancel();
	}

	public boolean isScrollTimerRunning() {
		return timScroll.isRunning();
	}

	public void setTimerDelay(int timerDelay) {
		this.scrollDelay = timerDelay;
	}


}
