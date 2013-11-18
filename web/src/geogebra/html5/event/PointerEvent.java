package geogebra.html5.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;

import java.util.LinkedList;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Touch;

/**
 * Base implementation of AbstractEvent.
 * 
 * @author Thomas Krismayer
 * 
 */
public class PointerEvent extends AbstractEvent {

	private GPoint point = new GPoint(0, 0);
	private PointerEventType type;
	private HasOffsets off;
	private EventTarget target;

	public PointerEvent(int x, int y, PointerEventType type, HasOffsets off) {
		this.off = off;
		this.point = new GPoint(x, y);
		this.type = type;
	}

	@Override
	public int getClickCount() {
		return 0;
	}

	@Override
	public GPoint getPoint() {
		return this.point;
	}

	@Override
	public double getWheelRotation() {
		return 0;
	}

	@Override
	public int getX() {
		return this.point.x - off.getXoffset();
	}

	@Override
	public int getY() {
		return this.point.y - off.getYoffset();
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public boolean isControlDown() {
		return false;
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
	public boolean isRightClick() {
		return false;
	}

	@Override
	public boolean isShiftDown() {
		return false;
	}

	@Override
	public void release() {
	}

	@Override
	public PointerEventType getType() {
		return this.type;
	}
	
	public EventTarget getTarget(){
		return target;
	}
	
	public static AbstractEvent wrapEvent(int x, int y,PointerEventType type, EventTarget t, HasOffsets h) {
		LinkedList<PointerEvent> pool = h.getTouchEventPool();
		if(!pool.isEmpty()){
			PointerEvent wrap = pool.getLast();
			wrap.point = new GPoint(x,y);
			wrap.type = type;
			wrap.target = t;
			pool.removeLast();
			return wrap;
		}
		if (!h.isOffsetsUpToDate()) {
			h.updateOffsets();
		}
		return new PointerEvent(x, y ,type,h);
	}

	public static AbstractEvent wrapEvent(Touch touch,
            HasOffsets off) {
	    // TODO Auto-generated method stub
	    return wrapEvent(touch.getClientX(), touch.getClientY(), PointerEventType.TOUCH, touch.getTarget(), off);
    }

}
