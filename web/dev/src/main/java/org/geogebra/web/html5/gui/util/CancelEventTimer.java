package org.geogebra.web.html5.gui.util;

import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * The static methods of this class should be used to check for duplicated
 * events (e.g. TouchStart and MouseDown) and to prevent that one event is
 * unintentionally used for multiple purposes (e.g. open and close keyboard)
 */
public class CancelEventTimer {
	private enum DragState {
		CANSTART, DRAG, NONE
	}

	private static DragState dragState = DragState.NONE;

	private static long lastTouchEvent = 0;

	private static long lastDragEvent = 0;

	private static long lastKeyboardEvent = 0;

	private static long lastBlurEvent = 0;

	private static boolean blurEnabled;

	private static long avRestoreWidthEvent = 0;

	/**
	 * amount of time (ms) in which all mouse events are ignored after a touch
	 * event
	 */
	public static final int TIME_BETWEEN_TOUCH_AND_MOUSE = 500;

	/**
	 * amount of time (ms) in which all mouse events are ignored after a touch
	 * event
	 */
	public static final int TIME_BETWEEN_TOUCH_AND_DRAG = 1000;

	/**
	 * amount of time (ms) in which background-clicks are not closing the
	 * keyboard
	 */
	public static final int TIME_BEFORE_HIDING_KEYBOARD = 250;

	/**
	 * amount of time (ms) in which all blur events are ignored after a click
	 * event
	 */
	public static final int TIME_BETWEEN_BLUR_AND_CLICK = 500;

	/**
	 * amount of time (ms) in which AV width restoring is canceled.
	 * 
	 */
	private static final long TIME_BEFORE_RESTORING_AV_WIDTH = 500;

	/**
	 * called at the end of any touch event
	 */
	public static void touchEventOccured() {
		lastTouchEvent = System.currentTimeMillis();
	}

	/**
	 * called when it may be a drag start.
	 */
	public static void dragCanStart() {
		lastDragEvent = System.currentTimeMillis();
		dragState = DragState.CANSTART;

	}

	/**
	 * @return if dragging just started.
	 */
	public static boolean isDragStarted() {
		boolean result = dragState == DragState.CANSTART;
		dragState = DragState.DRAG;
		return result;
	}

	/**
	 * 
	 * @return if drag is happening now.
	 */
	public static boolean isDragging() {
		return dragState == DragState.DRAG;
	}

	/**
	 * Cancels drag
	 */
	public static void resetDrag() {
		dragState = DragState.NONE;
	}

	/**
	 * @return true if no drag has happened.
	 */
	public static boolean noDrag() {
		return dragState == DragState.NONE;
	}

	/**
	 * called at the end of any blur event
	 */
	public static void blurEventOccured() {
		lastBlurEvent = System.currentTimeMillis();
		blurEnabled = true;
	}

	/**
	 * disable any blur event
	 */
	public static void disableBlurEvent() {
		blurEnabled = false;
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
	 * called at the beginning of a blur event
	 * 
	 * @return whether the actual blur event should be canceled
	 */
	public static boolean cancelBlurEvent() {
		return !blurEnabled || System.currentTimeMillis()
				- lastBlurEvent < TIME_BETWEEN_BLUR_AND_CLICK;
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

	/**
	 * Called to check if the drag should be canceled to enable long tap scroll to
	 * happen.
	 * 
	 * @return true if drag should be canceled.
	 */
	public static boolean cancelDragEvent() {
		return System.currentTimeMillis() - lastDragEvent < TIME_BETWEEN_TOUCH_AND_DRAG;
	}

	/**
	 * called after Algebra View should restore its original width (after editing an
	 * item)
	 */
	public static void avRestoreWidth() {
		avRestoreWidthEvent = System.currentTimeMillis();
	}

	/**
	 * @return whether AV restore happened recently
	 */
	public static boolean cancelAVRestoreWidth() {
		return System.currentTimeMillis()
				- avRestoreWidthEvent < TIME_BEFORE_RESTORING_AV_WIDTH;
	}

	/**
	 * Prevent default for touch up and down in given widget
	 * 
	 * @param panel
	 *            widget
	 */
	public static void killTouch(Widget panel) {
		panel.addDomHandler(new TouchStartHandler() {

			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();

			}
		}, TouchStartEvent.getType());
		panel.addDomHandler(new TouchMoveHandler() {

			public void onTouchMove(TouchMoveEvent event) {
				event.preventDefault();

			}
		}, TouchMoveEvent.getType());

	}

}
