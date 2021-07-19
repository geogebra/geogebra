package org.geogebra.common.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

/**
 * 3D mouse event
 *
 */
public class Mouse3DEvent extends AbstractEvent {

	private GPoint point;

	/**
	 * constructor
	 * 
	 * @param point
	 *            point
	 */
	public Mouse3DEvent(GPoint point) {
		this.point = point;
	}

	@Override
	public GPoint getPoint() {
		return point;
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public boolean isShiftDown() {
		return false;
	}

	@Override
	public void release() {
		// nothing to do
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return point.x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return point.y;
	}

	@Override
	public boolean isRightClick() {
		return false;
	}

	@Override
	public boolean isControlDown() {
		return false;
	}

	@Override
	public int getClickCount() {
		return 0;
	}

	@Override
	public boolean isMetaDown() {
		return false;
	}

	@Override
	public boolean isMiddleClick() {
		return false;
	}

	@Override
	public boolean isPopupTrigger() {
		return false;
	}

	@Override
	public PointerEventType getType() {
		return PointerEventType.MOUSE;
	}

}
