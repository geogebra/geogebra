package geogebra.euclidian.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.main.AbstractApplication;

import java.awt.event.InputEvent;
import java.util.LinkedList;

public class MouseEvent extends AbstractEvent {
	
	public static LinkedList<MouseEvent> pool = new LinkedList<MouseEvent>();
	private java.awt.event.MouseEvent event;
	private int id;
	
	private MouseEvent(java.awt.event.MouseEvent e) {
		AbstractApplication.debug("possible missing release()");
		this.event = e;
	}
	
	public static AbstractEvent wrapEvent(java.awt.event.MouseEvent e) {
		if(!pool.isEmpty()){
			MouseEvent wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new MouseEvent(e);
	}

	@Override
	public GPoint getPoint() {
		
		return new GPoint(event.getPoint().x,event.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		return event.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		return event.isShiftDown();
	}

	public static java.awt.event.MouseEvent getEvent(AbstractEvent e) {
		return ((MouseEvent)e).event;
	}

	@Override
	public void release() {
		MouseEvent.pool.add(this);
	}

	@Override
	public int getID() {
		return this.id;
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
		return (event.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
	}

	@Override
	public boolean isControlDown() {
		return event.isControlDown();
	}

	@Override
	public int getClickCount() {
		return event.getClickCount();
	}

	@Override
	public boolean isMetaDown() {
		return event.isMetaDown();
	}

	@Override
	public double getWheelRotation() {
		return ((java.awt.event.MouseWheelEvent)event).getWheelRotation();
	}

	@Override
	public boolean isMiddleClick() {
		return (event.getButton() == 2) && (event.getClickCount() == 1);
	}

	@Override
	public boolean isPopupTrigger() {
		return event.isPopupTrigger();
	}

}
