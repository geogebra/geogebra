package geogebra.euclidian.event;



import java.util.HashMap;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.main.Application;

public class MouseEvent extends AbstractEvent {
	
	public static HashMap<Integer,MouseEvent> pool = new HashMap<Integer, MouseEvent>();
	private java.awt.event.MouseEvent event;
	private int id;
	
	private MouseEvent(java.awt.event.MouseEvent e) {
		this.event = e;
		this.id = e.getID();
		MouseEvent.pool.put(this.id, this);
	}
	
	public static AbstractEvent wrapEvent(java.awt.event.MouseEvent e) {
		return new MouseEvent(e);
	}

	@Override
	public Point getPoint() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return new Point(current.event.getPoint().x,current.event.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.isShiftDown();
	}

	public static java.awt.event.MouseEvent getEvent(AbstractEvent e) {
		return MouseEvent.pool.get(e.getID()).event;
	}

	@Override
	public void release(int l) {
		MouseEvent.pool.remove(l);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getX();
	}

	@Override
	public int getY() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getY();
	}

	@Override
	public boolean isRightClick() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return (current.event.getModifiers() & java.awt.event.MouseEvent.BUTTON3_MASK) == java.awt.event.MouseEvent.BUTTON3_MASK;
	}

	@Override
	public boolean isControlDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.isControlDown();
	}

	@Override
	public int getClickCount() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getClickCount();
	}

	@Override
	public boolean isMetaDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.isMetaDown();
	}

	@Override
	public double getWheelRotation() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return ((java.awt.event.MouseWheelEvent)current.event).getWheelRotation();
	}

	@Override
	public boolean isMiddleClick() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return (current.event.getButton() == 2) && (current.event.getClickCount() == 1);
	}

}
