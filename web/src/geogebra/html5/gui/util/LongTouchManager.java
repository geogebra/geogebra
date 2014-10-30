package geogebra.html5.gui.util;

import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;

public class LongTouchManager {

	private static LongTouchManager instance = new LongTouchManager();

	private LongTouchTimer timer;

	private LongTouchManager() {
	}

	public static LongTouchManager getInstance() {
		return instance;
	}

	public void cancelTimer() {
		if (timer == null) {
			return;
		}
		timer.cancel();
	}
	
	public void scheduleTimer(LongTouchHandler handler, int x, int y) {
		if (timer == null) {
			timer = new LongTouchTimer();
		}
		timer.schedule(handler, x, y);
	}
	
	public void rescheduleTimerIfRunning(LongTouchHandler handler, int x, int y) {
		rescheduleTimerIfRunning(handler, x, y, true);
	}
	
	public void rescheduleTimerIfRunning(LongTouchHandler handler, int x, int y, boolean shouldCancel) {
		if (timer == null) {
			return;
		}
		timer.rescheduleIfRunning(handler, x, y, shouldCancel);
	}
}
