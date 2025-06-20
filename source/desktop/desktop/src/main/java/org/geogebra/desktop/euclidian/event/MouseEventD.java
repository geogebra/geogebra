package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;
import java.util.LinkedList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.debug.Log;

public class MouseEventD extends AbstractEvent implements MouseEventND {

	private static final LinkedList<MouseEventD> pool = new LinkedList<>();
	private MouseEvent event;

	private MouseEventD(MouseEvent e) {
		Log.debug("possible missing release()");
		this.event = e;
	}

	/**
	 * @param e native event
	 * @return cross-platform (wrapped) evnt
	 */
	public static AbstractEvent wrapEvent(MouseEvent e) {
		if (!pool.isEmpty()) {
			MouseEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new MouseEventD(e);
	}

	@Override
	public GPoint getPoint() {

		return new GPoint(event.getPoint().x, event.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		return event.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		return event.isShiftDown();
	}

	@Override
	public void release() {
		MouseEventD.pool.add(this);
	}

	@Override
	public int getX() {
		return event.getX();
	}

	@Override
	public int getY() {
		return event.getY();
	}

	@Override
	public boolean isRightClick() {
		return MouseEventUtil.isRightClick(event);
	}

	@Override
	public boolean isControlDown() {
		return MouseEventUtil.isControlDown(event);
	}

	@Override
	public int getClickCount() {
		return event.getClickCount();
	}

	@Override
	public boolean isMetaDown() {
		return MouseEventUtil.isMetaDown(event);
	}

	@Override
	public boolean isMiddleClick() {
		return (event.getButton() == 2) && (event.getClickCount() == 1);
	}

	@Override
	public boolean isPopupTrigger() {
		return event.isPopupTrigger();
	}

	@Override
	public java.awt.Component getComponent() {
		return event.getComponent();
	}

	@Override
	public PointerEventType getType() {
		return PointerEventType.MOUSE;
	}

}
