package geogebra.common.euclidian.event;

import geogebra.common.awt.Point;

public abstract class AbstractEvent {

	public abstract Point getPoint();

	public abstract boolean isAltDown();

	public abstract boolean isShiftDown();

	public abstract void release(int l);
	
	public abstract int getID();

	public abstract int getX();
	
	public abstract int getY();

	public abstract boolean isRightClick();

	public abstract boolean isControlDown();

	public abstract int getClickCount();

}
