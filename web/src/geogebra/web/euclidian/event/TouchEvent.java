package geogebra.web.euclidian.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;

import java.util.LinkedList;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Touch;

public class TouchEvent extends AbstractEvent {

	private Touch event;
	
	private TouchEvent(Touch touch,HasOffsets h) {
		this.off = h;
		this.event = touch;
	}

	@Override
	public GPoint getPoint() {		
		return new GPoint(getX(),getY());
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
		off.getTouchEventPool().add(this);
	}

	@Override
	public int getX() {		
		return event.getClientX() - off.getXoffset();
	}

	@Override
	public int getY() {
		return event.getClientY() - off.getYoffset();
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
	private HasOffsets off;
	public static AbstractEvent wrapEvent(Touch touch,HasOffsets h) {
		LinkedList<TouchEvent> pool = h.getTouchEventPool();
		if(!pool.isEmpty()){
			TouchEvent wrap = pool.getLast();
			wrap.event = touch;
			pool.removeLast();
			return wrap;
		}
		if (!h.isOffsetsUpToDate()) {
			h.updateOffsets();
		}
		return new TouchEvent(touch,h);
	}

	@Override
    public boolean isPopupTrigger() {
	    return false;
    }

	public EventTarget getTarget() {
	    return event.getTarget();
    }

}
