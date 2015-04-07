package org.geogebra.common.euclidian.event;

import org.geogebra.common.awt.GPoint;

public abstract class AbstractEvent {

	public abstract GPoint getPoint();

	public abstract boolean isAltDown();

	public abstract boolean isShiftDown();

	public abstract void release();

	public abstract int getX();

	public abstract int getY();

	public abstract boolean isRightClick();

	public abstract boolean isControlDown();

	public abstract int getClickCount();

	public abstract boolean isMetaDown();

	public abstract boolean isMiddleClick();

	public abstract boolean isPopupTrigger();

	public abstract PointerEventType getType();

}
