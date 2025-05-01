package org.geogebra.common.euclidian.event;

import org.geogebra.common.awt.GPoint;

public abstract class AbstractEvent {

	/**
	 * @return pixel coordinates of this event
	 */
	public abstract GPoint getPoint();

	/**
	 * @return whether Alt key is pressed
	 */
	public abstract boolean isAltDown();

	/**
	 * @return whether Shift key is pressed
	 */
	public abstract boolean isShiftDown();

	/**
	 * Return this event to a pool for reuse (if applicable).
	 */
	public abstract void release();

	/**
	 * @return x-coordinate in pixels
	 */
	public abstract int getX();

	/**
	 * @return y-coordinate in pixels
	 */
	public abstract int getY();

	/**
	 * @return whether the right mouse button is pressed
	 */
	public abstract boolean isRightClick();

	/**
	 * @return whether the control key is pressed
	 */
	public abstract boolean isControlDown();

	/**
	 * @return number of clicks
	 */
	public abstract int getClickCount();

	/**
	 * @return whether the meta key is pressed
	 */
	public abstract boolean isMetaDown();

	/**
	 * @return whether the middle mouse button is pressed
	 */
	public abstract boolean isMiddleClick();

	/**
	 * TODO this is based on Swing, meaning in other environments unclear
	 * @return whether this event should trigger popups
	 */
	public abstract boolean isPopupTrigger();

	/**
	 * @return event type
	 */
	public abstract PointerEventType getType();

}
