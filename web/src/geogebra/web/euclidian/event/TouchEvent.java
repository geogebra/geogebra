package geogebra.web.euclidian.event;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;

import java.util.LinkedList;

import com.google.gwt.dom.client.Touch;

public class TouchEvent extends AbstractEvent {
	
	private static final Integer TOUCHSTART = 1;
	private static final Integer TOUCHMOVE = 2;
	private static final Integer TOUCHEND = 3;
	public static LinkedList<TouchEvent> pool = new LinkedList<TouchEvent>();
	private Touch event;
	private Integer id;
	
	private TouchEvent(Touch touch) {
		this.event = touch;
	}

	@Override
	public Point getPoint() {
		
		return new Point(event.getClientX(),event.getClientY());
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
		TouchEvent.pool.remove(this);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		
		return event.getClientX();
	}

	@Override
	public int getY() {
		
		return event.getClientY();
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
		if(!pool.isEmpty()){
			TouchEvent wrap = pool.getLast();
			wrap.event = touch;
			pool.removeLast();
			return wrap;
		}
		return new TouchEvent(touch);
	}

	@Override
    public boolean isPopupTrigger() {
	    return false;
    }

}
