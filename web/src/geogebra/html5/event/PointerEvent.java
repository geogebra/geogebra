package geogebra.html5.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;

import java.util.LinkedList;

import com.google.gwt.dom.client.NativeEvent;
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
	private boolean shift, control, alt, meta, right, middle;
	private int clickCount = 1;
	private int evID;

	public PointerEvent(double x, double y, PointerEventType type, HasOffsets off) {
		this.off = off;
		this.point = new GPoint((int)Math.round(x), (int)Math.round(y));
		this.type = type;
		this.evID = off.getEvID();
	}

	@Override
	public int getClickCount() {
		return this.clickCount;
	}

	@Override
	public GPoint getPoint() {
		return new GPoint(getX(),getY());
	}

	@Override
	public int getX() {
		if(this.type == PointerEventType.MOUSE){
			return off.mouseEventX(this.point.x);
		}
		return off.touchEventX(this.point.x);
	}

	@Override
	public int getY() {
		if(this.type == PointerEventType.MOUSE){
			return off.mouseEventY(this.point.y);
		}
		return off.touchEventY(this.point.y);
	}

	@Override
	public boolean isAltDown() {
		return this.alt;
	}

	@Override
	public boolean isControlDown() {
		return this.control;
	}

	@Override
	public boolean isMetaDown() {
		return this.meta;
	}

	@Override
	public boolean isMiddleClick() {
		return this.middle;
	}

	@Override
	public boolean isPopupTrigger() {
		return false;
	}

	@Override
	public boolean isRightClick() {
		return this.right;
	}

	@Override
	public boolean isShiftDown() {
		return this.shift;
	}

	@Override
	public void release() {
		if(this.type == PointerEventType.TOUCH){
			this.off.getTouchEventPool().add(this);
		}
	}

	@Override
	public PointerEventType getType() {
		return this.type;
	}
	
	private static PointerEvent wrapEvent(int x, int y,PointerEventType type, HasOffsets h, LinkedList<PointerEvent> pool) {
		if(!pool.isEmpty()){
			PointerEvent wrap = pool.getLast();
			wrap.point = new GPoint(x,y);
			wrap.type = type;
			wrap.evID = h.getEvID();
			pool.removeLast();
			return wrap;
		}
		return new PointerEvent(x, y ,type,h);
	}
	
	public static PointerEvent wrapEvent(NativeEvent nativeEvent,HasOffsets off) {
		PointerEvent evt = wrapEvent(nativeEvent.getClientX(), nativeEvent.getClientY(), off.getDefaultEventType(), 
				 off, off.getMouseEventPool());
		evt.alt = nativeEvent.getAltKey();
		evt.control = nativeEvent.getCtrlKey();
		evt.clickCount = "dblclick".equals(nativeEvent.getType()) ? 2 : 1;
		evt.meta = nativeEvent.getMetaKey();
		evt.middle = nativeEvent.getButton() == NativeEvent.BUTTON_MIDDLE;
		evt.right = nativeEvent.getButton() == NativeEvent.BUTTON_RIGHT;
		evt.shift = nativeEvent.getShiftKey();
		return evt;
	}

	public static AbstractEvent wrapEvent(Touch touch,
            HasOffsets off) {
	    return wrapEvent(touch.getClientX(), touch.getClientY(), 
	    		PointerEventType.TOUCH,  off, off.getTouchEventPool());
    }

	public int getEvID() {
	    return this.evID;
    }

}
