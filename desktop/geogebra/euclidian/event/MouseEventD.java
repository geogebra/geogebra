package geogebra.euclidian.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.main.App;

import java.awt.event.InputEvent;
import java.util.LinkedList;

public class MouseEventD extends AbstractEvent {
	
	public static LinkedList<MouseEventD> pool = new LinkedList<MouseEventD>();
	private java.awt.event.MouseEvent event;
	private int id;
	
	private MouseEventD(java.awt.event.MouseEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}
	
	public static AbstractEvent wrapEvent(java.awt.event.MouseEvent e) {
		if(!pool.isEmpty()){
			MouseEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new MouseEventD(e);
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
		return ((MouseEventD)e).event;
	}

	@Override
	public void release() {
		MouseEventD.pool.add(this);
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
