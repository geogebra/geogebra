package org.geogebra.web.html5.gui.util;

/**
 * The static methods of this class should be used to check for duplicated
 * events (e.g. TouchStart and MouseDown) and to prevent that one event is
 * unintentionally used for multiple purposes (e.g. open and close keyboard)
 */
public class CancelEventTimer {

	private static long lastTouchEvent = 0;

	private static long lastKeyboardEvent = 0;

	/**
	 * amount of time (ms) in which all mouse events are ignored after a touch
	 * event
	 */
	public static final int TIME_BETWEEN_TOUCH_AND_MOUSE = 500;

	/**
	 * amount of time (ms) in which background-clicks are not closing the
	 * keyboard
	 */
	public static final int TIME_BEFORE_HIDING_KEYBOARD = 250;

	/**
	 * called at the end of any touch event
	 */
	public static void touchEventOccured() {
		lastTouchEvent = System.currentTimeMillis();
	}

	/**
	 * called at the beginning of a mouse event
	 * 
	 * @return whether the actual mouse event should be canceled
	 */
	public static boolean cancelMouseEvent() {
		return System.currentTimeMillis() - lastTouchEvent < TIME_BETWEEN_TOUCH_AND_MOUSE;
	}

	/**
	 * called after the keyboard is set to visible
	 */
	public static void keyboardSetVisible() {
		lastKeyboardEvent = System.currentTimeMillis();
	}

	/**
	 * called at the beginning of a background click
	 * 
	 * @return true if the actual event should not hide the keyboard; false
	 *         otherwise
	 */
	public static boolean cancelKeyboardHide() {
		return System.currentTimeMillis() - lastKeyboardEvent < TIME_BEFORE_HIDING_KEYBOARD;
	}
}
