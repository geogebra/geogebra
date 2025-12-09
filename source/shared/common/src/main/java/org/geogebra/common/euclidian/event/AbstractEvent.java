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
