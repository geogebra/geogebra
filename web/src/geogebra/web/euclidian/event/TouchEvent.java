package geogebra.web.euclidian.event;

import java.util.HashMap;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.web.main.Application;

public class TouchEvent extends AbstractEvent {
	
	private static final Integer TOUCHSTART = 1;
	private static final Integer TOUCHMOVE = 2;
	private static final Integer TOUCHEND = 3;
	public static HashMap<Integer, TouchEvent> pool = new HashMap<Integer, TouchEvent>();
	private Touch event;
	private Integer id;
	
	private TouchEvent(Touch touch) {
		this.event = touch;
		this.id  = touch.getIdentifier();
		TouchEvent.pool.put(this.id, this);
	}

	@Override
	public Point getPoint() {
		TouchEvent current = TouchEvent.pool.get(this.id);
		return new Point(current.event.getClientX(),current.event.getClientY());
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
	public void release(int l) {
		TouchEvent.pool.remove(l);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		TouchEvent current = TouchEvent.pool.get(this.id);
		return current.event.getClientX();
	}

	@Override
	public int getY() {
		TouchEvent current = TouchEvent.pool.get(this.id);
		return current.event.getClientY();
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
	public double getWheelRotation() {
		return 0;
	}

	@Override
	public boolean isMiddleClick() {
		return false;
	}

	public static AbstractEvent wrapEvent(Touch touch) {
	   return new TouchEvent(touch);
    }

}
