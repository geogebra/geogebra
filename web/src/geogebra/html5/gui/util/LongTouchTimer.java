package geogebra.html5.gui.util;

import com.google.gwt.user.client.Timer;

/**
 * Class used in view controllers to handle long touches.
 */
public class LongTouchTimer extends Timer {
	
	private static final int SHOW_CONTEXT_MENU_DELAY = 1000;
	
	/**
	 * Interface for handling long touches.
	 */
	public interface LongTouchHandler {
		/**
		 * Handles the long touch event.
		 * @param x the x coordinate of the long touch
		 * @param y the y coordinate of the long touch
		 */
		public void handleLongTouch(int x, int y);
	}
	
	private LongTouchHandler handler;
	private int xCoordinate;
	private int yCoordinate;
	
	/**
	 * @param handler used when the timer elapsed.
	 */
	public LongTouchTimer(LongTouchHandler handler) {
	    this.handler = handler;
	    this.xCoordinate = 0;
	    this.yCoordinate = 0;
    }

	@Override
    public void run() {
	    if (handler == null) {
	    	return;
	    }
	    handler.handleLongTouch(xCoordinate, yCoordinate);
    }
	
	/**
	 * Schedules the timer with a default delay value.
	 * @param x the x coordinate passed to the handler
	 * @param y the y coordinate passed to the handler
	 */
	public void schedule(int x, int y) {
		schedule(x, y, SHOW_CONTEXT_MENU_DELAY);
	}
	
	/**
	 * Schedules the timer with {@code delayMillis} ms.
	 * @param x the x coordinate passed to the handler
	 * @param y the y coordinate passed to the handler
	 * @param delayMillis how long to wait before the timer elapses, in milliseconds
	 */
	public void schedule(int x, int y, int delayMillis) {
		xCoordinate = x;
		yCoordinate = y;
		schedule(delayMillis);
	}
	
	/**
	 * Reschedules the timer if it is running, with a default delay value.
	 * @param x the x coordinate passed to the handler
	 * @param y the y coordinate passed to the handler
	 */
	public void reScheduleIfRunning(int x, int y) {
		reScheduleIfRunning(x, y, SHOW_CONTEXT_MENU_DELAY);
	}
	
	/**
	 * Reschedules the timer if it is running, with {@code delayMillis} ms.
	 * @param x the x coordinate passed to the handler
	 * @param y the y coordinate passed to the handler
	 * @param delayMillis how long to wait before the timer elapses, in milliseconds
	 */
	public void reScheduleIfRunning(int x, int y, int delayMillis) {
		if (isRunning()) {
			cancel();
			schedule(x, y, delayMillis);
		}
	}
	
	@Override
	public void cancel() {
		xCoordinate = 0;
		yCoordinate = 0;
	    super.cancel();
	}

}
