package geogebra.euclidian.event;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;

public class MouseEvent extends AbstractEvent {
	
	public static java.awt.event.MouseEvent impl;
	private static	MouseEvent singl = null;
	
	private MouseEvent() {
		
	}
	
	public static AbstractEvent wrapEvent(java.awt.event.MouseEvent e) {
		MouseEvent.impl = e;
		return getSingleton();
	}
	
	private static MouseEvent getSingleton() {
		if (singl == null) {
			singl = new MouseEvent();
		}
		return singl;
		
	}

	@Override
	public Point getPoint() {
		return new Point(impl.getPoint().x,impl.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		return impl.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		return impl.isShiftDown();
	}

}
