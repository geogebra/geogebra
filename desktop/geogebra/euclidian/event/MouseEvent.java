package geogebra.euclidian.event;



import java.util.HashMap;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.main.Application;

public class MouseEvent extends AbstractEvent {
	
	public static HashMap<Integer,MouseEvent> impl = new HashMap<Integer, MouseEvent>();
	private java.awt.event.MouseEvent event;
	private int id;
	
	private MouseEvent(java.awt.event.MouseEvent e) {
		this.event = e;
		this.id = e.getID();
		MouseEvent.impl.put(this.id, this);
	}
	
	public static AbstractEvent wrapEvent(java.awt.event.MouseEvent e) {
		return new MouseEvent(e);
	}

	@Override
	public Point getPoint() {
		MouseEvent current = MouseEvent.impl.get(this.id);
		return new Point(current.event.getPoint().x,current.event.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		MouseEvent current = MouseEvent.impl.get(this.id);
		return current.event.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		MouseEvent current = MouseEvent.impl.get(this.id);
		return current.event.isShiftDown();
	}

	public static java.awt.event.MouseEvent getEvent(AbstractEvent e) {
		return MouseEvent.impl.get(e.getID()).event;
	}

	@Override
	public void release(int l) {
		MouseEvent.impl.remove(l);
	}

	@Override
	public int getID() {
		return this.id;
	}

}
